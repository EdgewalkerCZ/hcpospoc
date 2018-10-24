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
package com.axelor.apps.marketing.service;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.crm.db.Event;
import com.axelor.apps.crm.db.Lead;
import com.axelor.apps.crm.db.repo.EventRepository;
import com.axelor.apps.marketing.db.Campaign;
import com.axelor.apps.marketing.exception.IExceptionMessage;
import com.axelor.apps.message.db.Template;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import javax.mail.MessagingException;

public class CampaignServiceImpl implements CampaignService {

  protected TemplateMessageServiceMarketingImpl templateMessageServiceMarketingImpl;

  protected EventRepository eventRepo;

  @Inject
  public CampaignServiceImpl(
      TemplateMessageServiceMarketingImpl templateMessageServiceMarketingImpl,
      EventRepository eventRepo) {
    this.templateMessageServiceMarketingImpl = templateMessageServiceMarketingImpl;
    this.eventRepo = eventRepo;
  }

  public MetaFile sendEmail(Campaign campaign) {

    String errorPartners = "";
    String errorLeads = "";

    templateMessageServiceMarketingImpl.setEmailAccount(campaign.getEmailAccount());

    if (campaign.getPartnerTemplate() != null) {
      errorPartners = sendToPartners(campaign.getPartnerSet(), campaign.getPartnerTemplate());
    }

    if (campaign.getLeadTemplate() != null) {
      errorLeads = sendToLeads(campaign.getLeadSet(), campaign.getLeadTemplate());
    }

    if (errorPartners.isEmpty() && errorLeads.isEmpty()) {
      return null;
    }

    return generateLog(errorPartners, errorLeads, campaign.getEmailLog(), campaign.getId());
  }

  protected String sendToPartners(Set<Partner> partnerSet, Template template) {

    StringBuilder errors = new StringBuilder();

    for (Partner partner : partnerSet) {

      try {
        templateMessageServiceMarketingImpl.generateAndSendMessage(partner, template);
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | MessagingException
          | IOException
          | AxelorException e) {
        errors.append(partner.getName() + "\n");
        e.printStackTrace();
      }
    }

    return errors.toString();
  }

  protected String sendToLeads(Set<Lead> leadSet, Template template) {

    StringBuilder errors = new StringBuilder();

    for (Lead lead : leadSet) {

      try {
        templateMessageServiceMarketingImpl.generateAndSendMessage(lead, template);
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | MessagingException
          | IOException
          | AxelorException e) {
        errors.append(lead.getName() + "\n");
        e.printStackTrace();
      }
    }

    return errors.toString();
  }

  protected MetaFile generateLog(
      String errorPartners, String errorLeads, MetaFile metaFile, Long campaignId) {

    if (metaFile == null) {
      metaFile = new MetaFile();
      metaFile.setFileName("EmailLog" + campaignId + ".text");
    }

    StringBuilder builder = new StringBuilder();
    builder.append(I18n.get(IExceptionMessage.EMAIL_ERROR1));
    builder.append("\n");
    if (!errorPartners.isEmpty()) {
      builder.append(I18n.get("Partners") + ":\n");
      builder.append(errorPartners);
    }
    if (!errorLeads.isEmpty()) {
      builder.append(I18n.get("Leads") + ":\n");
      builder.append(errorLeads);
    }

    ByteArrayInputStream stream = new ByteArrayInputStream(builder.toString().getBytes());

    try {
      return Beans.get(MetaFiles.class).upload(stream, metaFile.getFileName());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public void generateEvents(Campaign campaign) {

    for (Partner partner : campaign.getPartnerSet()) {
      Event event = new Event();
      event.setPartner(partner);
      event.setUser(campaign.getEventUser());
      event.setSubject(campaign.getSubject());
      event.setTypeSelect(campaign.getEventTypeSelect());
      event.setStartDateTime(campaign.getEventStartDateTime());
      event.setDuration(campaign.getDuration());
      event.setTeam(campaign.getTeam());
      event.setCampaign(campaign);
      event.setStatusSelect(1);
      eventRepo.save(event);
    }

    for (Lead lead : campaign.getLeadSet()) {
      Event event = new Event();
      event.setLead(lead);
      event.setUser(campaign.getEventUser());
      event.setSubject(campaign.getSubject());
      event.setTypeSelect(campaign.getEventTypeSelect());
      event.setStartDateTime(campaign.getEventStartDateTime());
      event.setDuration(campaign.getDuration());
      event.setTeam(campaign.getTeam());
      event.setCampaign(campaign);
      event.setStatusSelect(1);
      eventRepo.save(event);
    }
  }

  @Transactional(rollbackOn = {AxelorException.class, Exception.class})
  public void generateTargets(Campaign campaign) {

    TargetListService targetListService = Beans.get(TargetListService.class);

    Set<Partner> partnerSet = targetListService.getAllPartners(campaign.getTargetModelSet());
    Set<Lead> leadSet = targetListService.getAllLeads(campaign.getTargetModelSet());

    campaign.setPartnerSet(partnerSet);
    campaign.setLeadSet(leadSet);
  }
}
