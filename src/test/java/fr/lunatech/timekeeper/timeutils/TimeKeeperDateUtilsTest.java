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

import fr.lunatech.timekeeper.services.exceptions.CalendarNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static fr.lunatech.timekeeper.resources.utils.DateUtilsTestResourceProvider.*;
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
    void shouldReturnTheRightStringForLLocalDateTime() {
        assertEquals("2020-10-06T09:00:00",
                TimeKeeperDateUtils.formatToString(THE_6_TH_OCTOBER_2020_AT_9_AM));
    }

    @ParameterizedTest
    @CsvSource({
            "9, 10, 60, MINUTES",
            "9, 17, 480, MINUTES",
            "9, 9, 0, MINUTES",
            "9, 10, 1, HOURS",
            "9, 17, 8, HOURS",
            "9, 9, 0, HOURS"
    })
    void shouldComputeDuration(int to, int from, Long expected, ChronoUnit unit) {
        LocalDateTime start = LocalDateTime.of(2020, 10, 6, to, 0);
        LocalDateTime end = LocalDateTime.of(2020, 10, 6, from, 0);
        assertEquals(expected, TimeKeeperDateUtils.getDuration(start, end, unit));
    }

    @Test
    void shouldThrowAnErrorIfStartIsAfterEnd() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.getDuration(
                        THE_6_TH_OCTOBER_2020_AT_10_AM, THE_6_TH_OCTOBER_2020_AT_9_AM,
                        ChronoUnit.MINUTES)
        );
    }

    @Test
    void shouldReturnTrueIfItsSameWeekAndSameYear() {
        assertTrue(TimeKeeperDateUtils.isSameWeekAndYear(THE_6_TH_OCTOBER_2020, THE_8_TH_OCTOBER_2020));
    }

    @ParameterizedTest
    @CsvSource({
            //end year, end day
            "2021, 20", //shouldReturnFalseIfItsDifferentYearAndWeek
            "2021, 8", //shouldReturnFalseIfItsSameWeekButDifferentYear
            "2020, 20" //shouldReturnFalseIfItsSameYearButDifferentWeek
    })
    void shouldReturnFalseIfNotSameWeekOrYear(int endYear, int endDay) {
        assertFalse(TimeKeeperDateUtils.isSameWeekAndYear(
                THE_6_TH_OCTOBER_2020, LocalDate.of(endYear, 10, endDay)));
    }

    @Test
    void shouldReturn1ForJanuary() {
        LocalDate date = LocalDate.of(2020, 1, 20);
        assertEquals(1, TimeKeeperDateUtils.getMonthNumberFromDate(date));
    }

    @Test
    void shouldReturnALocalDateTimeFromAString() {
        String date = "2020-10-06T09:00:00";
        assertEquals(
                LocalDateTime.of(2020, 10, 6, 9, 0, 0),
                TimeKeeperDateUtils.formatToLocalDateTime(date));
    }

    @Test
    void shouldReturnALocalDateFromAString() {
        String date = "2020-06-11";
        assertEquals(
                THE_11_TH_JUNE_2020,
                TimeKeeperDateUtils.formatToLocalDate(date));
    }

    @Test
    void shouldReturnAStringFromLocalTime() {
        var time = LocalTime.of(9, 0);
        assertEquals("09:00:00", TimeKeeperDateUtils.formatToString(time));

    }

    @Test
    void shouldReturnALocalTimeFromString() {
        var timeString = "09:00:00";
        var expected = LocalTime.of(9, 0);
        assertEquals(expected, TimeKeeperDateUtils.formatToLocalTime(timeString));
    }

    @Test
    void shouldCompute0Hours() {
        assertEquals(0L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(THE_17_TH_JUNE_2020_AT_9_AM,
                        THE_17_TH_JUNE_2020_AT_9_AM
                )
        );
    }

    @Test
    void shouldCompute3BusinessHours() {
        assertEquals(3L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_17_TH_JUNE_2020_AT_NOON));
    }

    @Test
    void shouldCompute8BusinessHours() {
        assertEquals(8L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_17_TH_JUNE_2020_AT_17_PM));
    }

    @Test
    void shouldCompute12BusinessHours() {
        assertEquals(12L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_13_PM));
    }

    @Test
    void shouldCompute16BusinessHours() {
        assertEquals(16L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_17_PM));
    }

    @Test
    void shouldCompute40BusinessHours() {
        assertEquals(40L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_15_TH_JUNE_2020_AT_9_AM, THE_19_TH_JUNE_2020_AT_17_PM));
    }

    @Test
    void shouldCompute5BusinessDaysIgnoringWeekends() {
        assertEquals(40L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_23_RD_JUNE_2020_AT_17_PM));
    }

    @Test
    void shouldCompute5LongBusinessDaysIgnoringWeekends() {
        assertEquals(45L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_23_RD_JUNE_2020_AT_18_PM,
                        MORNING_AT_9_AM, AFTERNOON_AT_18_PM));
    }

    @Test
    void shouldIgnoreBastilleDay() {
        assertEquals(0L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_14_TH_JULY_2020_AT_9_AM, THE_14_TH_JULY_2020_AT_17_PM));
    }

    @Test
    void shouldCompute3BusinessDaysIgnoringBastilleDay() {
        assertEquals(16L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_13_TH_JULY_2020_AT_9_AM, THE_15_TH_JULY_2020_AT_17_PM));
    }

    @Test
    void shouldCompute5BusinessDaysIgnoringWeekendsAndBastilleDay() {
        assertEquals(40L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_10_TH_JULY_2020_AT_9_AM, THE_17_TH_JULY_2020_AT_17_PM));
    }

    @Test
    void shouldComputeTwo3HoursDays() {
        assertEquals(6L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_NOON,
                        MORNING_AT_9_AM, NOON));
    }

    @Test
    void shouldNotComputeTwo3HoursDaysEndOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_18_PM,
                        MORNING_AT_9_AM, NOON));
    }

    @Test
    void shouldNotComputeTwo3HoursDaysBeginningOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_8_AM, THE_17_TH_JUNE_2020_AT_NOON,
                        MORNING_AT_9_AM, NOON));
    }

    @Test
    void shouldComputeTwo5HoursDays() {
        assertEquals(10L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_14_PM,
                        MORNING_AT_9_AM, AFTERNOON_AT_14_PM));
    }

    @Test
    void shouldNotComputeTwo5HoursDaysEndOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_9_AM, THE_18_TH_JUNE_2020_AT_18_PM,
                        MORNING_AT_9_AM, AFTERNOON_AT_14_PM));

    }

    @Test
    void shouldThrowExceptionIfStartsBeforeBeginningOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_8_AM, THE_17_TH_JUNE_2020_AT_NOON));
    }

    @Test
    void shouldThrowExceptionIfStartsAfterEndOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_18_PM, THE_17_TH_JUNE_2020_AT_19_PM));
    }

    @Test
    void shouldThrowExceptionIfEndsBeforeStartOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_NOON, THE_18_TH_JUNE_2020_AT_8_AM));
    }

    @Test
    void shouldThrowExceptionIfEndsAfterEndOfDay() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JUNE_2020_AT_NOON, THE_18_TH_JUNE_2020_AT_18_PM));
    }

    @Test
    void shouldNotComputeHoursBridgingAWeekends() {
        assertEquals(2L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_17_TH_JULY_2020_AT_16_PM, THE_20_TH_JULY_2020_AT_10_AM));
    }

    @Test
    void shouldThrowAnExceptionIfOneIsNotDefined() {
        Assertions.assertThrows(CalendarNotFoundException.class, () ->
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        THE_29_TH_DECEMBER_2019_AT_16_PM, THE_3_RD_JANUARY_2020_AT_10_AM));
    }

    @Test
    void shouldReturn2HoursForAnEventBridgingNewYear() {
        assertEquals(2L,
                TimeKeeperDateUtils.computeTotalNumberOfHours(
                        NEW_YEARS_EVE_2020_AT_16_PM, THE_4_TH_JANUARY_2021_AT_10_AM));
    }
}
