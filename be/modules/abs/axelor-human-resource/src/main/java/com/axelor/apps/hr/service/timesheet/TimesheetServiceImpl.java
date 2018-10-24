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
package com.axelor.apps.hr.service.timesheet;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.service.invoice.generator.InvoiceLineGenerator;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.DayPlanning;
import com.axelor.apps.base.db.EventsPlanning;
import com.axelor.apps.base.db.EventsPlanningLine;
import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.PriceListLine;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.WeeklyPlanning;
import com.axelor.apps.base.db.repo.AppBaseRepository;
import com.axelor.apps.base.db.repo.PriceListLineRepository;
import com.axelor.apps.base.db.repo.PriceListRepository;
import com.axelor.apps.base.service.PartnerPriceListService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.UnitConversionService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.hr.db.HRConfig;
import com.axelor.apps.hr.db.LeaveRequest;
import com.axelor.apps.hr.db.Timesheet;
import com.axelor.apps.hr.db.TimesheetLine;
import com.axelor.apps.hr.db.repo.EmployeeRepository;
import com.axelor.apps.hr.db.repo.LeaveRequestRepository;
import com.axelor.apps.hr.db.repo.TimesheetLineRepository;
import com.axelor.apps.hr.db.repo.TimesheetRepository;
import com.axelor.apps.hr.exception.IExceptionMessage;
import com.axelor.apps.hr.service.app.AppHumanResourceService;
import com.axelor.apps.hr.service.config.HRConfigService;
import com.axelor.apps.hr.service.user.UserHrService;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.apps.project.db.Project;
import com.axelor.apps.project.db.repo.ProjectRepository;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.db.mapper.Mapper;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;

/** @author axelor */
public class TimesheetServiceImpl implements TimesheetService {

  protected PriceListService priceListService;

  protected AppHumanResourceService appHumanResourceService;

  protected HRConfigService hrConfigService;

  protected TemplateMessageService templateMessageService;

  protected ProjectRepository projectRepo;

  protected UserRepository userRepo;

  protected UserHrService userHrService;

  protected TimesheetLineService timesheetLineService;

  @Inject
  public TimesheetServiceImpl(
      PriceListService priceListService,
      AppHumanResourceService appHumanResourceService,
      HRConfigService hrConfigService,
      TemplateMessageService templateMessageService,
      ProjectRepository projectRepo,
      UserRepository userRepo,
      UserHrService userHrService,
      TimesheetLineService timesheetLineService) {
    this.priceListService = priceListService;
    this.appHumanResourceService = appHumanResourceService;
    this.hrConfigService = hrConfigService;
    this.templateMessageService = templateMessageService;
    this.projectRepo = projectRepo;
    this.userRepo = userRepo;
    this.userHrService = userHrService;
    this.timesheetLineService = timesheetLineService;
  }

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public void getTimeFromTask(Timesheet timesheet) throws AxelorException {

    List<TimesheetLine> timesheetLineList =
        TimesheetLineRepository.of(TimesheetLine.class)
            .all()
            .filter(
                "self.user = ?1 AND self.timesheet = null AND self.project != null",
                timesheet.getUser())
            .fetch();
    for (TimesheetLine timesheetLine : timesheetLineList) {
      timesheetLine.setDuration(
          Beans.get(TimesheetLineService.class)
              .computeHoursDuration(timesheet, timesheetLine.getHoursDuration(), false));
      timesheet.addTimesheetLineListItem(timesheetLine);
    }
  }

  @Override
  @Transactional(rollbackOn = {AxelorException.class, RuntimeException.class})
  public void confirm(Timesheet timesheet) throws AxelorException {

    this.validToDate(timesheet);

    timesheet.setStatusSelect(TimesheetRepository.STATUS_CONFIRMED);
    timesheet.setSentDate(appHumanResourceService.getTodayDate());

    if (timesheet.getToDate() == null) {
      List<TimesheetLine> timesheetLineList = timesheet.getTimesheetLineList();
      LocalDate timesheetLineLastDate = timesheetLineList.get(0).getDate();
      for (TimesheetLine timesheetLine : timesheetLineList.subList(1, timesheetLineList.size())) {
        if (timesheetLine.getDate().compareTo(timesheetLineLastDate) > 0) {
          timesheetLineLastDate = timesheetLine.getDate();
        }
      }

      timesheet.setToDate(timesheetLineLastDate);
    }
  }

  @Override
  public Message sendConfirmationEmail(Timesheet timesheet)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, MessagingException, IOException {

    HRConfig hrConfig = hrConfigService.getHRConfig(timesheet.getCompany());

    if (hrConfig.getTimesheetMailNotification()) {
      return templateMessageService.generateAndSendMessage(
          timesheet, hrConfigService.getSentTimesheetTemplate(hrConfig));
    }

    return null;
  }

  @Override
  @Transactional
  public void validate(Timesheet timesheet) {

    timesheet.setStatusSelect(TimesheetRepository.STATUS_VALIDATED);
    timesheet.setValidatedBy(AuthUtils.getUser());
    timesheet.setValidationDate(appHumanResourceService.getTodayDate());
  }

  @Override
  public Message sendValidationEmail(Timesheet timesheet)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, MessagingException, IOException {

    HRConfig hrConfig = hrConfigService.getHRConfig(timesheet.getCompany());

    if (hrConfig.getTimesheetMailNotification()) {

      return templateMessageService.generateAndSendMessage(
          timesheet, hrConfigService.getValidatedTimesheetTemplate(hrConfig));
    }

    return null;
  }

  @Override
  @Transactional
  public void refuse(Timesheet timesheet) {

    timesheet.setStatusSelect(TimesheetRepository.STATUS_REFUSED);
    timesheet.setRefusedBy(AuthUtils.getUser());
    timesheet.setRefusalDate(appHumanResourceService.getTodayDate());
  }

  @Override
  public Message sendRefusalEmail(Timesheet timesheet)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, MessagingException, IOException {

    HRConfig hrConfig = hrConfigService.getHRConfig(timesheet.getCompany());

    if (hrConfig.getTimesheetMailNotification()) {

      return templateMessageService.generateAndSendMessage(
          timesheet, hrConfigService.getRefusedTimesheetTemplate(hrConfig));
    }

    return null;
  }

  @Override
  @Transactional
  public void cancel(Timesheet timesheet) {
    timesheet.setStatusSelect(TimesheetRepository.STATUS_CANCELED);
  }

  @Override
  @Transactional
  public void draft(Timesheet timesheet) {
    timesheet.setStatusSelect(TimesheetRepository.STATUS_DRAFT);
  }

  @Override
  public Message sendCancellationEmail(Timesheet timesheet)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, MessagingException, IOException {

    HRConfig hrConfig = hrConfigService.getHRConfig(timesheet.getCompany());

    if (hrConfig.getTimesheetMailNotification()) {

      return templateMessageService.generateAndSendMessage(
          timesheet, hrConfigService.getCanceledTimesheetTemplate(hrConfig));
    }

    return null;
  }

  @Override
  public Timesheet generateLines(
      Timesheet timesheet,
      LocalDate fromGenerationDate,
      LocalDate toGenerationDate,
      BigDecimal logTime,
      Project project,
      Product product)
      throws AxelorException {

    TimesheetLineService timesheetLineService = Beans.get(TimesheetLineService.class);
    User user = timesheet.getUser();
    Employee employee = user.getEmployee();

    if (fromGenerationDate == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          I18n.get(IExceptionMessage.TIMESHEET_FROM_DATE));
    }
    if (toGenerationDate == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          I18n.get(IExceptionMessage.TIMESHEET_TO_DATE));
    }
    if (product == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          I18n.get(IExceptionMessage.TIMESHEET_PRODUCT));
    }
    if (employee == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.LEAVE_USER_EMPLOYEE),
          user.getName());
    }
    WeeklyPlanning planning = user.getEmployee().getWeeklyPlanning();
    if (planning == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.TIMESHEET_EMPLOYEE_DAY_PLANNING),
          user.getName());
    }
    List<DayPlanning> dayPlanningList = planning.getWeekDays();

    LocalDate fromDate = fromGenerationDate;
    LocalDate toDate = toGenerationDate;
    Map<Integer, String> correspMap = new HashMap<>();
    correspMap.put(1, "monday");
    correspMap.put(2, "tuesday");
    correspMap.put(3, "wednesday");
    correspMap.put(4, "thursday");
    correspMap.put(5, "friday");
    correspMap.put(6, "saturday");
    correspMap.put(7, "sunday");

    // Leaving list
    List<LeaveRequest> leaveList =
        LeaveRequestRepository.of(LeaveRequest.class)
            .all()
            .filter("self.user = ?1 AND (self.statusSelect = 2 OR self.statusSelect = 3)", user)
            .fetch();

    // Public holidays list
    EventsPlanning publicHolidayEventsPlanning = employee.getPublicHolidayEventsPlanning();
    List<EventsPlanningLine> publicHolidayList;
    if (publicHolidayEventsPlanning == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.TIMESHEET_EMPLOYEE_PUBLIC_HOLIDAY_EVENTS_PLANNING),
          user.getName());
    } else {
      publicHolidayList = employee.getPublicHolidayEventsPlanning().getEventsPlanningLineList();
    }

    while (!fromDate.isAfter(toDate)) {
      DayPlanning dayPlanningCurr = new DayPlanning();
      for (DayPlanning dayPlanning : dayPlanningList) {
        if (dayPlanning.getName().equals(correspMap.get(fromDate.getDayOfWeek().getValue()))) {
          dayPlanningCurr = dayPlanning;
          break;
        }
      }
      if (dayPlanningCurr.getMorningFrom() != null
          || dayPlanningCurr.getMorningTo() != null
          || dayPlanningCurr.getAfternoonFrom() != null
          || dayPlanningCurr.getAfternoonTo() != null) {
        /*Check if the day is not a leaving day */
        boolean noLeave = true;
        if (leaveList != null) {
          for (LeaveRequest leave : leaveList) {
            if ((leave.getFromDate().isBefore(fromDate) && leave.getToDate().isAfter(fromDate))
                || leave.getFromDate().isEqual(fromDate)
                || leave.getToDate().isEqual(fromDate)) {
              noLeave = false;
              break;
            }
          }
        }

        /*Check if the day is not a public holiday */
        boolean noPublicHoliday = true;
        if (publicHolidayList != null) {
          for (EventsPlanningLine publicHoliday : publicHolidayList) {
            if (publicHoliday.getDate().isEqual(fromDate)) {
              noPublicHoliday = false;
              break;
            }
          }
        }

        if (noLeave && noPublicHoliday) {
          TimesheetLine timesheetLine =
              timesheetLineService.createTimesheetLine(
                  project,
                  product,
                  user,
                  fromDate,
                  timesheet,
                  timesheetLineService.computeHoursDuration(timesheet, logTime, true),
                  "");
          timesheetLine.setDuration(logTime);
        }
      }
      fromDate = fromDate.plusDays(1);
    }
    return timesheet;
  }

  @Override
  public LocalDate getFromPeriodDate() {
    Timesheet timesheet =
        Beans.get(TimesheetRepository.class)
            .all()
            .filter("self.user = ?1 ORDER BY self.toDate DESC", AuthUtils.getUser())
            .fetchOne();
    if (timesheet != null) {
      return timesheet.getToDate();
    } else {
      return null;
    }
  }

  @Override
  public Timesheet getCurrentTimesheet() {
    Timesheet timesheet =
        Beans.get(TimesheetRepository.class)
            .all()
            .filter(
                "self.statusSelect = ?1 AND self.user.id = ?2",
                TimesheetRepository.STATUS_DRAFT,
                AuthUtils.getUser().getId())
            .order("-id")
            .fetchOne();
    if (timesheet != null) {
      return timesheet;
    } else {
      return null;
    }
  }

  @Override
  public Timesheet getCurrentOrCreateTimesheet() {
    Timesheet timesheet = getCurrentTimesheet();
    if (timesheet == null) {
      timesheet =
          createTimesheet(
              AuthUtils.getUser(), appHumanResourceService.getTodayDateTime().toLocalDate(), null);
    }
    return timesheet;
  }

  @Override
  public Timesheet createTimesheet(User user, LocalDate fromDate, LocalDate toDate) {
    Timesheet timesheet = new Timesheet();

    timesheet.setUser(user);
    Company company = null;
    if (user.getEmployee() != null && user.getEmployee().getMainEmploymentContract() != null) {
      company = user.getEmployee().getMainEmploymentContract().getPayCompany();
    }
    timesheet.setCompany(company);
    timesheet.setFromDate(fromDate);
    timesheet.setStatusSelect(TimesheetRepository.STATUS_DRAFT);
    timesheet.setFullName(computeFullName(timesheet));

    return timesheet;
  }

  @Override
  public List<InvoiceLine> createInvoiceLines(
      Invoice invoice, List<TimesheetLine> timesheetLineList, int priority) throws AxelorException {

    List<InvoiceLine> invoiceLineList = new ArrayList<>();
    int count = 0;
    DateFormat ddmmFormat = new SimpleDateFormat("dd/MM");
    HashMap<String, Object[]> timeSheetInformationsMap = new HashMap<>();
    // Check if a consolidation by product and user must be done
    boolean consolidate = appHumanResourceService.getAppTimesheet().getConsolidateTSLine();

    for (TimesheetLine timesheetLine : timesheetLineList) {
      Object[] tabInformations = new Object[5];
      tabInformations[0] = timesheetLine.getProduct();
      tabInformations[1] = timesheetLine.getUser();
      // Start date
      tabInformations[2] = timesheetLine.getDate();
      // End date, useful only for consolidation
      tabInformations[3] = timesheetLine.getDate();
      tabInformations[4] = timesheetLine.getHoursDuration();

      String key = null;
      if (consolidate) {
        key = timesheetLine.getProduct().getId() + "|" + timesheetLine.getUser().getId();
        if (timeSheetInformationsMap.containsKey(key)) {
          tabInformations = timeSheetInformationsMap.get(key);
          // Update date
          if (timesheetLine.getDate().compareTo((LocalDate) tabInformations[2]) < 0) {
            // If date is lower than start date then replace start date by this one
            tabInformations[2] = timesheetLine.getDate();
          } else if (timesheetLine.getDate().compareTo((LocalDate) tabInformations[3]) > 0) {
            // If date is upper than end date then replace end date by this one
            tabInformations[3] = timesheetLine.getDate();
          }
          tabInformations[4] =
              ((BigDecimal) tabInformations[4]).add(timesheetLine.getHoursDuration());
        } else {
          timeSheetInformationsMap.put(key, tabInformations);
        }
      } else {
        key = String.valueOf(timesheetLine.getId());
        timeSheetInformationsMap.put(key, tabInformations);
      }

      timesheetLine.setInvoiced(true);
    }

    for (Object[] timesheetInformations : timeSheetInformationsMap.values()) {

      String strDate = null;
      Product product = (Product) timesheetInformations[0];
      User user = (User) timesheetInformations[1];
      LocalDate startDate = (LocalDate) timesheetInformations[2];
      LocalDate endDate = (LocalDate) timesheetInformations[3];
      BigDecimal hoursDuration = (BigDecimal) timesheetInformations[4];

      if (consolidate) {
        strDate = ddmmFormat.format(startDate) + " - " + ddmmFormat.format(endDate);
      } else {
        strDate = ddmmFormat.format(startDate);
      }

      invoiceLineList.addAll(
          this.createInvoiceLine(
              invoice, product, user, strDate, hoursDuration, priority * 100 + count));
      count++;
    }

    return invoiceLineList;
  }

  @Override
  public List<InvoiceLine> createInvoiceLine(
      Invoice invoice,
      Product product,
      User user,
      String date,
      BigDecimal hoursDuration,
      int priority)
      throws AxelorException {

    int discountTypeSelect = 1;
    if (product == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.TIMESHEET_PRODUCT));
    }
    BigDecimal price = product.getSalePrice();
    BigDecimal discountAmount = product.getCostPrice();

    BigDecimal qtyConverted =
        Beans.get(UnitConversionService.class)
            .convert(
                appHumanResourceService.getAppBase().getUnitHours(),
                product.getUnit(),
                hoursDuration);

    PriceList priceList =
        Beans.get(PartnerPriceListService.class)
            .getDefaultPriceList(invoice.getPartner(), PriceListRepository.TYPE_SALE);
    if (priceList != null) {
      PriceListLine priceListLine =
          priceListService.getPriceListLine(product, qtyConverted, priceList);
      if (priceListLine != null) {
        discountTypeSelect = priceListLine.getTypeSelect();
      }
      if ((appHumanResourceService.getAppBase().getComputeMethodDiscountSelect()
                  == AppBaseRepository.INCLUDE_DISCOUNT_REPLACE_ONLY
              && discountTypeSelect == PriceListLineRepository.TYPE_REPLACE)
          || appHumanResourceService.getAppBase().getComputeMethodDiscountSelect()
              == AppBaseRepository.INCLUDE_DISCOUNT) {
        Map<String, Object> discounts =
            priceListService.getDiscounts(priceList, priceListLine, price);
        if (discounts != null) {
          discountAmount = (BigDecimal) discounts.get("discountAmount");
          price =
              priceListService.computeDiscount(
                  price, (int) discounts.get("discountTypeSelect"), discountAmount);
        }
      } else {
        Map<String, Object> discounts =
            priceListService.getDiscounts(priceList, priceListLine, price);
        if (discounts != null) {
          discountAmount = (BigDecimal) discounts.get("discountAmount");
          if (discounts.get("price") != null) {
            price = (BigDecimal) discounts.get("price");
          }
        }
      }
    }

    String description = user.getFullName(),
        productName = product.getName() + " " + "(" + date + ")";

    InvoiceLineGenerator invoiceLineGenerator =
        new InvoiceLineGenerator(
            invoice,
            product,
            productName,
            price,
            price,
            price,
            description,
            qtyConverted,
            product.getUnit(),
            null,
            priority,
            discountAmount,
            discountTypeSelect,
            price.multiply(qtyConverted),
            null,
            false) {

          @Override
          public List<InvoiceLine> creates() throws AxelorException {

            InvoiceLine invoiceLine = this.createInvoiceLine();

            List<InvoiceLine> invoiceLines = new ArrayList<>();
            invoiceLines.add(invoiceLine);

            return invoiceLines;
          }
        };

    return invoiceLineGenerator.creates();
  }

  @Override
  @Transactional
  public void computeTimeSpent(Timesheet timesheet) {
    List<TimesheetLine> timesheetLineList = timesheet.getTimesheetLineList();
    for (TimesheetLine timesheetLine : timesheetLineList) {
      Project project = timesheetLine.getProject();
      if (project != null) {
        project.setTimeSpent(
            timesheetLine.getHoursDuration().add(this.computeSubTimeSpent(project)));
        this.computeParentTimeSpent(project);
      }
    }
  }

  @Override
  public BigDecimal computeSubTimeSpent(Project project) {
    BigDecimal sum = BigDecimal.ZERO;
    List<Project> subProjectList =
        Beans.get(ProjectRepository.class).all().filter("self.parentProject = ?1", project).fetch();
    if (subProjectList == null || subProjectList.isEmpty()) {
      return project.getTimeSpent();
    }
    for (Project projectIt : subProjectList) {
      sum = sum.add(this.computeSubTimeSpent(projectIt));
    }
    return sum;
  }

  @Override
  public void computeParentTimeSpent(Project project) {
    Project parentProject = project.getParentProject();
    if (parentProject == null) {
      return;
    }
    parentProject.setTimeSpent(project.getTimeSpent().add(this.computeTimeSpent(parentProject)));
    this.computeParentTimeSpent(parentProject);
  }

  @Override
  public BigDecimal computeTimeSpent(Project project) {
    BigDecimal sum = BigDecimal.ZERO;
    List<TimesheetLine> timesheetLineList =
        Beans.get(TimesheetLineRepository.class)
            .all()
            .filter(
                "self.project = ?1 AND self.timesheet.statusSelect = ?2",
                project,
                TimesheetRepository.STATUS_VALIDATED)
            .fetch();
    for (TimesheetLine timesheetLine : timesheetLineList) {
      sum = sum.add(timesheetLine.getHoursDuration());
    }
    return sum;
  }

  @Override
  public String computeFullName(Timesheet timesheet) {

    User timesheetUser = timesheet.getUser();
    LocalDateTime createdOn = timesheet.getCreatedOn();

    if (timesheetUser != null && createdOn != null) {
      return timesheetUser.getFullName()
          + " "
          + createdOn.getDayOfMonth()
          + "/"
          + createdOn.getMonthValue()
          + "/"
          + timesheet.getCreatedOn().getYear()
          + " "
          + createdOn.getHour()
          + ":"
          + createdOn.getMinute();
    } else if (timesheetUser != null) {
      return timesheetUser.getFullName() + " N°" + timesheet.getId();
    } else {
      return "N°" + timesheet.getId();
    }
  }

  public void validToDate(Timesheet timesheet) throws AxelorException {

    List<TimesheetLine> timesheetLineList = timesheet.getTimesheetLineList();
    List<Integer> listId = new ArrayList<>();
    int count = 0;

    if (timesheet.getFromDate() == null) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          I18n.get(IExceptionMessage.TIMESHEET_NULL_FROM_DATE));
    } else if (timesheet.getToDate() != null) {
      if (timesheetLineList != null && !timesheetLineList.isEmpty()) {
        for (TimesheetLine timesheetLine : timesheetLineList) {
          count++;
          if (timesheetLine.getDate().isAfter(timesheet.getToDate())) {
            listId.add(count);
          } else if (timesheetLine.getDate().isBefore(timesheet.getFromDate())) {
            listId.add(count);
          }
        }
      }
    } else {
      if (timesheetLineList != null && !timesheetLineList.isEmpty()) {
        for (TimesheetLine timesheetLine : timesheetLineList) {
          count++;
          if (timesheetLine.getDate().isBefore(timesheet.getFromDate())) {
            listId.add(count);
          }
        }
      }
    }
    if (!listId.isEmpty()) {
      throw new AxelorException(
          timesheet,
          TraceBackRepository.TYPE_FUNCTIONNAL,
          I18n.get(IExceptionMessage.TIMESHEET_DATE_CONFLICT),
          Joiner.on(",").join(listId));
    }
  }

  @Override
  public List<Map<String, Object>> createDefaultLines(Timesheet timesheet) {

    List<Map<String, Object>> lines = new ArrayList<>();
    User user = timesheet.getUser();
    if (user == null || timesheet.getFromDate() == null) {
      return lines;
    }

    user = userRepo.find(user.getId());

    Product product = userHrService.getTimesheetProduct(user);

    if (product == null) {
      return lines;
    }

    List<Project> projects =
        projectRepo
            .all()
            .filter(
                "self.membersUserSet.id = ?1 and "
                    + "self.imputable = true "
                    + "and self.statusSelect != 3",
                user.getId())
            .fetch();

    for (Project project : projects) {
      TimesheetLine line =
          timesheetLineService.createTimesheetLine(
              project, product, user, timesheet.getFromDate(), timesheet, new BigDecimal(0), null);
      lines.add(Mapper.toMap(line));
    }

    return lines;
  }

  @Override
  public BigDecimal computePeriodTotal(Timesheet timesheet) {
    BigDecimal periodTotal = BigDecimal.ZERO;

    List<TimesheetLine> timesheetLines = timesheet.getTimesheetLineList();

    if (timesheetLines != null) {
      BigDecimal periodTotalTemp;
      for (TimesheetLine timesheetLine : timesheetLines) {
        if (timesheetLine != null) {
          periodTotalTemp = timesheetLine.getHoursDuration();
          if (periodTotalTemp != null) {
            periodTotal = periodTotal.add(periodTotalTemp);
          }
        }
      }
    }

    return periodTotal;
  }

  @Override
  public String getPeriodTotalConvertTitle(Timesheet timesheet) {
    String title = "";
    if (timesheet != null) {
      if (timesheet.getTimeLoggingPreferenceSelect() != null) {
        title = timesheet.getTimeLoggingPreferenceSelect();
      }
    } else {
      title = Beans.get(AppBaseService.class).getAppBase().getTimeLoggingPreferenceSelect();
    }
    switch (title) {
      case EmployeeRepository.TIME_PREFERENCE_DAYS:
        return I18n.get("Days");
      case EmployeeRepository.TIME_PREFERENCE_MINUTES:
        return I18n.get("Minutes");
      default:
        return I18n.get("Hours");
    }
  }

  @Override
  public void createValidateDomainTimesheetLine(
      User user, Employee employee, ActionView.ActionViewBuilder actionView) {

    actionView
        .domain("self.timesheet.company = :_activeCompany AND  self.timesheet.statusSelect = 2")
        .context("_activeCompany", user.getActiveCompany());

    if (employee == null || !employee.getHrManager()) {
      if (employee == null || employee.getManagerUser() == null) {
        actionView
            .domain(
                actionView.get().getDomain()
                    + " AND (self.timesheet.user = :_user OR self.timesheet.user.employee.managerUser = :_user)")
            .context("_user", user);
      } else {
        actionView
            .domain(
                actionView.get().getDomain()
                    + " AND self.timesheet.user.employee.managerUser = :_user")
            .context("_user", user);
      }
    }
  }

  @Override
  public void updateTimeLoggingPreference(Timesheet timesheet) throws AxelorException {
    String timeLoggingPref;
    if (timesheet.getUser() == null || timesheet.getUser().getEmployee() == null) {
      timeLoggingPref = EmployeeRepository.TIME_PREFERENCE_HOURS;
    } else {
      Employee employee = timesheet.getUser().getEmployee();
      timeLoggingPref = employee.getTimeLoggingPreferenceSelect();
    }
    timesheet.setTimeLoggingPreferenceSelect(timeLoggingPref);

    if (timesheet.getTimesheetLineList() != null) {
      for (TimesheetLine timesheetLine : timesheet.getTimesheetLineList()) {
        timesheetLine.setDuration(
            Beans.get(TimesheetLineService.class)
                .computeHoursDuration(timesheet, timesheetLine.getHoursDuration(), false));
      }
    }
  }
}
