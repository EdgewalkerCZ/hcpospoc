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

import com.axelor.apps.account.db.AccountingSituation;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.AccountingSituationService;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.account.service.payment.PaymentModeService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.PriceListRepository;
import com.axelor.apps.base.service.AddressService;
import com.axelor.apps.base.service.PartnerPriceListService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.purchase.service.PurchaseOrderLineService;
import com.axelor.apps.purchase.service.PurchaseOrderService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderComputeService;
import com.axelor.apps.sale.service.saleorder.SaleOrderCreateService;
import com.axelor.apps.supplychain.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;
import java.util.List;
import java.util.Set;

public class IntercoServiceImpl implements IntercoService {

  @Override
  @Transactional
  public SaleOrder generateIntercoSaleFromPurchase(PurchaseOrder purchaseOrder)
      throws AxelorException {

    SaleOrderCreateService saleOrderCreateService = Beans.get(SaleOrderCreateService.class);
    SaleOrderComputeService saleOrderComputeService = Beans.get(SaleOrderComputeService.class);

    // create sale order
    SaleOrder saleOrder =
        saleOrderCreateService.createSaleOrder(
            null,
            findIntercoCompany(purchaseOrder.getSupplierPartner()),
            purchaseOrder.getContactPartner(),
            purchaseOrder.getCurrency(),
            purchaseOrder.getDeliveryDate(),
            null,
            null,
            purchaseOrder.getOrderDate(),
            purchaseOrder.getPriceList(),
            purchaseOrder.getCompany().getPartner(),
            null);

    // copy date
    saleOrder.setOrderDate(purchaseOrder.getOrderDate());

    // copy payments
    PaymentMode intercoPaymentMode =
        Beans.get(PaymentModeService.class).reverseInOut(purchaseOrder.getPaymentMode());
    saleOrder.setPaymentMode(intercoPaymentMode);
    saleOrder.setPaymentCondition(purchaseOrder.getPaymentCondition());

    // copy delivery info
    saleOrder.setDeliveryDate(purchaseOrder.getDeliveryDate());
    saleOrder.setStockLocation(purchaseOrder.getStockLocation());
    saleOrder.setShipmentMode(purchaseOrder.getShipmentMode());
    saleOrder.setFreightCarrierMode(purchaseOrder.getFreightCarrierMode());

    // copy timetable info
    saleOrder.setExpectedRealisationDate(purchaseOrder.getExpectedRealisationDate());
    saleOrder.setAmountToBeSpreadOverTheTimetable(
        purchaseOrder.getAmountToBeSpreadOverTheTimetable());

    // create lines
    List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrder.getPurchaseOrderLineList();
    if (purchaseOrderLineList != null) {
      for (PurchaseOrderLine purchaseOrderLine : purchaseOrderLineList) {
        this.createIntercoSaleLineFromPurchaseLine(purchaseOrderLine, saleOrder);
      }
    }

    // compute the sale order
    saleOrderComputeService.computeSaleOrder(saleOrder);

    saleOrder.setCreatedByInterco(true);
    return Beans.get(SaleOrderRepository.class).save(saleOrder);
  }

  @Override
  @Transactional
  public PurchaseOrder generateIntercoPurchaseFromSale(SaleOrder saleOrder) throws AxelorException {

    PurchaseOrderService purchaseOrderService = Beans.get(PurchaseOrderService.class);

    // create purchase order
    PurchaseOrder purchaseOrder;
    purchaseOrder =
        purchaseOrderService.createPurchaseOrder(
            null,
            findIntercoCompany(saleOrder.getClientPartner()),
            saleOrder.getContactPartner(),
            saleOrder.getCurrency(),
            saleOrder.getDeliveryDate(),
            null,
            null,
            saleOrder.getOrderDate(),
            saleOrder.getPriceList(),
            saleOrder.getCompany().getPartner(),
            saleOrder.getTradingName());
    // copy date
    purchaseOrder.setOrderDate(saleOrder.getOrderDate());

    // copy payments
    PaymentMode intercoPaymentMode =
        Beans.get(PaymentModeService.class).reverseInOut(saleOrder.getPaymentMode());
    purchaseOrder.setPaymentMode(intercoPaymentMode);
    purchaseOrder.setPaymentCondition(saleOrder.getPaymentCondition());

    // copy delivery info
    purchaseOrder.setDeliveryDate(saleOrder.getDeliveryDate());
    purchaseOrder.setStockLocation(saleOrder.getStockLocation());
    purchaseOrder.setShipmentMode(saleOrder.getShipmentMode());
    purchaseOrder.setFreightCarrierMode(saleOrder.getFreightCarrierMode());

    // copy timetable info
    purchaseOrder.setExpectedRealisationDate(saleOrder.getExpectedRealisationDate());
    purchaseOrder.setAmountToBeSpreadOverTheTimetable(
        saleOrder.getAmountToBeSpreadOverTheTimetable());

    // create lines
    List<SaleOrderLine> saleOrderLineList = saleOrder.getSaleOrderLineList();
    if (saleOrderLineList != null) {
      for (SaleOrderLine saleOrderLine : saleOrderLineList) {
        this.createIntercoPurchaseLineFromSaleLine(saleOrderLine, purchaseOrder);
      }
    }

    // compute the purchase order
    purchaseOrderService.computePurchaseOrder(purchaseOrder);

    purchaseOrder.setCreatedByInterco(true);
    return Beans.get(PurchaseOrderRepository.class).save(purchaseOrder);
  }

  /**
   * @param saleOrderLine the sale order line needed to create the purchase order line
   * @param purchaseOrder the purchase order line belongs to this purchase order
   * @return the created purchase order line
   */
  protected PurchaseOrderLine createIntercoPurchaseLineFromSaleLine(
      SaleOrderLine saleOrderLine, PurchaseOrder purchaseOrder) throws AxelorException {
    PurchaseOrderLine purchaseOrderLine =
        Beans.get(PurchaseOrderLineService.class)
            .createPurchaseOrderLine(
                purchaseOrder,
                saleOrderLine.getProduct(),
                saleOrderLine.getProductName(),
                saleOrderLine.getDescription(),
                saleOrderLine.getQty(),
                saleOrderLine.getUnit());
    // compute amount
    purchaseOrderLine.setPrice(saleOrderLine.getPrice());
    purchaseOrderLine.setInTaxPrice(saleOrderLine.getInTaxPrice());
    purchaseOrderLine.setExTaxTotal(saleOrderLine.getExTaxTotal());
    purchaseOrderLine.setDiscountTypeSelect(saleOrderLine.getDiscountTypeSelect());
    purchaseOrderLine.setDiscountAmount(saleOrderLine.getDiscountAmount());

    // delivery
    purchaseOrderLine.setEstimatedDelivDate(saleOrderLine.getEstimatedDelivDate());

    // tax
    purchaseOrderLine.setTaxLine(saleOrderLine.getTaxLine());

    purchaseOrder.addPurchaseOrderLineListItem(purchaseOrderLine);
    return purchaseOrderLine;
  }

  /**
   * @param purchaseOrderLine the purchase order line needed to create the sale order line
   * @param saleOrder the sale order line belongs to this purchase order
   * @return the created purchase order line
   */
  protected SaleOrderLine createIntercoSaleLineFromPurchaseLine(
      PurchaseOrderLine purchaseOrderLine, SaleOrder saleOrder) {
    SaleOrderLine saleOrderLine = new SaleOrderLine();

    saleOrderLine.setSaleOrder(saleOrder);
    saleOrderLine.setProduct(purchaseOrderLine.getProduct());
    saleOrderLine.setProductName(purchaseOrderLine.getProductName());

    saleOrderLine.setDescription(purchaseOrderLine.getDescription());
    saleOrderLine.setQty(purchaseOrderLine.getQty());
    saleOrderLine.setUnit(purchaseOrderLine.getUnit());

    // compute amount
    saleOrderLine.setPrice(purchaseOrderLine.getPrice());
    saleOrderLine.setInTaxPrice(purchaseOrderLine.getInTaxPrice());
    saleOrderLine.setExTaxTotal(purchaseOrderLine.getExTaxTotal());
    saleOrderLine.setDiscountTypeSelect(purchaseOrderLine.getDiscountTypeSelect());
    saleOrderLine.setDiscountAmount(purchaseOrderLine.getDiscountAmount());

    // delivery
    saleOrderLine.setEstimatedDelivDate(purchaseOrderLine.getEstimatedDelivDate());

    // tax
    saleOrderLine.setTaxLine(purchaseOrderLine.getTaxLine());

    saleOrder.addSaleOrderLineListItem(saleOrderLine);
    return saleOrderLine;
  }

  @Override
  public Invoice generateIntercoInvoice(Invoice invoice) throws AxelorException {
    PartnerService partnerService = Beans.get(PartnerService.class);
    InvoiceRepository invoiceRepository = Beans.get(InvoiceRepository.class);
    InvoiceService invoiceService = Beans.get(InvoiceService.class);
    // set the status
    int generatedOperationTypeSelect;
    int priceListRepositoryType;
    switch (invoice.getOperationTypeSelect()) {
      case InvoiceRepository.OPERATION_TYPE_SUPPLIER_PURCHASE:
        generatedOperationTypeSelect = InvoiceRepository.OPERATION_TYPE_CLIENT_SALE;
        priceListRepositoryType = PriceListRepository.TYPE_SALE;
        break;
      case InvoiceRepository.OPERATION_TYPE_SUPPLIER_REFUND:
        generatedOperationTypeSelect = InvoiceRepository.OPERATION_TYPE_CLIENT_REFUND;
        priceListRepositoryType = PriceListRepository.TYPE_SALE;
        break;
      case InvoiceRepository.OPERATION_TYPE_CLIENT_SALE:
        generatedOperationTypeSelect = InvoiceRepository.OPERATION_TYPE_SUPPLIER_PURCHASE;
        priceListRepositoryType = PriceListRepository.TYPE_PURCHASE;
        break;
      case InvoiceRepository.OPERATION_TYPE_CLIENT_REFUND:
        generatedOperationTypeSelect = InvoiceRepository.OPERATION_TYPE_SUPPLIER_REFUND;
        priceListRepositoryType = PriceListRepository.TYPE_PURCHASE;
        break;
      default:
        throw new AxelorException(
            TraceBackRepository.CATEGORY_MISSING_FIELD,
            I18n.get(IExceptionMessage.INVOICE_MISSING_TYPE),
            invoice);
    }
    Company intercoCompany = findIntercoCompany(invoice.getPartner());
    Partner intercoPartner = invoice.getCompany().getPartner();
    PaymentMode intercoPaymentMode =
        Beans.get(PaymentModeService.class).reverseInOut(invoice.getPaymentMode());
    Address intercoAddress = partnerService.getInvoicingAddress(intercoPartner);
    BankDetails intercoBankDetails = partnerService.getDefaultBankDetails(intercoPartner);
    AccountingSituation accountingSituation =
        Beans.get(AccountingSituationService.class)
            .getAccountingSituation(intercoPartner, intercoCompany);
    PriceList intercoPriceList =
        Beans.get(PartnerPriceListService.class)
            .getDefaultPriceList(intercoPartner, priceListRepositoryType);

    Invoice intercoInvoice = invoiceRepository.copy(invoice, true);

    intercoInvoice.setOperationTypeSelect(generatedOperationTypeSelect);
    intercoInvoice.setCompany(intercoCompany);
    intercoInvoice.setPartner(intercoPartner);
    intercoInvoice.setAddress(intercoAddress);
    intercoInvoice.setAddressStr(Beans.get(AddressService.class).computeAddressStr(intercoAddress));
    intercoInvoice.setPaymentMode(intercoPaymentMode);
    intercoInvoice.setBankDetails(intercoBankDetails);
    Set<Invoice> invoices = invoiceService.getDefaultAdvancePaymentInvoice(intercoInvoice);
    intercoInvoice.setAdvancePaymentInvoiceSet(invoices);
    if (accountingSituation != null) {
      intercoInvoice.setInvoiceAutomaticMail(accountingSituation.getInvoiceAutomaticMail());
      intercoInvoice.setInvoiceMessageTemplate(accountingSituation.getInvoiceMessageTemplate());
    }
    intercoInvoice.setPriceList(intercoPriceList);
    intercoInvoice.setInvoicesCopySelect(intercoPartner.getInvoicesCopySelect());
    intercoInvoice.setCreatedByInterco(true);
    intercoInvoice.setInterco(false);
    if (intercoInvoice.getInvoiceLineList() != null) {
      intercoInvoice
          .getInvoiceLineList()
          .forEach(invoiceLine -> invoiceLine.setInvoice(intercoInvoice));
    }
    if (intercoInvoice.getInvoiceLineTaxList() != null) {
      intercoInvoice
          .getInvoiceLineTaxList()
          .forEach(invoiceTaxLine -> invoiceTaxLine.setInvoice(intercoInvoice));
    }

    return invoiceRepository.save(intercoInvoice);
  }

  @Override
  public Company findIntercoCompany(Partner partner) {
    return Beans.get(CompanyRepository.class).all().filter("self.partner = ?", partner).fetchOne();
  }
}
