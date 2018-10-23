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
package com.axelor.apps.purchase.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.purchase.db.repo.PurchaseOrderManagementRepository;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.purchase.service.AddressServicePurchaseImpl;
import com.axelor.apps.purchase.service.PurchaseOrderService;
import com.axelor.apps.purchase.service.PurchaseOrderServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.purchase.service.PurchaseProductServiceImpl;
import com.axelor.apps.purchase.service.PurchaseRequestService;
import com.axelor.apps.purchase.service.PurchaseRequestServiceImpl;
import com.axelor.apps.purchase.service.app.AppPurchaseService;
import com.axelor.apps.purchase.service.app.AppPurchaseServiceImpl;

public class PurchaseModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(AddressServicePurchaseImpl.class);
    bind(PurchaseOrderRepository.class).to(PurchaseOrderManagementRepository.class);
    bind(PurchaseOrderService.class).to(PurchaseOrderServiceImpl.class);
    bind(AppPurchaseService.class).to(AppPurchaseServiceImpl.class);
    bind(PurchaseRequestService.class).to(PurchaseRequestServiceImpl.class);
    bind(PurchaseProductService.class).to(PurchaseProductServiceImpl.class);
  }
}
