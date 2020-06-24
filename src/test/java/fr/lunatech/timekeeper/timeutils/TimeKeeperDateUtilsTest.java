/*
 * Copyright 2020 Lunatech Labs
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeKeeperDateUtilsTest {

    @Test
    void shouldReturnMondayFromWeekNumberForFirstWeek() {
        final var expected= LocalDate.of(2019,12,30);
        // The first monday of 2020 with Iso Weekview is the Monday, 30th of December 2019
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 1));
    }

    @Test
    void shouldReturnMondayFromWeekNumberForWeek52() {
        final var expected= LocalDate.of(2020,12,21);
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 52));
    }

    @Test
    void shouldReturnMondayFromWeekNumberForWeek53() {
        final var expected= LocalDate.of(2020,12,28);
        // In 2020 there are 53 weeks according to the Iso Week Calendar
        assertEquals(expected, TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(2020, 53));
    }

    @Test
    void shouldReturnMondayFromWeekNumberInFebruary() {
        final var expected= LocalDate.of(2020,2,24);
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
    void shouldAdjustToTheMonday(){
        LocalDate inputDate = LocalDate.of(2020,6,11);
        LocalDate expectedDate = LocalDate.of(2020, 6, 8);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldAdjustToMondayOfPreviousYear(){
        LocalDate inputDate = LocalDate.of(2020,1,2);
        LocalDate expectedDate = LocalDate.of(2019, 12, 30);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldAdjustToMondayForLastDayOfYear(){
        LocalDate inputDate = LocalDate.of(2020,12,31);
        LocalDate expectedDate = LocalDate.of(2020, 12, 28);
        assertEquals(expectedDate, TimeKeeperDateUtils.adjustToFirstDayOfWeek(inputDate));
    }

    @Test
    void shouldReturnFirstWeekOf2020(){
        LocalDate firstDayOfYear = LocalDate.of(2020,1,1);
        assertEquals( 1, TimeKeeperDateUtils.getWeekNumberFromDate(firstDayOfYear));
    }

    @Test
    void shouldReturnLastWeekOf2020(){
        // Friday 1st of January 2021 belongs to week 53 from the IsoWeek calendar perspective
        LocalDate lastDayOfAWeekIn2020 = LocalDate.of(2021,1,1);
        assertEquals( 53, TimeKeeperDateUtils.getWeekNumberFromDate(lastDayOfAWeekIn2020));
    }

    @Test
    void shouldReturnWeekNumberFromFebruaryDate(){
        LocalDate lastDayOfFebruary = LocalDate.of(2020,2,29);
        assertEquals( 9, TimeKeeperDateUtils.getWeekNumberFromDate(lastDayOfFebruary));
    }

    @Test
    void shouldFormatALocalDate(){
        LocalDate inputDate = LocalDate.of(2020,6,11);
        assertEquals("2020-06-11", TimeKeeperDateUtils.formatToString(inputDate));
    }
}
