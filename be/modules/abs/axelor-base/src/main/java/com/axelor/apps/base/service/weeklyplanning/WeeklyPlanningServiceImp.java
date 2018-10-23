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
package com.axelor.apps.base.service.weeklyplanning;

import com.axelor.apps.base.db.DayPlanning;
import com.axelor.apps.base.db.WeeklyPlanning;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;
import java.util.List;

public class WeeklyPlanningServiceImp implements WeeklyPlanningService {

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public WeeklyPlanning initPlanning(WeeklyPlanning planning) {
    String[] dayTab =
        new String[] {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    for (int i = 0; i < dayTab.length; i++) {
      DayPlanning day = new DayPlanning();
      day.setName(dayTab[i]);
      planning.addWeekDay(day);
    }
    return planning;
  }

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public WeeklyPlanning checkPlanning(WeeklyPlanning planning) throws AxelorException {

    List<DayPlanning> listDay = planning.getWeekDays();
    for (DayPlanning dayPlanning : listDay) {

      if (dayPlanning.getMorningFrom() != null
          && dayPlanning.getMorningTo() != null
          && dayPlanning.getMorningFrom().isAfter(dayPlanning.getMorningTo())) {

        String message = messageInCheckPlanning(IExceptionMessage.WEEKLY_PLANNING_1, dayPlanning);
        throw new AxelorException(TraceBackRepository.CATEGORY_INCONSISTENCY, message);
      }

      if (dayPlanning.getMorningTo() != null
          && dayPlanning.getAfternoonFrom() != null
          && dayPlanning.getMorningTo().isAfter(dayPlanning.getAfternoonFrom())) {

        String message = messageInCheckPlanning(IExceptionMessage.WEEKLY_PLANNING_2, dayPlanning);
        throw new AxelorException(TraceBackRepository.CATEGORY_INCONSISTENCY, message);
      }

      if (dayPlanning.getAfternoonFrom() != null
          && dayPlanning.getAfternoonTo() != null
          && dayPlanning.getAfternoonFrom().isAfter(dayPlanning.getAfternoonTo())) {

        String message = messageInCheckPlanning(IExceptionMessage.WEEKLY_PLANNING_3, dayPlanning);
        throw new AxelorException(TraceBackRepository.CATEGORY_INCONSISTENCY, message);
      }

      if ((dayPlanning.getMorningFrom() == null && dayPlanning.getMorningTo() != null)
          || (dayPlanning.getMorningTo() == null && dayPlanning.getMorningFrom() != null)
          || (dayPlanning.getAfternoonFrom() == null && dayPlanning.getAfternoonTo() != null)
          || (dayPlanning.getAfternoonTo() == null && dayPlanning.getAfternoonFrom() != null)) {

        String message = messageInCheckPlanning(IExceptionMessage.WEEKLY_PLANNING_4, dayPlanning);
        throw new AxelorException(TraceBackRepository.CATEGORY_INCONSISTENCY, message);
      }
    }
    return planning;
  }

  @Override
  public double workingDayValue(WeeklyPlanning planning, LocalDate date) {
    double value = 0;
    DayPlanning dayPlanning = findDayPlanning(planning, date);
    if (dayPlanning == null) {
      return value;
    }
    if (dayPlanning.getMorningFrom() != null && dayPlanning.getMorningTo() != null) {
      value += 0.5;
    }
    if (dayPlanning.getAfternoonFrom() != null && dayPlanning.getAfternoonTo() != null) {
      value += 0.5;
    }
    return value;
  }

  @Override
  public double workingDayValueWithSelect(
      WeeklyPlanning planning, LocalDate date, boolean morning, boolean afternoon) {
    double value = 0;
    DayPlanning dayPlanning = findDayPlanning(planning, date);
    if (dayPlanning == null) {
      return value;
    }
    if (morning && dayPlanning.getMorningFrom() != null && dayPlanning.getMorningTo() != null) {
      value += 0.5;
    }
    if (afternoon
        && dayPlanning.getAfternoonFrom() != null
        && dayPlanning.getAfternoonTo() != null) {
      value += 0.5;
    }
    return value;
  }

  public DayPlanning findDayPlanning(WeeklyPlanning planning, LocalDate date) {
    int dayOfWeek = date.getDayOfWeek().getValue();
    switch (dayOfWeek) {
      case 1:
        return findDayWithName(planning, "monday");

      case 2:
        return findDayWithName(planning, "tuesday");

      case 3:
        return findDayWithName(planning, "wednesday");

      case 4:
        return findDayWithName(planning, "thursday");

      case 5:
        return findDayWithName(planning, "friday");

      case 6:
        return findDayWithName(planning, "saturday");

      case 7:
        return findDayWithName(planning, "sunday");

      default:
        return findDayWithName(planning, "null");
    }
  }

  public DayPlanning findDayWithName(WeeklyPlanning planning, String name) {
    List<DayPlanning> dayPlanningList = planning.getWeekDays();
    for (DayPlanning dayPlanning : dayPlanningList) {
      if (dayPlanning.getName().equals(name)) {
        return dayPlanning;
      }
    }
    return null;
  }

  public String messageInCheckPlanning(String message, DayPlanning dayPlanning) {
    String dayPlanningName = dayPlanning.getName();
    return String.format(
        I18n.get(message),
        I18n.get(Character.toUpperCase(dayPlanningName.charAt(0)) + dayPlanningName.substring(1))
            .toLowerCase()); // Because day of week are traduced with a upperCase at the first
    // letter
  }
}
