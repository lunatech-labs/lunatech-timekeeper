package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;
import java.util.List;

public interface Calendar {
    List<LocalDate> getBusinessDays();

    List<PublicHoliday> getPublicHolidays();

    List<PublicHoliday> getPublicHolidaysForWeekNumber(final Integer weekNumber);

    List<PublicHoliday> getPublicHolidaysForMonthNumber(final Integer monthNumber);

}
