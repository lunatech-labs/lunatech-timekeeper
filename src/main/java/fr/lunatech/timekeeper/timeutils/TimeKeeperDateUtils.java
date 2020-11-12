/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.timeutils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author created by N.Martignole, Lunatech, on 2020-06-10.
 */
public class TimeKeeperDateUtils {

    private static final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
            DateTimeFormatter.ISO_TIME);

    private static final LocalTime END_OF_DAY = LocalTime.parse("17:00:00",
            DateTimeFormatter.ISO_TIME);

    private TimeKeeperDateUtils() {
    }

    public static void validateYear(final Integer year) {
        if (year < 1970) throw new IllegalStateException("year should be after 1970 due to TimeStamp limitation");
    }

    public static void validateMonth(final Integer monthNumber) {
        if (monthNumber < 1 || monthNumber > 12)
            throw new IllegalStateException("monthnumber must be an Int value in range 1 to 12");
    }

    public static void validateWeek(final Integer weekNumber, final Integer year) {
        Integer lastWeekNumber = getWeekNumberFromDate(LocalDate.of(year, 12, 28));
        if (weekNumber < 1 || weekNumber > lastWeekNumber)
            throw new IllegalStateException("weeknumber must be an Int value in range 1 to " + lastWeekNumber);
    }

    /**
     * Returns the first Monday as a LocalDate from a weekNumber
     *
     * @param weekNumber is a value between 1 to 53 (the year 2020 has 53 weeks according to IsoWeek)
     * @return the Monday of this week number in the year
     */
    public static LocalDate getFirstDayOfWeekFromWeekNumber(final Integer year, final Integer weekNumber) {
        validateYear(year);
        validateWeek(weekNumber, year);
        return LocalDate.of(year, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Adjust a date to the first day of week, using ISO calendar.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    public static LocalDate adjustToFirstDayOfWeek(final LocalDate inputDate) {
        DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
        return inputDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
    }

    /**
     * Adjust a date to the last day of week, using ISO calendar.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    public static LocalDate adjustToLastDayOfWeek(final LocalDate inputDate) {
        DayOfWeek lastDayOfWeek = WeekFields.ISO.getFirstDayOfWeek().minus(1);
        return inputDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
    }

    /**
     * Returns the weekNumber between 1 to 53 from a LocalDate
     *
     * @param date is a valid LocalDate, no TimeZone
     * @return a weekNumber as Integer
     */
    public static Integer getWeekNumberFromDate(final LocalDate date) {
        TemporalField woy = WeekFields.ISO.weekOfWeekBasedYear();
        return date.get(woy);
    }

    /**
     * Returns the monthNumber between 1 to 12 from LocalDate
     *
     * @param date
     * @return a monthNumber as Integer
     */
    public static Integer getMonthNumberFromDate(final LocalDate date) {
        return date.getMonthValue();
    }

    public static Boolean isSameWeekAndYear(final LocalDate date1, final LocalDate date2) {
        boolean isSameWeek = getWeekNumberFromDate(date1).equals(getWeekNumberFromDate(date2));
        boolean isSameYear = date1.getYear() == date2.getYear();
        return isSameWeek && isSameYear;
    }

    /**
     * Format and return the date as a String
     *
     * @param date is a valid date
     * @return a String ISO LOCAL DATE
     */
    public static String formatToString(final LocalDate date) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }

    /**
     * Returns a LocalDate from an ISO_LOCAL_DATE String
     *
     * @param dateString
     * @return a localDate
     */
    public static LocalDate parseToLocalDate(final String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Format and return the time as a String
     *
     * @param time is a valid time
     * @return a String ISO LOCAL TIME
     */
    public static String formatToString(final LocalTime time) {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(time);
    }

    /**
     * Returns a LocalTime from ISO LOCAL TIME String
     *
     * @param timeString
     * @return a LocalTime
     */
    public static LocalTime parseToLocalTime(final String timeString) {
        return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    /**
     * Returns a formatted DateTime as ISO LOCAL DATE_TIME
     *
     * @param dateTime
     * @return a String
     */
    public static String formatToString(final LocalDateTime dateTime) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
    }

    /**
     * Returns a LocalDateTime from an ISO_LOCAL_DATE_TIME String
     *
     * @param dateString
     * @return a localDateTime
     */
    public static LocalDateTime parseToLocalDateTime(final String dateString) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Return a predicate that test if the input date is included in the six weeks of the month
     * The calendars view usually displays six weeks for the month view
     *
     * @param year
     * @param monthNumber
     * @return a predicate to test if the input date is included in the six weeks of the month
     */
    public static Predicate<LocalDate> isIncludedInSixWeeksFromMonth(final Integer year, final Integer monthNumber) {
        validateYear(year);
        validateMonth(monthNumber);
        LocalDate firstDayOfMonth = LocalDate.of(year, monthNumber, 1);
        Integer weekNumber = getWeekNumberFromDate(firstDayOfMonth);
        LocalDate firstDayOfFirstWeek = getFirstDayOfWeekFromWeekNumber(year, weekNumber);
        LocalDate lastDayOfLastWeek = adjustToLastDayOfWeek(firstDayOfFirstWeek.plusWeeks(5));
        return inputDate -> inputDate.isAfter(firstDayOfFirstWeek.minusDays(1)) && inputDate.isBefore(lastDayOfLastWeek.plusDays(1));
    }

    /**
     * Returns the number of minutes between two LocalDateTime
     *
     * @param from
     * @param to
     * @return a long that represents the number of minutes between the two dates
     */
    public static Long getDuration(LocalDateTime from, LocalDateTime to, ChronoUnit unit) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("EndDateTime must be after startDateTime");
        }
        return from.until(to, unit);
    }

    /**
     * Returns the time between two date in hours, minutes and seconds
     *
     * @param from
     * @param to
     * @return a String that represent the time between two date
     */
    public static String getDurationIntoHoursMinutesAndSecondAsString(LocalDateTime from, LocalDateTime to){
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("EndDateTime must be after startDateTime");
        }
        Duration duration = Duration.between(from, to);
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    /**
     * Get the business days between two LocalDate
     *
     * @param from is a LocalDate
     * @param to   is a LocalDate
     * @return the business days between two LocalDate as a List of LocalDate
     */
    private static List<LocalDate> getBusinessDays(LocalDate from, LocalDate to) {
        return IntStream.rangeClosed(from.getYear(), to.getYear())
                .boxed()
                .map(year -> CalendarFactory.instanceFor("FR", year))
                .flatMap(calendar -> calendar.getBusinessDays().stream())
                .filter(date -> date.isAfter(from) && date.isBefore(to) || date.equals(from) || date.equals(to))
                .collect(Collectors.toList());
    }

    /**
     * Compute the total number of business hours between two LocalDateTimes, assuming a start time
     * of 09:00 and a finish time of 17:00
     *
     * @param startDateTime is a localDateTime
     * @param endDateTime   is a localDateTime
     * @return the total of business hours between two LocalDateTimes as a Long
     */
    public static Long computeTotalNumberOfHours(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return computeTotalNumberOfHours(startDateTime, endDateTime, START_OF_DAY, END_OF_DAY);
    }

    /**
     * Compute the total number of business hours between two LocalDateTimes
     *
     * @param startDateTime is a localDateTime
     * @param endDateTime   is a localDateTime
     * @param startOfDay    is a localTime
     * @param endOfDay      is a localTime
     * @return the total number of business hours between two LocalDateTimes as a Long
     */
    public static Long computeTotalNumberOfHours(LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime,
                                                 LocalTime startOfDay,
                                                 LocalTime endOfDay) {
        Objects.requireNonNull(startDateTime, "StartDateTime should not be null");
        Objects.requireNonNull(endDateTime, "EndDateTime should not be null");
        if (endDateTime.isBefore(startDateTime)
                || startDateTime.getHour() < startOfDay.getHour() || startDateTime.getHour() > endOfDay.getHour()
                || endDateTime.getHour() < startOfDay.getHour() || endDateTime.getHour() > endOfDay.getHour()
        ) {
            throw new IllegalArgumentException("StartDateTime must be before EndDateTime and in the business hours range");
        }

        final int businessDays = getBusinessDays(startDateTime.toLocalDate(), endDateTime.toLocalDate()).size();
        final int hoursFromStartToEndOfDay = endOfDay.getHour() - startDateTime.toLocalTime().getHour();
        final int hoursFromEndToEndOfDay = endOfDay.getHour() - endDateTime.toLocalTime().getHour();

        return (long) hoursFromStartToEndOfDay
                + ((businessDays - 1) * (endOfDay.getHour() - startOfDay.getHour()))
                - hoursFromEndToEndOfDay;
    }
}