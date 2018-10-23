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
package com.axelor.apps.sale.web;

import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Currency;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.PrintingSettings;
import com.axelor.apps.base.db.Wizard;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.PriceListRepository;
import com.axelor.apps.base.service.BankDetailsService;
import com.axelor.apps.base.service.PartnerPriceListService;
import com.axelor.apps.base.service.TradingNameService;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.exception.IExceptionMessage;
import com.axelor.apps.sale.service.saleorder.SaleOrderComputeService;
import com.axelor.apps.sale.service.saleorder.SaleOrderCreateService;
import com.axelor.apps.sale.service.saleorder.SaleOrderMarginService;
import com.axelor.apps.sale.service.saleorder.SaleOrderService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.sale.service.saleorder.print.SaleOrderPrintService;
import com.axelor.apps.tool.StringTool;
import com.axelor.common.ObjectUtils;
import com.axelor.db.JPA;
import com.axelor.db.mapper.Mapper;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.meta.schema.actions.ActionView.ActionViewBuilder;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.team.db.Team;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.eclipse.birt.core.exception.BirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SaleOrderController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject private SaleOrderRepository saleOrderRepo;

  @Inject private SaleOrderPrintService saleOrderPrintService;

  public void compute(ActionRequest request, ActionResponse response) {

    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);

    try {
      saleOrder = Beans.get(SaleOrderComputeService.class).computeSaleOrder(saleOrder);
      response.setValues(saleOrder);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void computeMargin(ActionRequest request, ActionResponse response) {

    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);

    try {
      Beans.get(SaleOrderMarginService.class).computeMarginSaleOrder(saleOrder);

      response.setValue("totalCostPrice", saleOrder.getTotalCostPrice());
      response.setValue("totalGrossMargin", saleOrder.getTotalGrossMargin());
      response.setValue("marginRate", saleOrder.getMarginRate());

    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  /**
   * Method that print the sale order as a Pdf
   *
   * @param request
   * @param response
   * @return
   * @throws BirtException
   * @throws IOException
   */
  public void showSaleOrder(ActionRequest request, ActionResponse response) throws AxelorException {

    this.exportSaleOrder(request, response, false, ReportSettings.FORMAT_PDF);
  }

  /** Method that prints a proforma invoice as a PDF */
  public void printProformaInvoice(ActionRequest request, ActionResponse response)
      throws AxelorException {

    this.exportSaleOrder(request, response, true, ReportSettings.FORMAT_PDF);
  }

  public void exportSaleOrderExcel(ActionRequest request, ActionResponse response)
      throws AxelorException {

    this.exportSaleOrder(request, response, false, ReportSettings.FORMAT_XLS);
  }

  public void exportSaleOrderWord(ActionRequest request, ActionResponse response)
      throws AxelorException {

    this.exportSaleOrder(request, response, false, ReportSettings.FORMAT_DOC);
  }

  @SuppressWarnings("unchecked")
  public void exportSaleOrder(
      ActionRequest request, ActionResponse response, boolean proforma, String format) {

    Context context = request.getContext();
    String fileLink;
    String title;

    try {
      if (!ObjectUtils.isEmpty(request.getContext().get("_ids"))) {
        List<Long> ids =
            Lists.transform(
                (List) request.getContext().get("_ids"),
                new Function<Object, Long>() {
                  @Nullable
                  @Override
                  public Long apply(@Nullable Object input) {
                    return Long.parseLong(input.toString());
                  }
                });
        fileLink = saleOrderPrintService.printSaleOrders(ids);
        title = I18n.get("Sale orders");

      } else if (context.get("id") != null) {

        SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
        title = Beans.get(SaleOrderService.class).getFileName(saleOrder);
        fileLink = saleOrderPrintService.printSaleOrder(saleOrder, proforma, format);

        logger.debug("Printing " + title);
      } else {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_MISSING_FIELD,
            I18n.get(IExceptionMessage.SALE_ORDER_PRINT));
      }
      response.setView(ActionView.define(title).add("html", fileLink).map());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void cancelSaleOrder(ActionRequest request, ActionResponse response) {

    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);

    Beans.get(SaleOrderWorkflowService.class)
        .cancelSaleOrder(
            saleOrderRepo.find(saleOrder.getId()),
            saleOrder.getCancelReason(),
            saleOrder.getCancelReasonStr());

    response.setFlash(I18n.get("The sale order was canceled"));
    response.setCanClose(true);
  }

  public void finalizeQuotation(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    saleOrder = saleOrderRepo.find(saleOrder.getId());

    try {
      Beans.get(SaleOrderWorkflowService.class).finalizeQuotation(saleOrder);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }

    response.setReload(true);
  }

  public void completeSaleOrder(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    saleOrder = saleOrderRepo.find(saleOrder.getId());

    try {
      Beans.get(SaleOrderWorkflowServiceImpl.class).completeSaleOrder(saleOrder);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }

    response.setReload(true);
  }

  public void confirmSaleOrder(ActionRequest request, ActionResponse response) throws Exception {

    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);

    Beans.get(SaleOrderWorkflowService.class)
        .confirmSaleOrder(saleOrderRepo.find(saleOrder.getId()));

    response.setReload(true);
  }

  public void generateViewSaleOrder(ActionRequest request, ActionResponse response) {
    SaleOrder context = request.getContext().asType(SaleOrder.class);
    context = saleOrderRepo.find(context.getId());
    response.setView(
        ActionView.define("Sale order")
            .model(SaleOrder.class.getName())
            .add("form", "sale-order-form-wizard")
            .context("_idCopy", context.getId().toString())
            .map());
  }

  public void generateViewTemplate(ActionRequest request, ActionResponse response) {
    SaleOrder context = request.getContext().asType(SaleOrder.class);
    response.setView(
        ActionView.define("Template")
            .model(SaleOrder.class.getName())
            .add("form", "sale-order-template-form-wizard")
            .context("_idCopy", context.getId().toString())
            .map());
  }

  public void createSaleOrder(ActionRequest request, ActionResponse response) {
    SaleOrder origin =
        saleOrderRepo.find(Long.parseLong(request.getContext().get("_idCopy").toString()));
    if (origin != null) {
      SaleOrder copy = Beans.get(SaleOrderCreateService.class).createSaleOrder(origin);
      response.setValues(Mapper.toMap(copy));
    }
  }

  public void createTemplate(ActionRequest request, ActionResponse response) {
    Context context = request.getContext();
    if (context.get("_idCopy") != null) {
      String idCopy = context.get("_idCopy").toString();
      SaleOrder origin = saleOrderRepo.find(Long.parseLong(idCopy));
      SaleOrder copy = Beans.get(SaleOrderCreateService.class).createSaleOrder(origin);
      response.setValues(Mapper.toMap(copy));
    }
  }

  public void computeEndOfValidityDate(ActionRequest request, ActionResponse response) {

    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);

    try {
      saleOrder = Beans.get(SaleOrderService.class).computeEndOfValidityDate(saleOrder);
      response.setValue("endOfValidityDate", saleOrder.getEndOfValidityDate());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void mergeSaleOrder(ActionRequest request, ActionResponse response) {
    List<SaleOrder> saleOrderList = new ArrayList<SaleOrder>();
    List<Long> saleOrderIdList = new ArrayList<Long>();
    boolean fromPopup = false;
    String lineToMerge;
    if (request.getContext().get("saleQuotationToMerge") != null) {
      lineToMerge = "saleQuotationToMerge";
    } else {
      lineToMerge = "saleOrderToMerge";
    }

    if (request.getContext().get(lineToMerge) != null) {

      if (request.getContext().get(lineToMerge) instanceof List) {
        // No confirmation popup, sale orders are content in a parameter list
        List<Map> saleOrderMap = (List<Map>) request.getContext().get(lineToMerge);
        for (Map map : saleOrderMap) {
          saleOrderIdList.add(new Long((Integer) map.get("id")));
        }
      } else {
        // After confirmation popup, sale order's id are in a string separated by ","
        String saleOrderIdListStr = (String) request.getContext().get(lineToMerge);
        for (String saleOrderId : saleOrderIdListStr.split(",")) {
          saleOrderIdList.add(new Long(saleOrderId));
        }
        fromPopup = true;
      }
    }

    // Check if currency, clientPartner and company are the same for all selected sale orders
    Currency commonCurrency = null;
    Partner commonClientPartner = null;
    Company commonCompany = null;
    Partner commonContactPartner = null;
    Team commonTeam = null;
    // Useful to determine if a difference exists between teams of all sale orders
    boolean existTeamDiff = false;
    // Useful to determine if a difference exists between contact partners of all sale orders
    boolean existContactPartnerDiff = false;
    PriceList commonPriceList = null;
    // Useful to determine if a difference exists between price lists of all sale orders
    boolean existPriceListDiff = false;

    SaleOrder saleOrderTemp;
    int count = 1;
    for (Long saleOrderId : saleOrderIdList) {
      saleOrderTemp = JPA.em().find(SaleOrder.class, saleOrderId);
      saleOrderList.add(saleOrderTemp);
      if (count == 1) {
        commonCurrency = saleOrderTemp.getCurrency();
        commonClientPartner = saleOrderTemp.getClientPartner();
        commonCompany = saleOrderTemp.getCompany();
        commonContactPartner = saleOrderTemp.getContactPartner();
        commonTeam = saleOrderTemp.getTeam();
        commonPriceList = saleOrderTemp.getPriceList();
      } else {
        if (commonCurrency != null && !commonCurrency.equals(saleOrderTemp.getCurrency())) {
          commonCurrency = null;
        }
        if (commonClientPartner != null
            && !commonClientPartner.equals(saleOrderTemp.getClientPartner())) {
          commonClientPartner = null;
        }
        if (commonCompany != null && !commonCompany.equals(saleOrderTemp.getCompany())) {
          commonCompany = null;
        }
        if (commonContactPartner != null
            && !commonContactPartner.equals(saleOrderTemp.getContactPartner())) {
          commonContactPartner = null;
          existContactPartnerDiff = true;
        }
        if (commonTeam != null && !commonTeam.equals(saleOrderTemp.getTeam())) {
          commonTeam = null;
          existTeamDiff = true;
        }
        if (commonPriceList != null && !commonPriceList.equals(saleOrderTemp.getPriceList())) {
          commonPriceList = null;
          existPriceListDiff = true;
        }
      }
      count++;
    }

    StringBuilder fieldErrors = new StringBuilder();
    if (commonCurrency == null) {
      fieldErrors.append(I18n.get(IExceptionMessage.SALE_ORDER_MERGE_ERROR_CURRENCY));
    }
    if (commonClientPartner == null) {
      if (fieldErrors.length() > 0) {
        fieldErrors.append("<br/>");
      }
      fieldErrors.append(I18n.get(IExceptionMessage.SALE_ORDER_MERGE_ERROR_CLIENT_PARTNER));
    }
    if (commonCompany == null) {
      if (fieldErrors.length() > 0) {
        fieldErrors.append("<br/>");
      }
      fieldErrors.append(I18n.get(IExceptionMessage.SALE_ORDER_MERGE_ERROR_COMPANY));
    }

    if (fieldErrors.length() > 0) {
      response.setFlash(fieldErrors.toString());
      return;
    }

    // Check if priceList or contactPartner are content in parameters
    if (request.getContext().get("priceList") != null) {
      commonPriceList =
          JPA.em()
              .find(
                  PriceList.class,
                  new Long((Integer) ((Map) request.getContext().get("priceList")).get("id")));
    }
    if (request.getContext().get("contactPartner") != null) {
      commonContactPartner =
          JPA.em()
              .find(
                  Partner.class,
                  new Long((Integer) ((Map) request.getContext().get("contactPartner")).get("id")));
    }
    if (request.getContext().get("team") != null) {
      commonTeam =
          JPA.em()
              .find(
                  Team.class,
                  new Long((Integer) ((Map) request.getContext().get("team")).get("id")));
    }

    if (!fromPopup && (existContactPartnerDiff || existPriceListDiff || existTeamDiff)) {
      // Need to display intermediate screen to select some values
      ActionViewBuilder confirmView =
          ActionView.define("Confirm merge sale order")
              .model(Wizard.class.getName())
              .add("form", "sale-order-merge-confirm-form")
              .param("popup", "true")
              .param("show-toolbar", "false")
              .param("show-confirm", "false")
              .param("popup-save", "false")
              .param("forceEdit", "true");

      if (existPriceListDiff) {
        confirmView.context("contextPriceListToCheck", "true");
      }
      if (existContactPartnerDiff) {
        confirmView.context("contextContactPartnerToCheck", "true");
        confirmView.context("contextPartnerId", commonClientPartner.getId().toString());
      }
      if (existTeamDiff) {
        confirmView.context("contextTeamToCheck", "true");
      }

      confirmView.context(lineToMerge, Joiner.on(",").join(saleOrderIdList));

      response.setView(confirmView.map());

      return;
    }

    try {
      SaleOrder saleOrder =
          Beans.get(SaleOrderCreateService.class)
              .mergeSaleOrders(
                  saleOrderList,
                  commonCurrency,
                  commonClientPartner,
                  commonCompany,
                  commonContactPartner,
                  commonPriceList,
                  commonTeam);
      if (saleOrder != null) {
        // Open the generated sale order in a new tab
        response.setView(
            ActionView.define("Sale order")
                .model(SaleOrder.class.getName())
                .add("grid", "sale-order-grid")
                .add("form", "sale-order-form")
                .param("forceEdit", "true")
                .context("_showRecord", String.valueOf(saleOrder.getId()))
                .map());
        response.setCanClose(true);
      }
    } catch (Exception e) {
      response.setFlash(e.getLocalizedMessage());
    }
  }

  /**
   * Set the address string with their values.
   *
   * @param request
   * @param response
   */
  public void computeAddressStr(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    Beans.get(SaleOrderService.class).computeAddressStr(saleOrder);

    response.setValues(saleOrder);
  }

  /**
   * Called on partner, company or payment change. Fill the bank details with a default value.
   *
   * @param request
   * @param response
   */
  public void fillCompanyBankDetails(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    PaymentMode paymentMode = (PaymentMode) request.getContext().get("paymentMode");
    Company company = saleOrder.getCompany();
    Partner partner = saleOrder.getClientPartner();
    if (company == null) {
      return;
    }
    if (partner != null) {
      partner = Beans.get(PartnerRepository.class).find(partner.getId());
    }
    BankDetails defaultBankDetails =
        Beans.get(BankDetailsService.class)
            .getDefaultCompanyBankDetails(company, paymentMode, partner);
    response.setValue("companyBankDetails", defaultBankDetails);
  }

  public void enableEditOrder(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder =
        Beans.get(SaleOrderRepository.class)
            .find(request.getContext().asType(SaleOrder.class).getId());

    try {
      Beans.get(SaleOrderService.class).enableEditOrder(saleOrder);
      response.setReload(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void validateChanges(ActionRequest request, ActionResponse response) {
    try {
      SaleOrder saleOrderView = request.getContext().asType(SaleOrder.class);
      SaleOrder saleOrder = saleOrderRepo.find(saleOrderView.getId());
      Beans.get(SaleOrderService.class).validateChanges(saleOrder, saleOrderView);
      response.setValues(saleOrderView);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
      response.setReload(true);
    }
  }

  /**
   * Called on printing settings select. Set the domain for {@link SaleOrder#printingSettings}
   *
   * @param request
   * @param response
   */
  public void filterPrintingSettings(ActionRequest request, ActionResponse response) {
    try {
      SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
      List<PrintingSettings> printingSettingsList =
          Beans.get(TradingNameService.class)
              .getPrintingSettingsList(saleOrder.getTradingName(), saleOrder.getCompany());
      String domain =
          String.format(
              "self.id IN (%s)",
              !printingSettingsList.isEmpty()
                  ? StringTool.getIdListString(printingSettingsList)
                  : "0");
      response.setAttr("printingSettings", "domain", domain);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  /**
   * Called on trading name change. Set the default value for {@link SaleOrder#printingSettings}
   *
   * @param request
   * @param response
   */
  public void fillDefaultPrintingSettings(ActionRequest request, ActionResponse response) {
    try {
      SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
      response.setValue(
          "printingSettings",
          Beans.get(TradingNameService.class)
              .getDefaultPrintingSettings(saleOrder.getTradingName(), saleOrder.getCompany()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  /**
   * Called from sale order form view on partner change. Get the default price list for the sale
   * order. Call {@link PartnerPriceListService#getDefaultPriceList(Partner, int)}.
   *
   * @param request
   * @param response
   */
  public void fillPriceList(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    response.setValue(
        "priceList",
        saleOrder.getClientPartner() != null
            ? Beans.get(PartnerPriceListService.class)
                .getDefaultPriceList(saleOrder.getClientPartner(), PriceListRepository.TYPE_SALE)
            : null);
  }

  /**
   * Called from sale order view on price list select. Call {@link
   * PartnerPriceListService#getPriceListDomain(Partner, int)}.
   *
   * @param request
   * @param response
   */
  public void changePriceListDomain(ActionRequest request, ActionResponse response) {
    SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
    String domain =
        Beans.get(PartnerPriceListService.class)
            .getPriceListDomain(saleOrder.getClientPartner(), PriceListRepository.TYPE_SALE);
    response.setAttr("priceList", "domain", domain);
  }
}
