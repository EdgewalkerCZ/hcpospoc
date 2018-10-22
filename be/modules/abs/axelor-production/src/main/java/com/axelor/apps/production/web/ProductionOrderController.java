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
package com.axelor.apps.production.web;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.production.db.BillOfMaterial;
import com.axelor.apps.production.db.ProductionOrder;
import com.axelor.apps.production.db.repo.BillOfMaterialRepository;
import com.axelor.apps.production.db.repo.ProductionOrderRepository;
import com.axelor.apps.production.exceptions.IExceptionMessage;
import com.axelor.apps.production.service.ProductionOrderService;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Singleton
public class ProductionOrderController {

  @Inject ProductionOrderService productionOrderService;

  @Inject private BillOfMaterialRepository billOfMaterialRepo;

  @Inject private ProductionOrderRepository productionOrderRepo;

  @Inject private ProductRepository productRepo;

  @Inject private AppBaseService appBaseService;

  @SuppressWarnings("unchecked")
  public void addManufOrder(ActionRequest request, ActionResponse response) throws AxelorException {

    Context context = request.getContext();

    if (context.get("qty") == null
        || new BigDecimal(context.get("qty").toString()).compareTo(BigDecimal.ZERO) <= 0) {
      response.setFlash(I18n.get(IExceptionMessage.PRODUCTION_ORDER_3) + "!");
    } else if (context.get("billOfMaterial") == null) {
      response.setFlash(I18n.get(IExceptionMessage.PRODUCTION_ORDER_4) + "!");
    } else {
      Map<String, Object> bomContext = (Map<String, Object>) context.get("billOfMaterial");
      BillOfMaterial billOfMaterial =
          billOfMaterialRepo.find(((Integer) bomContext.get("id")).longValue());

      BigDecimal qty = new BigDecimal(context.get("qty").toString());

      Product product = null;

      if (context.get("product") != null) {
        Map<String, Object> productContext = (Map<String, Object>) context.get("product");
        product = productRepo.find(((Integer) productContext.get("id")).longValue());
      } else {
        product = billOfMaterial.getProduct();
      }

      ZonedDateTime startDateT;
      if (context.containsKey("_startDate") && context.get("_startDate") != null) {
        startDateT =
            ZonedDateTime.parse(
                (CharSequence) context.get("_startDate"),
                DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault()));
      } else {
        startDateT = appBaseService.getTodayDateTime();
      }

      ProductionOrder productionOrder =
          productionOrderRepo.find(Long.parseLong(request.getContext().get("_id").toString()));

      if (billOfMaterial.getProdProcess() != null) {
        productionOrderService.addManufOrder(
            productionOrder, product, billOfMaterial, qty, startDateT.toLocalDateTime());
      } else {
        response.setError(I18n.get(IExceptionMessage.MANUF_ORDER_NO_GENERATION));
      }

      response.setCanClose(true);
    }
  }
}
