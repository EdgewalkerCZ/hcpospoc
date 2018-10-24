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
package com.axelor.apps.businessproduction.service;

import com.axelor.apps.base.service.UnitConversionService;
import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.production.db.CostSheetLine;
import com.axelor.apps.production.db.ProdHumanResource;
import com.axelor.apps.production.db.repo.BillOfMaterialRepository;
import com.axelor.apps.production.service.CostSheetLineService;
import com.axelor.apps.production.service.CostSheetServiceImpl;
import com.axelor.apps.production.service.app.AppProductionService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CostSheetServiceBusinessImpl extends CostSheetServiceImpl {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  public CostSheetServiceBusinessImpl(
      AppProductionService appProductionService,
      UnitConversionService unitConversionService,
      CostSheetLineService costSheetLineService,
      BillOfMaterialRepository billOfMaterialRepo) {

    super(appProductionService, unitConversionService, costSheetLineService, billOfMaterialRepo);
  }

  @Override
  protected void _computeHumanResourceCost(
      ProdHumanResource prodHumanResource,
      int priority,
      int bomLevel,
      CostSheetLine parentCostSheetLine)
      throws AxelorException {

    Employee employee = prodHumanResource.getEmployee();

    if (employee != null) {

      BigDecimal durationHours =
          new BigDecimal(prodHumanResource.getDuration())
              .divide(
                  BigDecimal.valueOf(3600),
                  appProductionService.getNbDecimalDigitForUnitPrice(),
                  BigDecimal.ROUND_HALF_EVEN);

      costSheet.addCostSheetLineListItem(
          costSheetLineService.createWorkCenterCostSheetLine(
              prodHumanResource.getWorkCenter(),
              priority,
              bomLevel,
              parentCostSheetLine,
              durationHours,
              employee.getHourlyRate().multiply(durationHours),
              hourUnit));

    } else {

      super._computeHumanResourceCost(prodHumanResource, priority, bomLevel, parentCostSheetLine);
    }
  }
}
