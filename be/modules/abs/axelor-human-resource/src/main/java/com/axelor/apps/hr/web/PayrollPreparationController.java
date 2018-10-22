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
package com.axelor.apps.hr.web;

import com.axelor.apps.hr.db.EmploymentContract;
import com.axelor.apps.hr.db.PayrollLeave;
import com.axelor.apps.hr.db.PayrollPreparation;
import com.axelor.apps.hr.db.repo.EmploymentContractRepository;
import com.axelor.apps.hr.db.repo.HrBatchRepository;
import com.axelor.apps.hr.db.repo.PayrollPreparationRepository;
import com.axelor.apps.hr.service.PayrollPreparationService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class PayrollPreparationController {

  @Inject protected PayrollPreparationService payrollPreparationService;

  @Inject protected PayrollPreparationRepository payrollPreparationRepo;

  public void generateFromEmploymentContract(ActionRequest request, ActionResponse response) {

    PayrollPreparation payrollPreparation = request.getContext().asType(PayrollPreparation.class);
    EmploymentContract employmentContract =
        Beans.get(EmploymentContractRepository.class)
            .find(new Long(request.getContext().get("_idEmploymentContract").toString()));

    response.setValues(
        payrollPreparationService.generateFromEmploymentContract(
            payrollPreparation, employmentContract));
  }

  public void fillInPayrollPreparation(ActionRequest request, ActionResponse response)
      throws AxelorException {
    PayrollPreparation payrollPreparation = request.getContext().asType(PayrollPreparation.class);

    List<PayrollLeave> payrollLeaveList =
        payrollPreparationService.fillInPayrollPreparation(payrollPreparation);

    response.setValue("extraHoursLineList", payrollPreparation.getExtraHoursLineList());
    response.setValue("$payrollLeavesList", payrollLeaveList);
    response.setValue("duration", payrollPreparation.getDuration());
    response.setValue("leaveDuration", payrollPreparation.getLeaveDuration());
    response.setValue("expenseAmount", payrollPreparation.getExpenseAmount());
    response.setValue("expenseList", payrollPreparation.getExpenseList());
    response.setValue(
        "otherCostsEmployeeSet",
        payrollPreparation.getEmploymentContract().getOtherCostsEmployeeSet());
    response.setValue(
        "annualGrossSalary", payrollPreparation.getEmploymentContract().getAnnualGrossSalary());
    response.setValue("employeeBonusMgtLineList", payrollPreparation.getEmployeeBonusMgtLineList());
    response.setValue("lunchVoucherNumber", payrollPreparation.getLunchVoucherNumber());
    response.setValue("lunchVoucherMgtLineList", payrollPreparation.getLunchVoucherMgtLineList());
  }

  public void fillInPayrollPreparationLeaves(ActionRequest request, ActionResponse response)
      throws AxelorException {
    PayrollPreparation payrollPreparation = request.getContext().asType(PayrollPreparation.class);

    List<PayrollLeave> payrollLeaveList =
        payrollPreparationService.fillInLeaves(payrollPreparation);

    response.setValue("$payrollLeavesList", payrollLeaveList);
  }

  public void exportPayrollPreparation(ActionRequest request, ActionResponse response)
      throws IOException, AxelorException {

    PayrollPreparation payrollPreparation =
        payrollPreparationRepo.find(request.getContext().asType(PayrollPreparation.class).getId());

    if (payrollPreparation.getExportTypeSelect() == HrBatchRepository.EXPORT_TYPE_STANDARD) {
      response.setExportFile(
          payrollPreparationService.exportSinglePayrollPreparation(payrollPreparation));
    } else if (payrollPreparation.getExportTypeSelect() == HrBatchRepository.EXPORT_TYPE_NIBELIS) {
      response.setExportFile(
          payrollPreparationService.exportNibelisPayrollPreparation(payrollPreparation));
    }
    payrollPreparationService.closePayPeriodIfExported(payrollPreparation);

    response.setReload(true);
  }
}
