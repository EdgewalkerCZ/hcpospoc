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
package com.axelor.apps.base.service;

import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.base.db.Bank;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.tool.StringTool;
import org.iban4j.CountryCode;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

public class BankDetailsServiceImpl implements BankDetailsService {

  /**
   * This method allows to extract information from iban Update following fields :
   *
   * <ul>
   *   <li>BankCode
   *   <li>SortCode
   *   <li>AccountNbr
   *   <li>BbanKey
   *   <li>Bank
   * </ul>
   *
   * @param bankDetails
   * @return BankDetails
   */
  @Override
  public BankDetails detailsIban(BankDetails bankDetails) {

    if (bankDetails.getIban() != null) {

      bankDetails.setBankCode(StringTool.extractStringFromRight(bankDetails.getIban(), 23, 5));
      bankDetails.setSortCode(StringTool.extractStringFromRight(bankDetails.getIban(), 18, 5));
      bankDetails.setAccountNbr(StringTool.extractStringFromRight(bankDetails.getIban(), 13, 11));
      bankDetails.setBbanKey(StringTool.extractStringFromRight(bankDetails.getIban(), 2, 2));
    }
    return bankDetails;
  }

  /**
   * Method allowing to create a bank details
   *
   * @param accountNbr
   * @param bankCode
   * @param bbanKey
   * @param bank
   * @param ownerName
   * @param partner
   * @param sortCode
   * @return
   */
  @Override
  public BankDetails createBankDetails(
      String accountNbr,
      String bankCode,
      String bbanKey,
      Bank bank,
      String ownerName,
      Partner partner,
      String sortCode) {
    BankDetails bankDetails = new BankDetails();

    bankDetails.setAccountNbr(accountNbr);
    bankDetails.setBankCode(bankCode);
    bankDetails.setBbanKey(bbanKey);
    bankDetails.setBank(bank);
    bankDetails.setOwnerName(ownerName);
    bankDetails.setPartner(partner);
    bankDetails.setSortCode(sortCode);

    return bankDetails;
  }

  /**
   * In this implementation, we do not have the O2M in paymentMode. The bank details is from the
   * company.
   *
   * @param company
   * @param paymentMode
   * @return
   */
  @Override
  public String createCompanyBankDetailsDomain(Company company, PaymentMode paymentMode) {
    if (company == null) {
      return "self.id IN (0)";
    }

    return "self.id IN ("
        + StringTool.getIdListString(company.getBankDetailsSet())
        + ") AND self.active = true";
  }

  @Override
  public BankDetails getDefaultCompanyBankDetails(
      Company company, PaymentMode paymentMode, Partner partner) {
    BankDetails bankDetails = company.getDefaultBankDetails();
    if (bankDetails != null && bankDetails.getActive()) {
      return company.getDefaultBankDetails();
    } else {
      return null;
    }
  }

  public void validateIban(String iban)
      throws IbanFormatException, InvalidCheckDigitException, UnsupportedCountryException {
    CountryCode countryCode = CountryCode.getByCode(IbanUtil.getCountryCode(iban));
    if (countryCode == null) {
      throw new UnsupportedCountryException("Country code is not supported.");
    }
    if (IbanUtil.isSupportedCountry(countryCode)) {
      IbanUtil.validate(iban);
    }
  }
}
