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
package com.axelor.apps.stock.db.repo;

import com.axelor.apps.base.db.Sequence;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.stock.db.LogisticalForm;
import com.axelor.apps.stock.exception.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.common.base.Strings;
import javax.persistence.PersistenceException;

public class LogisticalFormStockRepository extends LogisticalFormRepository {

  @Override
  public LogisticalForm save(LogisticalForm logisticalForm) {
    try {
      if (Strings.isNullOrEmpty(logisticalForm.getDeliveryNumber())
          && logisticalForm.getCompany() != null) {
        String sequenceNumber =
            Beans.get(SequenceService.class)
                .getSequenceNumber("logisticalForm", logisticalForm.getCompany());
        if (Strings.isNullOrEmpty(sequenceNumber)) {
          throw new AxelorException(
              Sequence.class,
              TraceBackRepository.CATEGORY_NO_VALUE,
              I18n.get(IExceptionMessage.LOGISTICAL_FORM_MISSING_SEQUENCE),
              logisticalForm.getCompany().getName());
        }
        logisticalForm.setDeliveryNumber(sequenceNumber);
      }

      return super.save(logisticalForm);
    } catch (Exception e) {
      throw new PersistenceException(e.getLocalizedMessage());
    }
  }
}
