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
package com.axelor.apps.account.db.repo;

import com.axelor.apps.account.db.AccountingSituation;
import com.axelor.apps.account.service.AccountingSituationService;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.PartnerBaseRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.service.app.AppService;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.util.List;
import javax.persistence.PersistenceException;

public class PartnerAccountRepository extends PartnerBaseRepository {

  @Inject private AppService appService;

  @Override
  public Partner save(Partner partner) {
    try {

      if (partner.getId() == null) {
        partner = super.save(partner);
      }
      if (!partner.getIsContact() && appService.isApp("account")) {
        List<AccountingSituation> accountingSituationList =
            Beans.get(AccountingSituationService.class)
                .createAccountingSituation(
                    Beans.get(PartnerRepository.class).find(partner.getId()));

        if (accountingSituationList != null) {
          partner.setAccountingSituationList(accountingSituationList);
        }
      }

      return super.save(partner);
    } catch (Exception e) {
      throw new PersistenceException(e.getLocalizedMessage());
    }
  }

  @Override
  public Partner copy(Partner partner, boolean deep) {

    Partner copy = super.copy(partner, deep);

    if (appService.isApp("account")) {
      copy.setAccountingSituationList(null);
    }

    return copy;
  }
}
