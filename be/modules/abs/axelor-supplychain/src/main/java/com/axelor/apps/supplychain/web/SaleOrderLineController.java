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
package com.axelor.apps.supplychain.web;

import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Blocking;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.Unit;
import com.axelor.apps.base.db.repo.BlockingRepository;
import com.axelor.apps.base.service.BlockingService;
import com.axelor.apps.base.service.UnitConversionService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderLineRepository;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.db.repo.StockMoveLineRepository;
import com.axelor.apps.stock.service.StockLocationLineService;
import com.axelor.apps.supplychain.service.SaleOrderLineServiceSupplyChain;
import com.axelor.apps.supplychain.service.SaleOrderLineServiceSupplyChainImpl;
import com.axelor.apps.supplychain.service.StockMoveLineSupplychainServiceImpl;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SaleOrderLineController {

  @Inject protected SaleOrderLineServiceSupplyChainImpl saleOrderLineServiceSupplyChainImpl;

  public void computeAnalyticDistribution(ActionRequest request, ActionResponse response)
      throws AxelorException {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    SaleOrder saleOrder = saleOrderLine.getSaleOrder();
    if (saleOrder == null) {
      saleOrder = saleOrderLineServiceSupplyChainImpl.getSaleOrder(request.getContext());
      saleOrderLine.setSaleOrder(saleOrder);
    }
    if (Beans.get(AppAccountService.class).getAppAccount().getManageAnalyticAccounting()) {
      saleOrderLine =
          saleOrderLineServiceSupplyChainImpl.computeAnalyticDistribution(saleOrderLine);
      response.setValue("analyticMoveLineList", saleOrderLine.getAnalyticMoveLineList());
    }
  }

  public void createAnalyticDistributionWithTemplate(ActionRequest request, ActionResponse response)
      throws AxelorException {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    SaleOrder saleOrder = saleOrderLine.getSaleOrder();
    if (saleOrder == null) {
      saleOrder = saleOrderLineServiceSupplyChainImpl.getSaleOrder(request.getContext());
      saleOrderLine.setSaleOrder(saleOrder);
    }
    if (saleOrderLine.getAnalyticDistributionTemplate() != null) {
      saleOrderLine =
          saleOrderLineServiceSupplyChainImpl.createAnalyticDistributionWithTemplate(saleOrderLine);
      response.setValue("analyticMoveLineList", saleOrderLine.getAnalyticMoveLineList());
    } else {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR, I18n.get("No template selected"));
    }
  }

  public void checkStocks(ActionRequest request, ActionResponse response) {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    SaleOrder saleOrder = saleOrderLineServiceSupplyChainImpl.getSaleOrder(request.getContext());
    if (saleOrder.getStockLocation() == null) {
      return;
    }
    try {
      if (saleOrderLine.getSaleSupplySelect() != SaleOrderLineRepository.SALE_SUPPLY_FROM_STOCK) {
        return;
      }
      // Use the unit to get the right quantity
      Unit unit = null;
      if (saleOrderLine.getProduct() != null) unit = saleOrderLine.getProduct().getUnit();
      BigDecimal qty = saleOrderLine.getQty();
      if (unit != null && !unit.equals(saleOrderLine.getUnit())) {
        qty =
            Beans.get(UnitConversionService.class)
                .convertWithProduct(saleOrderLine.getUnit(), unit, qty, saleOrderLine.getProduct());
      }
      Beans.get(StockLocationLineService.class)
          .checkIfEnoughStock(saleOrder.getStockLocation(), saleOrderLine.getProduct(), qty);
    } catch (Exception e) {
      response.setAlert(e.getLocalizedMessage());
    }
  }

  public void fillAvailableStock(ActionRequest request, ActionResponse response) {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    if (saleOrderLine.getSaleOrder() == null) {
      return;
    }
    if (saleOrderLine.getProduct() != null
        && saleOrderLine.getSaleOrder().getStockLocation() != null) {
      response.setValue(
          "$availableStock", saleOrderLineServiceSupplyChainImpl.getAvailableStock(saleOrderLine));
    }
  }

  public void changeReservedQty(ActionRequest request, ActionResponse response) {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    BigDecimal newReservedQty = saleOrderLine.getReservedQty();
    try {
      saleOrderLine = Beans.get(SaleOrderLineRepository.class).find(saleOrderLine.getId());
      StockMoveLine stockMoveLine =
          Beans.get(StockMoveLineRepository.class)
              .all()
              .filter("self.saleOrderLine = :saleOrderLine")
              .bind("saleOrderLine", saleOrderLine)
              .fetchOne();
      saleOrderLineServiceSupplyChainImpl.changeReservedQty(saleOrderLine, newReservedQty);
      if (stockMoveLine != null) {
        Beans.get(StockMoveLineSupplychainServiceImpl.class)
            .updateReservedQty(stockMoveLine, newReservedQty);
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  /**
   * Called from sale order line form. Set domain for supplier partner.
   *
   * @param request
   * @param response
   */
  public void supplierPartnerDomain(ActionRequest request, ActionResponse response) {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    String domain = "self.isContact = false AND self.isSupplier = true";
    Product product = saleOrderLine.getProduct();
    if (product != null) {
      List<Long> authorizedPartnerIdsList =
          Beans.get(SaleOrderLineServiceSupplyChain.class).getSupplierPartnerList(saleOrderLine);
      if (authorizedPartnerIdsList.isEmpty()) {
        response.setAttr("supplierPartner", "domain", "self.id IN (0)");
        return;
      } else {
        domain +=
            String.format(
                " AND self.id IN (%s)",
                authorizedPartnerIdsList
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
      }
    }
    SaleOrder saleOrder = saleOrderLine.getSaleOrder();
    if (saleOrder == null) {
      Context parentContext = request.getContext().getParent();
      if (parentContext == null) {
        response.setAttr("supplierPartner", "domain", domain);
        return;
      }
      saleOrder = parentContext.asType(SaleOrder.class);
      if (saleOrder == null) {
        response.setAttr("supplierPartner", "domain", domain);
        return;
      }
    }
    String blockedPartnerQuery =
        Beans.get(BlockingService.class)
            .listOfBlockedPartner(saleOrder.getCompany(), BlockingRepository.PURCHASE_BLOCKING);

    if (!Strings.isNullOrEmpty(blockedPartnerQuery)) {
      domain += String.format(" AND self.id NOT in (%s)", blockedPartnerQuery);
    }
    response.setAttr("supplierPartner", "domain", domain);
  }

  /**
   * Called from sale order line form, on product change and on sale supply select change
   *
   * @param request
   * @param response
   */
  public void supplierPartnerDefault(ActionRequest request, ActionResponse response) {
    SaleOrderLine saleOrderLine = request.getContext().asType(SaleOrderLine.class);
    if (saleOrderLine.getSaleSupplySelect() != SaleOrderLineRepository.SALE_SUPPLY_PURCHASE) {
      return;
    }

    SaleOrder saleOrder = saleOrderLine.getSaleOrder();
    if (saleOrder == null) {
      Context parentContext = request.getContext().getParent();
      if (parentContext == null) {
        return;
      }
      saleOrder = parentContext.asType(SaleOrder.class);
    }
    if (saleOrder == null) {
      return;
    }

    Partner supplierPartner = null;
    if (saleOrderLine.getProduct() != null) {
      supplierPartner = saleOrderLine.getProduct().getDefaultSupplierPartner();
    }

    if (supplierPartner != null) {
      Blocking blocking =
          Beans.get(BlockingService.class)
              .getBlocking(
                  supplierPartner, saleOrder.getCompany(), BlockingRepository.PURCHASE_BLOCKING);
      if (blocking != null) {
        supplierPartner = null;
      }
    }

    response.setValue("supplierPartner", supplierPartner);
  }
}
