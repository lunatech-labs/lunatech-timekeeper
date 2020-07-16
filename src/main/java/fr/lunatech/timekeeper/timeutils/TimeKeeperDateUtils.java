package fr.lunatech.timekeeper.timeutils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.function.Predicate;

/**
 * @author created by N.Martignole, Lunatech, on 2020-06-10.
 */
public class TimeKeeperDateUtils {

    private TimeKeeperDateUtils(){ }

    public static void validateMonth(final Integer monthNumber, final Integer year){
        if(year < 1970) throw new IllegalStateException("year should be after 1970 due to TimeStamp limitation");
        if(monthNumber<1 || monthNumber>12) throw new IllegalStateException("monthnumber must be an Int value in range 1 to 12");
    }

    public static void validateWeek(final Integer weekNumber, final Integer year){
        if(year < 1970) throw new IllegalStateException("year should be after 1970 due to TimeStamp limitation");
        Integer lastWeekNumber = getWeekNumberFromDate(LocalDate.of(year, 12, 28));
        if(weekNumber<1 || weekNumber>lastWeekNumber) throw new IllegalStateException("weeknumber must be an Int value in range 1 to "+ lastWeekNumber);
    }

    /**
     * Returns the first Monday as a LocalDate from a weekNumber
     *
     * @param weekNumber is a value between 1 to 53 (the year 2020 has 53 weeks according to IsoWeek)
     * @return the Monday of this week number in the year
     */
    public static LocalDate getFirstDayOfWeekFromWeekNumber(final Integer year, final Integer weekNumber) {
        validateWeek(weekNumber, year);
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
        return inputDate.with(fieldISO, 1);
    }

    /**
     * Adjust a date to the last day of week, according to the current Locale.
     * For US Calendar, the week starts on Sunday. For European calendar, it is the Monday.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    public static LocalDate adjustToLastDayOfWeek(final LocalDate inputDate) {
        // We can use either the User Locale or the Organization Locale.
        // But for the time being I set it to FR so the last day of week is a Monday
        // This code will need to be updated later, to adjust user preferences and maybe relies on the AuthenticationContext
        TemporalField fieldISO = WeekFields.ISO.dayOfWeek();
        LocalDate lastDayOfWeek = inputDate.with(fieldISO, 7);
        return lastDayOfWeek;
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

    public static Boolean isSameWeekAndYear(final LocalDate date1, final LocalDate date2) {
        boolean isSameWeek = getWeekNumberFromDate(date1).equals(getWeekNumberFromDate(date2));
        boolean isSameYear = date1.getYear() == date2.getYear();
        return isSameWeek && isSameYear;
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

    /**
     * Return a predicate that test if the input date is included in the six weeks of the month
     * The calendars view usually displays six weeks for the month view
     * @param year
     * @param monthNumber
     * @return a predicate to test if the input date is included in the six weeks of the month
     */
    public static Predicate<LocalDate> isIncludedInSixWeeksFromMonth(final Integer year, final Integer monthNumber) {
        validateMonth(monthNumber, year);
        LocalDate firstDayOfMonth = LocalDate.of(year, monthNumber, 1);
        Integer weekNumber = getWeekNumberFromDate(firstDayOfMonth);
        LocalDate firstDayOfFirstWeek = getFirstDayOfWeekFromWeekNumber(year, weekNumber);
        LocalDate lastDayOfLastWeek = adjustToLastDayOfWeek(firstDayOfFirstWeek.plusWeeks(5));
        return inputDate -> inputDate.isAfter(firstDayOfFirstWeek.minusDays(1)) && inputDate.isBefore(lastDayOfLastWeek);
    }
}
