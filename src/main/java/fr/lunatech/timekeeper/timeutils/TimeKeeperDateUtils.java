package fr.lunatech.timekeeper.timeutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Locale;

/**
 * @author created by N.Martignole, Lunatech, on 2020-06-10.
 */
public class TimeKeeperDateUtils {
    /**
     * Returns the first Monday as a LocalDate from a weekNumber
     *
     * @param weekNumber is a value between 1 to 53 (the year 2020 has 53 weeks according to IsoWeek)
     * @return the Monday of this week number in the year
     */
    public static LocalDate getFirstDayOfWeekFromWeekNumber(final Integer year, final Integer weekNumber) {
        if(year < 1970 ) throw new IllegalStateException("year should be a 4 digits value");
        if(weekNumber<1 || weekNumber>53) throw new IllegalStateException("weeknumber must be an Int value in range 1 to 53");
        return LocalDate.of(year,1,1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Adjust a date to the first day of week, according to the current Locale.
     * For US Calendar, the week starts on Sunday. For European calendar, it is the Monday.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    public static LocalDate adjustToFirstDayOfWeek(final LocalDate inputDate) {
        // We can use either the User Locale or the Organization Locale.
        // But for the time being I set it to FR so the first day of week is a Monday
        // This code will need to be updated later, to adjust user preferences and maybe relies on the AuthenticationContext
        TemporalField fieldISO = WeekFields.ISO.dayOfWeek();
        LocalDate firstDayOfWeek = inputDate.with(fieldISO, 1);
        return firstDayOfWeek;
    }

    /**
     * Returns the weekNumber between 1 to 53 from a LocalDate
     * @param date is a valid LocalDate, no TimeZone
     * @return a weekNumber as Integer
     */
    public static Integer getWeekNumberFromDate(final LocalDate date) {
        TemporalField woy = WeekFields.ISO.weekOfWeekBasedYear();
        return date.get(woy);
    }

    /**
     * Format and return the date as a String
     * @param date is a valid date
     * @return a String ISO LOCAL DATE
     */
    public static String formatToString(final LocalDate date){
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }

    /**
     * Returns a formatted DateTime as ISO LOCAL DATE_TIME
     * @param dateTime
     * @return
     */
    public static String formatToString(final LocalDateTime dateTime) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
    }
}
