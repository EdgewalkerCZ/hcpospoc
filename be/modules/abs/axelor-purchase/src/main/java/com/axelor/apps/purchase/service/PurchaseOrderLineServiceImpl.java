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
package com.axelor.apps.purchase.service;

import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.base.db.Currency;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.PriceListLine;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.Unit;
import com.axelor.apps.base.db.repo.AppBaseRepository;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductMultipleQtyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.purchase.db.SupplierCatalog;
import com.axelor.apps.purchase.db.repo.SupplierCatalogRepository;
import com.axelor.apps.purchase.exception.IExceptionMessage;
import com.axelor.apps.tool.ContextTool;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseOrderLineServiceImpl implements PurchaseOrderLineService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject protected CurrencyService currencyService;

  @Inject protected AccountManagementService accountManagementService;

  @Inject protected PriceListService priceListService;

  @Inject protected AppBaseService appBaseService;

  @Inject protected PurchaseProductService productService;

  @Inject protected ProductMultipleQtyService productMultipleQtyService;

  @Deprecated private int sequence = 0;

  @Override
  public Map<String, BigDecimal> compute(
      PurchaseOrderLine purchaseOrderLine, PurchaseOrder purchaseOrder) throws AxelorException {

    HashMap<String, BigDecimal> map = new HashMap<>();
    if (purchaseOrder == null
        || purchaseOrderLine.getPrice() == null
        || purchaseOrderLine.getInTaxPrice() == null
        || purchaseOrderLine.getQty() == null) {
      return map;
    }

    BigDecimal exTaxTotal;
    BigDecimal companyExTaxTotal;
    BigDecimal inTaxTotal;
    BigDecimal companyInTaxTotal;
    BigDecimal priceDiscounted = this.computeDiscount(purchaseOrderLine, purchaseOrder.getInAti());
    BigDecimal taxRate = BigDecimal.ZERO;

    if (purchaseOrderLine.getTaxLine() != null) {
      taxRate = purchaseOrderLine.getTaxLine().getValue();
    }

    if (!purchaseOrder.getInAti()) {
      exTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
      inTaxTotal = exTaxTotal.add(exTaxTotal.multiply(taxRate));
      companyExTaxTotal = getCompanyExTaxTotal(exTaxTotal, purchaseOrder);
      companyInTaxTotal = companyExTaxTotal.add(companyExTaxTotal.multiply(taxRate));
    } else {
      inTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
      exTaxTotal = inTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP);
      companyInTaxTotal = getCompanyExTaxTotal(inTaxTotal, purchaseOrder);
      companyExTaxTotal =
          companyInTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP);
    }

    if (purchaseOrderLine.getProduct() != null) {
      map.put("saleMinPrice", getMinSalePrice(purchaseOrder, purchaseOrderLine));
      map.put(
          "salePrice",
          getSalePrice(
              purchaseOrder,
              purchaseOrderLine.getProduct(),
              purchaseOrder.getInAti()
                  ? purchaseOrderLine.getInTaxPrice()
                  : purchaseOrderLine.getPrice()));
    }
    map.put("exTaxTotal", exTaxTotal);
    map.put("inTaxTotal", inTaxTotal);
    map.put("companyExTaxTotal", companyExTaxTotal);
    map.put("companyInTaxTotal", companyInTaxTotal);
    map.put("priceDiscounted", priceDiscounted);
    purchaseOrderLine.setExTaxTotal(exTaxTotal);
    purchaseOrderLine.setInTaxTotal(inTaxTotal);
    purchaseOrderLine.setPriceDiscounted(priceDiscounted);
    purchaseOrderLine.setCompanyExTaxTotal(companyExTaxTotal);
    purchaseOrderLine.setCompanyInTaxTotal(companyInTaxTotal);
    purchaseOrderLine.setSaleMinPrice(getMinSalePrice(purchaseOrder, purchaseOrderLine));
    purchaseOrderLine.setSalePrice(
        getSalePrice(
            purchaseOrder,
            purchaseOrderLine.getProduct(),
            purchaseOrder.getInAti()
                ? purchaseOrderLine.getInTaxPrice()
                : purchaseOrderLine.getPrice()));
    return map;
  }

  /**
   * Calculer le montant HT d'une ligne de commande.
   *
   * @param quantity Quantité.
   * @param price Le prix.
   * @return Le montant HT de la ligne.
   */
  public static BigDecimal computeAmount(BigDecimal quantity, BigDecimal price) {

    BigDecimal amount =
        quantity
            .multiply(price)
            .setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_EVEN);

    LOG.debug(
        "Calcul du montant HT avec une quantité de {} pour {} : {}",
        new Object[] {quantity, price, amount});

    return amount;
  }

  @Override
  public String[] getProductSupplierInfos(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) throws AxelorException {

    Product product = purchaseOrderLine.getProduct();
    SupplierCatalog supplierCatalog =
        getSupplierCatalog(product, purchaseOrder.getSupplierPartner());

    if (supplierCatalog != null) {
      String productName = supplierCatalog.getProductSupplierName();
      String productCode = supplierCatalog.getProductSupplierCode();
      return new String[] {productName, productCode};
    }
    return new String[] {"", ""};
  }

  /**
   * Returns the ex. tax unit price of the purchase order line or null if the product is not
   * available for purchase at the supplier of the purchase order
   */
  @Override
  public BigDecimal getExTaxUnitPrice(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine, TaxLine taxLine)
      throws AxelorException {
    return this.getUnitPrice(purchaseOrder, purchaseOrderLine, taxLine, false);
  }

  /**
   * Returns the incl. tax unit price of the purchase order line or null if the product is not
   * available for purchase at the supplier of the purchase order
   */
  @Override
  public BigDecimal getInTaxUnitPrice(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine, TaxLine taxLine)
      throws AxelorException {
    return this.getUnitPrice(purchaseOrder, purchaseOrderLine, taxLine, true);
  }

  /**
   * A function used to get the unit price of a purchase order line, either in ati or wt
   *
   * @param purchaseOrder the purchase order containing the purchase order line
   * @param purchaseOrderLine
   * @param taxLine the tax applied to the unit price
   * @param resultInAti whether or not you want the result to be in ati or not
   * @return the unit price of the purchase order line or null if the product is not available for
   *     purchase at the supplier of the purchase order
   * @throws AxelorException
   */
  private BigDecimal getUnitPrice(
      PurchaseOrder purchaseOrder,
      PurchaseOrderLine purchaseOrderLine,
      TaxLine taxLine,
      boolean resultInAti)
      throws AxelorException {
    BigDecimal purchasePrice;
    Currency purchaseCurrency;
    Product product = purchaseOrderLine.getProduct();
    SupplierCatalog supplierCatalog =
        getSupplierCatalog(product, purchaseOrder.getSupplierPartner());

    if (supplierCatalog != null) {
      purchasePrice = supplierCatalog.getPrice();
      purchaseCurrency = supplierCatalog.getSupplierPartner().getCurrency();
    } else {
      return null;
    }

    BigDecimal price =
        (product.getInAti() == resultInAti)
            ? purchasePrice
            : this.convertUnitPrice(product.getInAti(), taxLine, purchasePrice);

    return currencyService
        .getAmountCurrencyConvertedAtDate(
            purchaseCurrency, purchaseOrder.getCurrency(), price, purchaseOrder.getOrderDate())
        .setScale(appBaseService.getNbDecimalDigitForUnitPrice(), RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal getMinSalePrice(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) throws AxelorException {

    Product product = purchaseOrderLine.getProduct();

    if (product == null || !product.getSellable()) {
      return BigDecimal.ZERO;
    }

    TaxLine saleTaxLine =
        accountManagementService.getTaxLine(
            purchaseOrder.getOrderDate(),
            purchaseOrderLine.getProduct(),
            purchaseOrder.getCompany(),
            purchaseOrder.getSupplierPartner().getFiscalPosition(),
            false);

    BigDecimal price;
    if (purchaseOrder.getInAti() != product.getInAti()) {
      price = this.convertUnitPrice(product.getInAti(), saleTaxLine, product.getSalePrice());
    } else {
      price = product.getSalePrice();
    }

    return currencyService
        .getAmountCurrencyConvertedAtDate(
            product.getSaleCurrency(),
            purchaseOrder.getCurrency(),
            price,
            purchaseOrder.getOrderDate())
        .setScale(appBaseService.getNbDecimalDigitForUnitPrice(), RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal getSalePrice(PurchaseOrder purchaseOrder, Product product, BigDecimal price)
      throws AxelorException {

    if (product == null || !product.getSellable()) {
      return BigDecimal.ZERO;
    }

    price = price.multiply(product.getManagPriceCoef());

    return currencyService
        .getAmountCurrencyConvertedAtDate(
            product.getSaleCurrency(),
            purchaseOrder.getCurrency(),
            price,
            purchaseOrder.getOrderDate())
        .setScale(appBaseService.getNbDecimalDigitForUnitPrice(), RoundingMode.HALF_UP);
  }

  @Override
  public TaxLine getTaxLine(PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine)
      throws AxelorException {

    return accountManagementService.getTaxLine(
        purchaseOrder.getOrderDate(),
        purchaseOrderLine.getProduct(),
        purchaseOrder.getCompany(),
        purchaseOrder.getSupplierPartner().getFiscalPosition(),
        true);
  }

  @Override
  public Optional<TaxLine> getOptionalTaxLine(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) {
    try {
      return Optional.of(getTaxLine(purchaseOrder, purchaseOrderLine));
    } catch (AxelorException e) {
      return Optional.empty();
    }
  }

  @Override
  public BigDecimal computePurchaseOrderLine(PurchaseOrderLine purchaseOrderLine) {

    return purchaseOrderLine.getExTaxTotal();
  }

  @Override
  public BigDecimal getCompanyExTaxTotal(BigDecimal exTaxTotal, PurchaseOrder purchaseOrder)
      throws AxelorException {

    return currencyService
        .getAmountCurrencyConvertedAtDate(
            purchaseOrder.getCurrency(),
            purchaseOrder.getCompany().getCurrency(),
            exTaxTotal,
            purchaseOrder.getOrderDate())
        .setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP);
  }

  @Override
  public PriceListLine getPriceListLine(PurchaseOrderLine purchaseOrderLine, PriceList priceList) {

    return priceListService.getPriceListLine(
        purchaseOrderLine.getProduct(), purchaseOrderLine.getQty(), priceList);
  }

  @Override
  public BigDecimal computeDiscount(PurchaseOrderLine purchaseOrderLine, Boolean inAti) {

    BigDecimal price = inAti ? purchaseOrderLine.getInTaxPrice() : purchaseOrderLine.getPrice();

    return priceListService.computeDiscount(
        price, purchaseOrderLine.getDiscountTypeSelect(), purchaseOrderLine.getDiscountAmount());
  }

  @Override
  public PurchaseOrderLine createPurchaseOrderLine(
      PurchaseOrder purchaseOrder,
      Product product,
      String productName,
      String description,
      BigDecimal qty,
      Unit unit)
      throws AxelorException {

    PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();
    purchaseOrderLine.setPurchaseOrder(purchaseOrder);

    purchaseOrderLine.setEstimatedDelivDate(purchaseOrder.getDeliveryDate());
    purchaseOrderLine.setDescription(description);

    purchaseOrderLine.setIsOrdered(false);

    purchaseOrderLine.setQty(qty);
    purchaseOrderLine.setSequence(sequence);
    sequence++;

    purchaseOrderLine.setUnit(unit);
    purchaseOrderLine.setProductName(productName);

    if (product == null) {
      return purchaseOrderLine;
    }

    purchaseOrderLine.setProduct(product);

    if (productName == null) {
      purchaseOrderLine.setProductName(product.getName());
    }

    TaxLine taxLine = this.getTaxLine(purchaseOrder, purchaseOrderLine);
    purchaseOrderLine.setTaxLine(taxLine);

    BigDecimal price = this.getExTaxUnitPrice(purchaseOrder, purchaseOrderLine, taxLine);
    BigDecimal inTaxPrice = this.getInTaxUnitPrice(purchaseOrder, purchaseOrderLine, taxLine);

    Map<String, Object> discounts;
    if (product.getInAti()) {
      discounts = this.getDiscount(purchaseOrder, purchaseOrderLine, inTaxPrice);
    } else {
      discounts = this.getDiscount(purchaseOrder, purchaseOrderLine, price);
    }

    if (discounts != null) {
      purchaseOrderLine.setDiscountAmount((BigDecimal) discounts.get("discountAmount"));
      purchaseOrderLine.setDiscountTypeSelect((Integer) discounts.get("discountTypeSelect"));
      if (discounts.get("price") != null) {
        if (product.getInAti()) {
          inTaxPrice = (BigDecimal) discounts.get("price");
          price = this.convertUnitPrice(true, taxLine, inTaxPrice);
        } else {
          price = (BigDecimal) discounts.get("price");
          inTaxPrice = this.convertUnitPrice(false, taxLine, price);
        }
      }
    }

    purchaseOrderLine.setPrice(price);
    purchaseOrderLine.setInTaxPrice(inTaxPrice);

    BigDecimal priceDiscounted = this.computeDiscount(purchaseOrderLine, purchaseOrder.getInAti());
    purchaseOrderLine.setPriceDiscounted(priceDiscounted);

    BigDecimal exTaxTotal, inTaxTotal, companyExTaxTotal, companyInTaxTotal;

    if (!purchaseOrder.getInAti()) {
      exTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
      inTaxTotal = exTaxTotal.add(exTaxTotal.multiply(purchaseOrderLine.getTaxLine().getValue()));
      companyExTaxTotal = this.getCompanyExTaxTotal(exTaxTotal, purchaseOrder);
      companyInTaxTotal =
          companyExTaxTotal.add(
              companyExTaxTotal.multiply(purchaseOrderLine.getTaxLine().getValue()));

    } else {
      inTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
      exTaxTotal =
          inTaxTotal.divide(
              purchaseOrderLine.getTaxLine().getValue().add(BigDecimal.ONE),
              2,
              BigDecimal.ROUND_HALF_UP);
      companyInTaxTotal = this.getCompanyExTaxTotal(inTaxTotal, purchaseOrder);
      companyExTaxTotal =
          companyInTaxTotal.divide(
              purchaseOrderLine.getTaxLine().getValue().add(BigDecimal.ONE),
              2,
              BigDecimal.ROUND_HALF_UP);
    }

    purchaseOrderLine.setExTaxTotal(exTaxTotal);
    purchaseOrderLine.setCompanyExTaxTotal(companyExTaxTotal);
    purchaseOrderLine.setCompanyInTaxTotal(companyInTaxTotal);
    purchaseOrderLine.setInTaxTotal(inTaxTotal);

    return purchaseOrderLine;
  }

  @Override
  public BigDecimal getQty(PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) {

    SupplierCatalog supplierCatalog = this.getSupplierCatalog(purchaseOrder, purchaseOrderLine);

    if (supplierCatalog != null) {

      return supplierCatalog.getMinQty();
    }

    return BigDecimal.ONE;
  }

  @Override
  public SupplierCatalog getSupplierCatalog(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) {

    Product product = purchaseOrderLine.getProduct();

    SupplierCatalog supplierCatalog =
        this.getSupplierCatalog(product, purchaseOrder.getSupplierPartner());

    //		If there is no catalog for supplier, then we don't take the default catalog.

    //		if(supplierCatalog == null)  {
    //
    //			supplierCatalog = this.getSupplierCatalog(product, product.getDefaultSupplierPartner());
    //		}

    return supplierCatalog;
  }

  @Override
  public SupplierCatalog getSupplierCatalog(Product product, Partner supplierPartner) {

    if (product != null && product.getSupplierCatalogList() != null) {

      for (SupplierCatalog supplierCatalog : product.getSupplierCatalogList()) {

        if (supplierCatalog.getSupplierPartner().equals(supplierPartner)) {
          return supplierCatalog;
        }
      }
    }
    return null;
  }

  @Override
  public BigDecimal convertUnitPrice(Boolean priceIsAti, TaxLine taxLine, BigDecimal price) {

    if (taxLine == null) {
      return price;
    }

    if (priceIsAti) {
      price = price.divide(taxLine.getValue().add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP);
    } else {
      price = price.add(price.multiply(taxLine.getValue()));
    }
    return price;
  }

  @Override
  public Map<String, Object> getDiscount(
      PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine, BigDecimal price) {

    PriceList priceList = purchaseOrder.getPriceList();
    BigDecimal discountAmount = BigDecimal.ZERO;

    int computeMethodDiscountSelect = appBaseService.getAppBase().getComputeMethodDiscountSelect();

    Map<String, Object> discounts = null;

    if (priceList != null) {
      PriceListLine priceListLine = this.getPriceListLine(purchaseOrderLine, priceList);
      discounts = priceListService.getReplacedPriceAndDiscounts(priceList, priceListLine, price);
    }

    if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
      List<SupplierCatalog> supplierCatalogList =
          purchaseOrderLine.getProduct().getSupplierCatalogList();
      if (supplierCatalogList != null && !supplierCatalogList.isEmpty()) {
        SupplierCatalog supplierCatalog =
            Beans.get(SupplierCatalogRepository.class)
                .all()
                .filter(
                    "self.product = ?1 AND self.minQty <= ?2 AND self.supplierPartner = ?3 ORDER BY self.minQty DESC",
                    purchaseOrderLine.getProduct(),
                    purchaseOrderLine.getQty(),
                    purchaseOrder.getSupplierPartner())
                .fetchOne();
        if (supplierCatalog != null) {

          discounts = productService.getDiscountsFromCatalog(supplierCatalog, price);

          if (computeMethodDiscountSelect != AppBaseRepository.DISCOUNT_SEPARATE) {
            discounts.put(
                "price",
                priceListService.computeDiscount(
                    price,
                    (int) discounts.get("discountTypeSelect"),
                    (BigDecimal) discounts.get("discountAmount")));
          }
        }
      }
    }

    return discounts;
  }

  @Override
  public int getDiscountTypeSelect(
      PurchaseOrderLine purchaseOrderLine, PurchaseOrder purchaseOrder) {
    PriceList priceList = purchaseOrder.getPriceList();
    if (priceList != null) {
      PriceListLine priceListLine = this.getPriceListLine(purchaseOrderLine, priceList);

      return priceListLine.getTypeSelect();
    }
    return 0;
  }

  @Override
  public Unit getPurchaseUnit(PurchaseOrderLine purchaseOrderLine) {
    Unit unit = purchaseOrderLine.getProduct().getPurchasesUnit();
    if (unit == null) {
      unit = purchaseOrderLine.getProduct().getUnit();
    }
    return unit;
  }

  @Override
  public BigDecimal getMinQty(PurchaseOrder purchaseOrder, PurchaseOrderLine purchaseOrderLine) {
    SupplierCatalog supplierCatalog = getSupplierCatalog(purchaseOrder, purchaseOrderLine);
    return supplierCatalog != null ? supplierCatalog.getMinQty() : BigDecimal.ONE;
  }

  @Override
  public void checkMinQty(
      PurchaseOrder purchaseOrder,
      PurchaseOrderLine purchaseOrderLine,
      ActionRequest request,
      ActionResponse response) {

    BigDecimal minQty = this.getMinQty(purchaseOrder, purchaseOrderLine);

    if (purchaseOrderLine.getQty().compareTo(minQty) < 0) {
      String msg = String.format(I18n.get(IExceptionMessage.PURCHASE_ORDER_LINE_MIN_QTY), minQty);

      if (request.getAction().endsWith("onchange")) {
        response.setFlash(msg);
      }

      String title = ContextTool.formatLabel(msg, ContextTool.SPAN_CLASS_WARNING, 75);

      response.setAttr("minQtyNotRespectedLabel", "title", title);
      response.setAttr("minQtyNotRespectedLabel", "hidden", false);

    } else {
      response.setAttr("minQtyNotRespectedLabel", "hidden", true);
    }
  }

  @Override
  public void checkMultipleQty(PurchaseOrderLine purchaseOrderLine, ActionResponse response) {

    Product product = purchaseOrderLine.getProduct();

    if (product == null) {
      return;
    }

    productMultipleQtyService.checkMultipleQty(
        purchaseOrderLine.getQty(),
        product.getPurchaseProductMultipleQtyList(),
        product.getAllowToForcePurchaseQty(),
        response);
  }
}
