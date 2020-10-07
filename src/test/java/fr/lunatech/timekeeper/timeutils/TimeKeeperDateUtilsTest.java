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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TimeKeeperDateUtilsTest {

    @Test
    void shouldReturnMondayFromWeekNumberForFirstWeek() {
        final var expected = LocalDate.of(2019, 12, 30);
        // The first monday of 2020 with Iso Weekview is the Monday, 30th of December 2019
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 1));
    }

    @Test
    void shouldReturnMondayFromWeekNumberForWeek52() {
        final var expected = LocalDate.of(2020, 12, 21);
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 52));
    }

    @Test
    void shouldReturnMondayFromWeekNumberForWeek53() {
        final var expected = LocalDate.of(2020, 12, 28);
        // In 2020 there are 53 weeks according to the Iso Week Calendar
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 53));
    }

    @Test
    void shouldReturnMondayFromWeekNumberInFebruary() {
        final var expected = LocalDate.of(2020, 2, 24);
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 9));
    }

    @Test
    void shouldNotAcceptInvalidDate() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(19, 9);
        });
    }

    @Test
    void shouldNotAcceptInvalidWeekNumber() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2019, 65);
        });
    }

    @Test
    void shouldNotAcceptWeekNumberSetToZero() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2019, 0);
        });
    }

    @Test
    void shouldNotAcceptNegativeWeekNumber() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2019, -1);
        });
    }

    @Test
    void shouldNotAcceptYearBefore1970() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(1950, 9);
        });
    }

    @Test
    void shouldAdjustToTheMonday() {
        LocalDate inputDate = LocalDate.of(2020, 6, 11);
        LocalDate expectedDate = LocalDate.of(2020, 6, 8);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldAdjustToMondayOfPreviousYear() {
        LocalDate inputDate = LocalDate.of(2020, 1, 2);
        LocalDate expectedDate = LocalDate.of(2019, 12, 30);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldAdjustToMondayForLastDayOfYear() {
        LocalDate inputDate = LocalDate.of(2020, 12, 31);
        LocalDate expectedDate = LocalDate.of(2020, 12, 28);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldReturnFirstWeekOf2020() {
        LocalDate firstDayOfYear = LocalDate.of(2020, 1, 1);
        assertEquals(1, TimeKeeperDateUtils.getWeekNumberFromDate(firstDayOfYear));
    }

    @Test
    void shouldReturnLastWeekOf2020() {
        // Friday 1st of January 2021 belongs to week 53 from the IsoWeek calendar perspective
        LocalDate lastDayOfAWeekIn2020 = LocalDate.of(2021, 1, 1);
        assertEquals(53, TimeKeeperDateUtils.getWeekNumberFromDate(lastDayOfAWeekIn2020));
    }

    @Test
    void shouldReturnWeekNumberFromFebruaryDate() {
        LocalDate lastDayOfFebruary = LocalDate.of(2020, 2, 29);
        assertEquals(9, TimeKeeperDateUtils.getWeekNumberFromDate(lastDayOfFebruary));
    }

    @Test
    void shouldFormatALocalDate() {
        LocalDate inputDate = LocalDate.of(2020, 6, 11);
        assertEquals("2020-06-11", TimeKeeperDateUtils.formatToString(inputDate));
    }

    @Test
    void shouldBeTrueForFirstDayOfMonth() {
        LocalDate inputDate = LocalDate.of(2020, 2, 1);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldBeTrueForFirstDayOfFirstWeekOfMonth() {
        LocalDate inputDate = LocalDate.of(2020, 1, 27);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldBeTrueForLastDayOfLastWeekOfMonthInFebruary() {
        LocalDate inputDate1 = LocalDate.of(2020, 3, 7);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 2).test(inputDate1));

        LocalDate inputDate3 = LocalDate.of(2020, 3, 9);
        assertFalse(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 2).test(inputDate3));


        LocalDate inputDate = LocalDate.of(2020, 3, 8);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 2).test(inputDate));

    }

    @Test
    void shouldBeTrueForLastDayOfLastWeekOfMonthInDecember() {
        // 6th january 2021 belongs to the 6th week of december, 2020
        LocalDate inputDate = LocalDate.of(2021, 1, 6);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 12).test(inputDate));
    }

    @Test
    void shouldBeTrueForAnyDayOfAnyWeekOfMonth() {
        LocalDate inputDate = LocalDate.of(2020, 2, 14);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldBeTrueForBisextilesYears() {
        LocalDate inputDate = LocalDate.of(2020, 2, 29);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldBeTrueForLastDayOfYear() {
        LocalDate inputDate = LocalDate.of(2020, 12, 31);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 12).test(inputDate));
    }

    @Test
    void shouldBeTrueForFirstDayOfYear() {
        LocalDate inputDate = LocalDate.of(2020, 1, 1);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 1).test(inputDate));
    }

    @Test
    void shouldBeTrueForFirstDayOfFirstWeekOfYear() {
        LocalDate inputDate = LocalDate.of(2019, 12, 30);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 1).test(inputDate));
    }

    @Test
    void shouldBeTrueForLastDayOfLastWeekOfYear() {
        LocalDate inputDate = LocalDate.of(2021, 1, 9);
        assertTrue(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 12).test(inputDate));
    }

    @Test
    void shouldNotBeTrueForTheDayBeforeTheFirstDayOfFirstWeekOfMonth() {
        LocalDate inputDate = LocalDate.of(2020, 1, 26);
        assertFalse(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldNotBeTrueForTheDayAfterTheLastDayOfLastWeekOfMonth() {
        LocalDate inputDate = LocalDate.of(2020, 3, 9);
        assertFalse(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 02).test(inputDate));
    }

    @Test
    void shouldNotBeTrueForWrongYear() {
        LocalDate inputDate = LocalDate.of(2020, 2, 1);
        assertFalse(TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2019, 02).test(inputDate));
    }

    @Test
    void shouldThrowExceptionForNonBisextilesYears() {
        Assertions.assertThrows(DateTimeException.class, () -> {
            TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2021, 02).test(LocalDate.of(2021, 2, 29));
        });
    }

    @Test
    void shouldThrowExceptionForYearBefore1970() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(1969, 02).test(LocalDate.of(1969, 2, 1));
        });
    }

    @Test
    void shouldThrowExceptionFor13thMonth() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 13).test(LocalDate.of(2020, 13, 1));
        });
    }

    @Test
    void shouldThrowExceptionFor0Month() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(2020, 0).test(LocalDate.of(2020, 0, 1));
        });
    }

    @Test
    void shouldThrowExceptionForWeek0Validator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateWeek(0, 2020);
        });
    }

    @Test
    void shouldBeTrueFor28DecemberAlwaysInLastWeekOfYear() {
        for (int year = 2000; year < 2030; year++) {
            assertNotEquals(1, TimeKeeperDateUtils.getWeekNumberFromDate(LocalDate.of(year, 12, 28)));
        }
    }

    @Test
    void shouldThrowExceptionForWeek53OfYearsWith52WeeksValidator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateWeek(53, 2019);
        });
    }

    @Test
    void shouldThrowExceptionForWeek54Validator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateWeek(54, 2020);
        });
    }

    @Test
    void shouldThrowExceptionForNegativeWeekNumberValidator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateWeek(-1, 2020);
        });
    }

    @Test
    void shouldThrowExceptionFor0MonthValidator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateMonth(0);
        });
    }

    @Test
    void shouldThrowExceptionFor13thMonthValidator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateMonth(13);
        });
    }

    @Test
    void shouldThrowExceptionForNegativehMonthValidator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateMonth(-1);
        });
    }

    @Test
    void shouldThrowExceptionForYearBefore1970Validator() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            TimeKeeperDateUtils.validateYear(1969);
        });
    }

    @Test
    void shouldReturnSundayAsLastDayOfWeek() {
        LocalDate inputDate = LocalDate.of(2020, 2, 26);
        LocalDate expected = LocalDate.of(2020, 3, 1);
        assertEquals(expected, TimeKeeperDateUtils.adjustToLastDayOfWeek(inputDate));

        LocalDate inputDate2 = LocalDate.of(2020, 4, 30);
        LocalDate expected2 = LocalDate.of(2020, 5, 3);
        assertEquals(expected2, TimeKeeperDateUtils.adjustToLastDayOfWeek(inputDate2));
    }

    @Test
    void shouldReturnMondayAsFirstDayOfWeek() {
        LocalDate inputDate = LocalDate.of(2020, 7, 2);
        LocalDate expected = LocalDate.of(2020, 6, 29);
        assertEquals(expected, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldReturnTheRightStringForLLocalDateTime(){
        LocalDateTime dateTime = LocalDateTime.of(2020,10,06,9,0,0);
        assertEquals("2020-10-06T09:00:00", TimeKeeperDateUtils.formatToString(dateTime));
    }

    @Test
    void shouldReturn60Minutes(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,9,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,10,0);
        assertEquals(60L, TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.MINUTES));
    }

    @Test
    void shouldReturn480Minutes(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,9,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,17,0);
        assertEquals(480L, TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.MINUTES));
    }

    @Test
    void shouldReturn1Hour(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,9,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,10,0);
        assertEquals(1L, TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.HOURS));
    }

    @Test
    void shouldReturn8Hours(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,9,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,17,0);
        assertEquals(8L, TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.HOURS));
    }

    @Test
    void shouldThrowAnErrorIfStartIsAfterEnd(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,10,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,9,0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.MINUTES);
        });
    }

    @Test
    void shouldReturn0IfStartEqualsEnd(){
        LocalDateTime start = LocalDateTime.of(2020,10,06,9,0);
        LocalDateTime end = LocalDateTime.of(2020,10,06,9,0);
        assertEquals(0L, TimeKeeperDateUtils.getDuration(start, end, ChronoUnit.MINUTES));
    }

    @Test
    void shouldReturnTrueIfItsSameWeekAndSameYear(){
        LocalDate date1 = LocalDate.of(2020,10,6);
        LocalDate date2 = LocalDate.of(2020,10,8);
        assertTrue(TimeKeeperDateUtils.isSameWeekAndYear(date1,date2));
    }

    @Test
    void shouldReturnFalseIfItsDifferentYearAndWeek(){
        LocalDate date1 = LocalDate.of(2020,10,6);
        LocalDate date2 = LocalDate.of(2021,10,20);
        assertFalse(TimeKeeperDateUtils.isSameWeekAndYear(date1,date2));
    }

    @Test
    void shouldReturnFalseIfItsSameWeekButDifferentYear(){
        LocalDate date1 = LocalDate.of(2020,10,6);
        LocalDate date2 = LocalDate.of(2021,10,8);
        assertFalse(TimeKeeperDateUtils.isSameWeekAndYear(date1,date2));
    }

    @Test
    void shouldReturnFalseIfItsSameYearButDifferentWeek(){
        LocalDate date1 = LocalDate.of(2020,10,6);
        LocalDate date2 = LocalDate.of(2020,10,20);
        assertFalse(TimeKeeperDateUtils.isSameWeekAndYear(date1,date2));
    }

    @Test
    void shouldReturn1ForJanuary(){
        LocalDate date = LocalDate.of(2020,01,20);
        assertEquals(1, TimeKeeperDateUtils.getMonthNumberFromDate(date));
    }

    @Test
    void shouldReturnALocalDateTimeFromAString(){
        String date = "2020-10-06T09:00:00";
        assertEquals(
                LocalDateTime.of(2020,10,6,9,0,0),
                TimeKeeperDateUtils.formatToLocalDateTime(date));
    }

    @Test
    void shouldReturnALocalDateFromAString(){
        String date = "2020-06-11";
        assertEquals(
                LocalDate.of(2020,6,11),
                TimeKeeperDateUtils.formatToLocalDate(date));
    }

    @Test
    void shouldCompute0Hours() {
        assertEquals(0L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute3BusinessHours() {
        assertEquals(3L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute8BusinessHours() {
        assertEquals(8L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 17).atTime(17, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute12BusinessHours() {
        assertEquals(12L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 18).atTime(13, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute16BusinessHours() {
        assertEquals(16L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 18).atTime(17, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute40BusinessHours() {
        assertEquals(40L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 15).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 19).atTime(17, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute5BusinessDaysIgnoringWeekends() {
        assertEquals(40L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 23).atTime(17, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute5LongBusinessDaysIgnoringWeekends() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("18:00:00",
                DateTimeFormatter.ISO_TIME);

        assertEquals(45L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                        LocalDate.of(2020, 6, 23).atTime(18, 0).truncatedTo(ChronoUnit.HOURS),
                        START_OF_DAY,
                        END_OF_DAY
                )
        );
    }

    @Test
    void shouldIgnoreBastilleDay() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("17:00:00",
                DateTimeFormatter.ISO_TIME);

        assertEquals(0L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 7, 14).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 7, 14).atTime(17, 0).truncatedTo(ChronoUnit.HOURS))
        );
    }

    @Test
    void shouldCompute3BusinessDaysIgnoringBastilleDay() {
        assertEquals(16L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 7, 13).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 7, 15).atTime(17, 0).truncatedTo(ChronoUnit.HOURS)
                )
        );
    }

    @Test
    void shouldCompute5BusinessDaysIgnoringWeekendsAndBastilleDay() {
        assertEquals(40L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 7, 10).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 7, 17).atTime(17, 0).truncatedTo(ChronoUnit.HOURS))
        );
    }

    @Test
    void shouldComputeTwo3HoursDays() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("12:00:00",
                DateTimeFormatter.ISO_TIME);

        assertEquals(6L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 6, 18).atTime(12, 0).truncatedTo(ChronoUnit.HOURS),
                START_OF_DAY,
                END_OF_DAY)
        );
    }

    @Test
    void shouldNotComputeTwo3HoursDaysEndOfDay() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("12:00:00",
                DateTimeFormatter.ISO_TIME);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                    LocalDate.of(2020, 6, 18).atTime(18, 0).truncatedTo(ChronoUnit.HOURS),
                    START_OF_DAY,
                    END_OF_DAY);
        });
    }

    @Test
    void shouldNotComputeTwo3HoursDaysBeginningOfDay() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("12:00:00",
                DateTimeFormatter.ISO_TIME);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(8, 0).truncatedTo(ChronoUnit.HOURS),
                    LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS),
                    START_OF_DAY,
                    END_OF_DAY);
        });
    }

    @Test
    void shouldComputeTwo5HoursDays() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("14:00:00",
                DateTimeFormatter.ISO_TIME);

        assertEquals(10L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 6, 18).atTime(14, 0).truncatedTo(ChronoUnit.HOURS),
                START_OF_DAY,
                END_OF_DAY)
        );
    }

    @Test
    void shouldNotComputeTwo5HoursDaysEndOfDay() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("14:00:00",
                DateTimeFormatter.ISO_TIME);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS),
                    LocalDate.of(2020, 6, 18).atTime(18, 0).truncatedTo(ChronoUnit.HOURS),
                    START_OF_DAY,
                    END_OF_DAY);
        });

    }

    @Test
    void shouldNotComputeTwo5HoursDaysBeginningOfDay() {
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("14:00:00",
                DateTimeFormatter.ISO_TIME);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 6, 17).atTime(8, 0).truncatedTo(ChronoUnit.HOURS),
                    LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS),
                    START_OF_DAY,
                    END_OF_DAY);
        });
    }

    @Test
    void shouldNotComputeHoursBridgingAWeekends() {
        assertEquals(2L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 7, 17).atTime(16, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2020, 7, 20).atTime(10, 0).truncatedTo(ChronoUnit.HOURS))
        );
    }

    @Test
    void shouldReturnAnEmptyListIfTheTwoDateAreNotInTheSameYear(){
        final LocalTime START_OF_DAY = LocalTime.parse("09:00:00",
                DateTimeFormatter.ISO_TIME);

        final LocalTime END_OF_DAY = LocalTime.parse("17:00:00",
                DateTimeFormatter.ISO_TIME);

        assertEquals(0L, TimeKeeperDateUtils.computeTotalNumberOfHours(LocalDate.of(2020, 12, 29).atTime(16, 0).truncatedTo(ChronoUnit.HOURS),
                LocalDate.of(2021, 1, 3).atTime(10, 0).truncatedTo(ChronoUnit.HOURS))
        );
    }
}
