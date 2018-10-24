/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.supplychain.service;

import com.axelor.apps.account.db.Account;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.PaymentCondition;
import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.account.service.invoice.generator.InvoiceLineGenerator;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Currency;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.PriceListLineRepository;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.SaleOrderLineTax;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderComputeService;
import com.axelor.apps.supplychain.exception.IExceptionMessage;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.apps.supplychain.service.invoice.generator.InvoiceGeneratorSupplyChain;
import com.axelor.apps.supplychain.service.invoice.generator.InvoiceLineGeneratorSupplyChain;
import com.axelor.db.JPA;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaleOrderInvoiceServiceImpl implements SaleOrderInvoiceService {

  private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected AppSupplychainService appSupplychainService;

  protected SaleOrderRepository saleOrderRepo;

  protected InvoiceRepository invoiceRepo;

  protected InvoiceService invoiceService;

  @Inject
  public SaleOrderInvoiceServiceImpl(
      AppSupplychainService appSupplychainService,
      SaleOrderRepository saleOrderRepo,
      InvoiceRepository invoiceRepo,
      InvoiceService invoiceService) {

    this.appSupplychainService = appSupplychainService;

    this.saleOrderRepo = saleOrderRepo;
    this.invoiceRepo = invoiceRepo;
    this.invoiceService = invoiceService;
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public Invoice generateInvoice(
      SaleOrder saleOrder,
      int operationSelect,
      BigDecimal amount,
      boolean isPercent,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {

    Invoice invoice;
    switch (operationSelect) {
      case SaleOrderRepository.INVOICE_ALL:
        invoice = generateInvoice(saleOrder);
        break;

      case SaleOrderRepository.INVOICE_PART:
        invoice = generatePartialInvoice(saleOrder, amount, isPercent);
        break;

      case SaleOrderRepository.INVOICE_LINES:
        invoice = generateInvoiceFromLines(saleOrder, qtyToInvoiceMap, isPercent);
        break;

      case SaleOrderRepository.INVOICE_ADVANCE_PAYMENT:
        invoice = generateAdvancePayment(saleOrder, amount, isPercent);
        break;

      default:
        return null;
    }
    invoice.setSaleOrder(saleOrder);

    // fill default advance payment invoice
    if (invoice.getOperationSubTypeSelect() != InvoiceRepository.OPERATION_SUB_TYPE_ADVANCE) {
      invoice.setAdvancePaymentInvoiceSet(invoiceService.getDefaultAdvancePaymentInvoice(invoice));
    }
    return invoiceRepo.save(invoice);
  }

  @Override
  public BigDecimal computeAmountToInvoicePercent(
      SaleOrder saleOrder, BigDecimal amount, boolean isPercent) throws AxelorException {
    BigDecimal total = Beans.get(SaleOrderComputeService.class).getTotalSaleOrderPrice(saleOrder);
    if (!isPercent) {
      amount = amount.multiply(new BigDecimal("100")).divide(total, 4, RoundingMode.HALF_EVEN);
    }
    if (amount.compareTo(new BigDecimal("100")) > 0) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(IExceptionMessage.SO_INVOICE_QTY_MAX));
    }

    return amount;
  }

  @Override
  public Invoice generatePartialInvoice(
      SaleOrder saleOrder, BigDecimal amountToInvoice, boolean isPercent) throws AxelorException {
    List<SaleOrderLineTax> taxLineList = saleOrder.getSaleOrderLineTaxList();

    BigDecimal percentToInvoice =
        computeAmountToInvoicePercent(saleOrder, amountToInvoice, isPercent);
    Product invoicingProduct =
        Beans.get(AccountConfigService.class)
            .getAccountConfig(saleOrder.getCompany())
            .getInvoicingProduct();
    if (invoicingProduct == null) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.SO_INVOICE_MISSING_INVOICING_PRODUCT));
    }
    Invoice invoice =
        createInvoiceAndLines(
            saleOrder,
            taxLineList,
            invoicingProduct,
            percentToInvoice,
            InvoiceRepository.OPERATION_SUB_TYPE_DEFAULT,
            null);
    return invoiceRepo.save(invoice);
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public Invoice generateAdvancePayment(
      SaleOrder saleOrder, BigDecimal amountToInvoice, boolean isPercent) throws AxelorException {
    List<SaleOrderLineTax> taxLineList = saleOrder.getSaleOrderLineTaxList();
    AccountConfigService accountConfigService = Beans.get(AccountConfigService.class);

    BigDecimal percentToInvoice =
        computeAmountToInvoicePercent(saleOrder, amountToInvoice, isPercent);
    Product invoicingProduct =
        accountConfigService.getAccountConfig(saleOrder.getCompany()).getAdvancePaymentProduct();
    Account advancePaymentAccount =
        accountConfigService.getAccountConfig(saleOrder.getCompany()).getAdvancePaymentAccount();
    if (invoicingProduct == null) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.SO_INVOICE_MISSING_ADVANCE_PAYMENT_PRODUCT));
    }
    if (advancePaymentAccount == null) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.SO_INVOICE_MISSING_ADVANCE_PAYMENT_ACCOUNT),
          saleOrder.getCompany().getName());
    }

    Invoice invoice =
        createInvoiceAndLines(
            saleOrder,
            taxLineList,
            invoicingProduct,
            percentToInvoice,
            InvoiceRepository.OPERATION_SUB_TYPE_ADVANCE,
            advancePaymentAccount);

    // no need for link to sale order lines for an advance payment
    if (invoice.getInvoiceLineList() != null) {
      invoice.getInvoiceLineList().forEach(invoiceLine -> invoiceLine.setSaleOrderLine(null));
    }

    return invoiceRepo.save(invoice);
  }

  public Invoice createInvoiceAndLines(
      SaleOrder saleOrder,
      List<SaleOrderLineTax> taxLineList,
      Product invoicingProduct,
      BigDecimal percentToInvoice,
      int operationSubTypeSelect,
      Account partnerAccount)
      throws AxelorException {
    InvoiceGenerator invoiceGenerator = this.createInvoiceGenerator(saleOrder);

    Invoice invoice = invoiceGenerator.generate();

    invoiceGenerator.populate(
        invoice,
        this.createInvoiceLinesFromTax(invoice, taxLineList, invoicingProduct, percentToInvoice));
    this.fillInLines(invoice);

    invoice.setAddressStr(saleOrder.getMainInvoicingAddressStr());

    invoice.setOperationSubTypeSelect(operationSubTypeSelect);

    if (partnerAccount != null) {
      invoice.setPartnerAccount(partnerAccount);
    }

    return invoice;
  }

  @Override
  public List<InvoiceLine> createInvoiceLinesFromTax(
      Invoice invoice,
      List<SaleOrderLineTax> taxLineList,
      Product invoicingProduct,
      BigDecimal percentToInvoice)
      throws AxelorException {

    List<InvoiceLine> createdInvoiceLineList = new ArrayList<>();
    if (taxLineList != null) {
      for (SaleOrderLineTax saleOrderLineTax : taxLineList) {
        BigDecimal lineAmountToInvoice =
            percentToInvoice
                .multiply(saleOrderLineTax.getExTaxBase())
                .divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_EVEN);
        TaxLine taxLine = saleOrderLineTax.getTaxLine();
        BigDecimal lineAmountToInvoiceInclTax =
            (taxLine != null)
                ? lineAmountToInvoice.add(lineAmountToInvoice.multiply(taxLine.getValue()))
                : lineAmountToInvoice;

        InvoiceLineGenerator invoiceLineGenerator =
            new InvoiceLineGenerator(
                invoice,
                invoicingProduct,
                invoicingProduct.getName(),
                lineAmountToInvoice,
                lineAmountToInvoiceInclTax,
                invoice.getInAti() ? lineAmountToInvoiceInclTax : lineAmountToInvoice,
                invoicingProduct.getDescription(),
                BigDecimal.ONE,
                invoicingProduct.getUnit(),
                taxLine,
                InvoiceLineGenerator.DEFAULT_SEQUENCE,
                BigDecimal.ZERO,
                PriceListLineRepository.AMOUNT_TYPE_NONE,
                lineAmountToInvoice,
                null,
                false) {
              @Override
              public List<InvoiceLine> creates() throws AxelorException {

                InvoiceLine invoiceLine = this.createInvoiceLine();

                List<InvoiceLine> invoiceLines = new ArrayList<>();
                invoiceLines.add(invoiceLine);

                return invoiceLines;
              }
            };

        List<InvoiceLine> invoiceOneLineList = invoiceLineGenerator.creates();
        // link to the created invoice line the first line of the sale order.
        for (InvoiceLine invoiceLine : invoiceOneLineList) {
          SaleOrderLine saleOrderLine =
              saleOrderLineTax.getSaleOrder().getSaleOrderLineList().get(0);
          invoiceLine.setSaleOrderLine(saleOrderLine);
        }
        createdInvoiceLineList.addAll(invoiceOneLineList);
      }
    }
    return createdInvoiceLineList;
  }

  @Override
  public Invoice generateInvoiceFromLines(
      SaleOrder saleOrder, Map<Long, BigDecimal> qtyToInvoiceMap, boolean isPercent)
      throws AxelorException {

    if (qtyToInvoiceMap.isEmpty()) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(IExceptionMessage.SO_INVOICE_NO_LINES_SELECTED));
    }

    for (SaleOrderLine saleOrderLine : saleOrder.getSaleOrderLineList()) {
      Long SOrderId = saleOrderLine.getId();
      if (qtyToInvoiceMap.containsKey(SOrderId)) {
        if (isPercent) {
          BigDecimal percent = qtyToInvoiceMap.get(SOrderId);
          BigDecimal realQty =
              saleOrderLine
                  .getQty()
                  .multiply(percent)
                  .divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN);
          qtyToInvoiceMap.put(SOrderId, realQty);
        }
        if (qtyToInvoiceMap.get(SOrderId).compareTo(saleOrderLine.getQty()) > 0) {
          throw new AxelorException(
              saleOrder,
              TraceBackRepository.CATEGORY_INCONSISTENCY,
              I18n.get(IExceptionMessage.SO_INVOICE_QTY_MAX));
        }
      }
    }
    return this.generateInvoice(saleOrder, saleOrder.getSaleOrderLineList(), qtyToInvoiceMap);
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public Invoice generateInvoice(SaleOrder saleOrder) throws AxelorException {

    Invoice invoice = this.createInvoice(saleOrder);

    invoiceRepo.save(invoice);

    saleOrderRepo.save(fillSaleOrder(saleOrder, invoice));

    return invoice;
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public Invoice generateInvoice(SaleOrder saleOrder, List<SaleOrderLine> saleOrderLinesSelected)
      throws AxelorException {

    Invoice invoice = this.createInvoice(saleOrder, saleOrderLinesSelected);

    invoiceRepo.save(invoice);

    saleOrderRepo.save(fillSaleOrder(saleOrder, invoice));

    return invoice;
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public Invoice generateInvoice(
      SaleOrder saleOrder,
      List<SaleOrderLine> saleOrderLinesSelected,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {

    Invoice invoice = this.createInvoice(saleOrder, saleOrderLinesSelected, qtyToInvoiceMap);
    invoiceRepo.save(invoice);

    saleOrderRepo.save(fillSaleOrder(saleOrder, invoice));

    return invoice;
  }

  public BigDecimal computeInTaxTotalInvoiced(Invoice invoice) {

    BigDecimal total = BigDecimal.ZERO;

    if (invoice.getStatusSelect() == InvoiceRepository.STATUS_VENTILATED) {
      if (invoice.getOperationTypeSelect() == InvoiceRepository.OPERATION_TYPE_CLIENT_SALE) {
        total = total.add(invoice.getInTaxTotal());
      }
      if (invoice.getOperationTypeSelect() == InvoiceRepository.OPERATION_TYPE_CLIENT_REFUND) {
        total = total.subtract(invoice.getInTaxTotal());
      }
    }

    if (invoice.getRefundInvoiceList() != null) {
      for (Invoice refund : invoice.getRefundInvoiceList()) {
        total = total.add(this.computeInTaxTotalInvoiced(refund));
      }
    }

    return total;
  }

  @Override
  public SaleOrder fillSaleOrder(SaleOrder saleOrder, Invoice invoice) {

    saleOrder.setOrderDate(appSupplychainService.getTodayDate());

    // TODO Créer une séquence pour les commandes (Porter sur la facture ?)
    //		saleOrder.setOrderNumber();

    return saleOrder;
  }

  @Override
  public Invoice createInvoice(SaleOrder saleOrder) throws AxelorException {

    return createInvoice(saleOrder, saleOrder.getSaleOrderLineList());
  }

  @Override
  public Invoice createInvoice(SaleOrder saleOrder, List<SaleOrderLine> saleOrderLineList)
      throws AxelorException {
    Map<Long, BigDecimal> qtyToInvoiceMap = new HashMap<>();
    for (SaleOrderLine saleOrderLine : saleOrderLineList) {
      qtyToInvoiceMap.put(saleOrderLine.getId(), saleOrderLine.getQty());
    }
    return createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);
  }

  @Override
  public Invoice createInvoice(
      SaleOrder saleOrder,
      List<SaleOrderLine> saleOrderLineList,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {

    InvoiceGenerator invoiceGenerator = this.createInvoiceGenerator(saleOrder);

    Invoice invoice = invoiceGenerator.generate();

    invoiceGenerator.populate(
        invoice, this.createInvoiceLines(invoice, saleOrderLineList, qtyToInvoiceMap));
    this.fillInLines(invoice);

    invoice.setAddressStr(saleOrder.getMainInvoicingAddressStr());

    return invoice;
  }

  @Override
  public InvoiceGenerator createInvoiceGenerator(SaleOrder saleOrder) throws AxelorException {
    return createInvoiceGenerator(saleOrder, false);
  }

  @Override
  public InvoiceGenerator createInvoiceGenerator(SaleOrder saleOrder, boolean isRefund)
      throws AxelorException {
    if (saleOrder.getCurrency() == null) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.SO_INVOICE_6),
          saleOrder.getSaleOrderSeq());
    }

    return new InvoiceGeneratorSupplyChain(saleOrder, isRefund) {
      @Override
      public Invoice generate() throws AxelorException {
        return super.createInvoiceHeader();
      }
    };
  }

  // TODO ajouter tri sur les séquences
  @Override
  public List<InvoiceLine> createInvoiceLines(
      Invoice invoice, List<SaleOrderLine> saleOrderLineList, Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {

    List<InvoiceLine> invoiceLineList = new ArrayList<>();
    for (SaleOrderLine saleOrderLine : saleOrderLineList) {

      if (qtyToInvoiceMap.containsKey(saleOrderLine.getId())) {
        invoiceLineList.addAll(
            this.createInvoiceLine(
                invoice, saleOrderLine, qtyToInvoiceMap.get(saleOrderLine.getId())));

        saleOrderLine.setInvoiced(true);
      }
    }

    return invoiceLineList;
  }

  @Override
  public List<InvoiceLine> createInvoiceLine(
      Invoice invoice, SaleOrderLine saleOrderLine, BigDecimal qtyToInvoice)
      throws AxelorException {

    Product product = saleOrderLine.getProduct();

    InvoiceLineGenerator invoiceLineGenerator =
        new InvoiceLineGeneratorSupplyChain(
            invoice,
            product,
            saleOrderLine.getProductName(),
            saleOrderLine.getDescription(),
            qtyToInvoice,
            saleOrderLine.getUnit(),
            saleOrderLine.getSequence(),
            false,
            saleOrderLine,
            null,
            null) {

          @Override
          public List<InvoiceLine> creates() throws AxelorException {

            InvoiceLine invoiceLine = this.createInvoiceLine();

            List<InvoiceLine> invoiceLines = new ArrayList<>();
            invoiceLines.add(invoiceLine);

            return invoiceLines;
          }
        };

    return invoiceLineGenerator.creates();
  }

  @Override
  public void updateAndCheckInvoicedAmount(
      SaleOrder saleOrder, Long currentInvoiceId, boolean excludeCurrentInvoice)
      throws AxelorException {

    update(saleOrder, currentInvoiceId, excludeCurrentInvoice, true);
  }

  @Override
  public void update(SaleOrder saleOrder, Long currentInvoiceId, boolean excludeCurrentInvoice)
      throws AxelorException {

    update(saleOrder, currentInvoiceId, excludeCurrentInvoice, false);
  }

  protected void update(
      SaleOrder saleOrder,
      Long currentInvoiceId,
      boolean excludeCurrentInvoice,
      boolean checkInvoicedAmount)
      throws AxelorException {

    BigDecimal amountInvoiced =
        this.getInvoicedAmount(saleOrder, currentInvoiceId, excludeCurrentInvoice);
    if (checkInvoicedAmount && amountInvoiced.compareTo(saleOrder.getExTaxTotal()) > 0) {
      throw new AxelorException(
          saleOrder,
          TraceBackRepository.TYPE_FUNCTIONNAL,
          I18n.get(IExceptionMessage.SO_INVOICE_TOO_MUCH_INVOICED),
          saleOrder.getSaleOrderSeq());
    }
    saleOrder.setAmountInvoiced(amountInvoiced);
  }

  @Override
  public BigDecimal getInvoicedAmount(SaleOrder saleOrder) {
    return this.getInvoicedAmount(saleOrder, null, true);
  }

  /**
   * Return the remaining amount to invoice for the saleOrder in parameter
   *
   * @param saleOrder
   * @param currentInvoiceId In the case of invoice ventilation or cancellation, the invoice status
   *     isn't modify in database but it will be integrated in calculation For ventilation, the
   *     invoice should be integrated in calculation For cancellation, the invoice shouldn't be
   *     integrated in calculation
   * @param includeInvoice To know if the invoice should be or not integrated in calculation
   */
  @Override
  public BigDecimal getInvoicedAmount(
      SaleOrder saleOrder, Long currentInvoiceId, boolean excludeCurrentInvoice) {

    BigDecimal invoicedAmount = BigDecimal.ZERO;

    BigDecimal saleAmount =
        this.getAmountVentilated(
            saleOrder,
            currentInvoiceId,
            excludeCurrentInvoice,
            InvoiceRepository.OPERATION_TYPE_CLIENT_SALE);
    BigDecimal refundAmount =
        this.getAmountVentilated(
            saleOrder,
            currentInvoiceId,
            excludeCurrentInvoice,
            InvoiceRepository.OPERATION_TYPE_CLIENT_REFUND);

    if (saleAmount != null) {
      invoicedAmount = invoicedAmount.add(saleAmount);
    }
    if (refundAmount != null) {
      invoicedAmount = invoicedAmount.subtract(refundAmount);
    }

    if (!saleOrder.getCurrency().equals(saleOrder.getCompany().getCurrency())
        && saleOrder.getCompanyExTaxTotal().compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal rate =
          invoicedAmount.divide(saleOrder.getCompanyExTaxTotal(), 4, RoundingMode.HALF_UP);
      invoicedAmount = saleOrder.getExTaxTotal().multiply(rate);
    }

    log.debug(
        "Compute the invoiced amount ({}) of the sale order : {}",
        invoicedAmount,
        saleOrder.getSaleOrderSeq());

    return invoicedAmount;
  }

  @Override
  public List<Invoice> getInvoices(SaleOrder saleOrder) {
    return invoiceRepo
        .all()
        .filter(
            "self.saleOrder.id = ? OR (self.saleOrder.id IS NULL AND EXISTS(SELECT 1 FROM self.invoiceLineList inli WHERE inli.saleOrderLine.id IN (?)))",
            saleOrder.getId(),
            saleOrder
                .getSaleOrderLineList()
                .stream()
                .map(SaleOrderLine::getId)
                .collect(Collectors.toList()))
        .fetch();
  }

  private BigDecimal getAmountVentilated(
      SaleOrder saleOrder,
      Long currentInvoiceId,
      boolean excludeCurrentInvoice,
      int invoiceOperationTypeSelect) {

    String query = "SELECT SUM(self.companyExTaxTotal)" + " FROM InvoiceLine as self";

    if (appSupplychainService.getAppSupplychain().getManageInvoicedAmountByLine()) {
      query += " WHERE self.saleOrderLine.saleOrder.id = :saleOrderId";
    } else {
      query += " WHERE self.saleOrder.id = :saleOrderId";
    }

    query +=
        " AND self.invoice.operationTypeSelect = :invoiceOperationTypeSelect"
            + " AND self.invoice.statusSelect = :statusVentilated";

    // exclude invoices that are advance payments
    boolean invoiceIsNotAdvancePayment =
        (currentInvoiceId != null
            && invoiceRepo.find(currentInvoiceId).getOperationSubTypeSelect()
                != InvoiceRepository.OPERATION_SUB_TYPE_ADVANCE);

    if (invoiceIsNotAdvancePayment) {
      if (excludeCurrentInvoice) {
        query += " AND self.invoice.id <> :invoiceId";
      } else {
        query +=
            " OR (self.invoice.id = :invoiceId AND self.invoice.operationTypeSelect = :invoiceOperationTypeSelect) ";
      }
    }

    Query q = JPA.em().createQuery(query, BigDecimal.class);

    q.setParameter("saleOrderId", saleOrder.getId());
    q.setParameter("statusVentilated", InvoiceRepository.STATUS_VENTILATED);
    q.setParameter("invoiceOperationTypeSelect", invoiceOperationTypeSelect);
    if (invoiceIsNotAdvancePayment) {
      q.setParameter("invoiceId", currentInvoiceId);
    }

    BigDecimal invoicedAmount = (BigDecimal) q.getSingleResult();

    if (invoicedAmount != null) {
      return invoicedAmount;
    } else {
      return BigDecimal.ZERO;
    }
  }

  @Override
  public void fillInLines(Invoice invoice) {
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    if (invoiceLineList != null) {
      for (InvoiceLine invoiceLine : invoiceLineList) {
        invoiceLine.setSaleOrder(invoice.getSaleOrder());
      }
    }
  }

  @Override
  @Transactional
  public Invoice mergeInvoice(
      List<Invoice> invoiceList,
      Company company,
      Currency currency,
      Partner partner,
      Partner contactPartner,
      PriceList priceList,
      PaymentMode paymentMode,
      PaymentCondition paymentCondition,
      SaleOrder saleOrder)
      throws AxelorException {
    log.debug("service supplychain 1 (saleOrder) {}", saleOrder);
    if (saleOrder != null) {
      String numSeq = "";
      String externalRef = "";

      for (Invoice invoiceLocal : invoiceList) {
        if (!numSeq.isEmpty()) {
          numSeq += "-";
        }
        if (invoiceLocal.getInternalReference() != null) {
          numSeq += invoiceLocal.getInternalReference();
        }

        if (!externalRef.isEmpty()) {
          externalRef += "|";
        }
        if (invoiceLocal.getExternalReference() != null) {
          externalRef += invoiceLocal.getExternalReference();
        }
      }
      InvoiceGenerator invoiceGenerator = this.createInvoiceGenerator(saleOrder);
      Invoice invoiceMerged = invoiceGenerator.generate();
      invoiceMerged.setExternalReference(externalRef);
      invoiceMerged.setInternalReference(numSeq);

      if (paymentMode != null) invoiceMerged.setPaymentMode(paymentMode);
      if (paymentCondition != null) invoiceMerged.setPaymentCondition(paymentCondition);

      List<InvoiceLine> invoiceLines = invoiceService.getInvoiceLinesFromInvoiceList(invoiceList);
      invoiceGenerator.populate(invoiceMerged, invoiceLines);
      invoiceService.setInvoiceForInvoiceLines(invoiceLines, invoiceMerged);
      if (!appSupplychainService.getAppSupplychain().getManageInvoicedAmountByLine()) {
        this.fillInLines(invoiceMerged);
      } else {
        invoiceMerged.setSaleOrder(null);
      }
      invoiceRepo.save(invoiceMerged);
      invoiceService.deleteOldInvoices(invoiceList);
      return invoiceMerged;
    } else {
      if (!appSupplychainService.getAppSupplychain().getManageInvoicedAmountByLine()) {
        Invoice invoiceMerged =
            invoiceService.mergeInvoice(
                invoiceList,
                company,
                currency,
                partner,
                contactPartner,
                priceList,
                paymentMode,
                paymentCondition);
        this.fillInLines(invoiceMerged);
        return invoiceMerged;
      } else {
        return invoiceService.mergeInvoice(
            invoiceList,
            company,
            currency,
            partner,
            contactPartner,
            priceList,
            paymentMode,
            paymentCondition);
      }
    }
  }

  @Override
  public BigDecimal getInTaxInvoicedAmount(SaleOrder saleOrder) {
    BigDecimal exTaxTotal = saleOrder.getExTaxTotal();
    BigDecimal inTaxTotal = saleOrder.getInTaxTotal();

    BigDecimal exTaxAmountInvoiced = saleOrder.getAmountInvoiced();
    if (exTaxTotal.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    } else {
      return inTaxTotal
          .multiply(exTaxAmountInvoiced)
          .divide(exTaxTotal, 2, BigDecimal.ROUND_HALF_EVEN);
    }
  }

  @Override
  public Map<String, Integer> getInvoicingWizardOperationDomain(SaleOrder saleOrder) {
    boolean manageAdvanceInvoice =
        Beans.get(AppAccountService.class).getAppAccount().getManageAdvancePaymentInvoice();
    BigDecimal amountInvoiced = saleOrder.getAmountInvoiced();
    Map<String, Integer> contextValues = new HashMap<>();

    contextValues.put(
        "invoiceAll",
        amountInvoiced.compareTo(BigDecimal.ZERO) == 0 ? SaleOrderRepository.INVOICE_ALL : 0);
    contextValues.put("invoiceFraction", SaleOrderRepository.INVOICE_PART);
    contextValues.put("invoiceLines", SaleOrderRepository.INVOICE_LINES);
    contextValues.put(
        "invoiceAdvPayment",
        manageAdvanceInvoice ? SaleOrderRepository.INVOICE_ADVANCE_PAYMENT : 0);
    return contextValues;
  }
}
