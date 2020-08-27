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

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class TimeSheetUtilsTest {

    private final LocalDate START_DATE = LocalDate.now();

    @Test
    void shouldComputeLeftOver_nominal() {
        //GIVEN a timeSheet with a limit of 10 DAYS

        //WHEN user add 8 entries of 8 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.HOURLY,
                true,
                null,
                10,
                TimeUnit.DAY,
                generateTestEntries(8,8L),
                START_DATE
        );

        //THEN: only 2 hours left
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(2L));
    }

    @Test
    void shouldComputeLeftOver_nullTimeSheet() {
        //GIVEN a timeSheet with a limit of 10 DAYS

        //WHEN there's no time entry for this time sheet
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.HOURLY,
                true,
                null,
                10,
                TimeUnit.DAY,
                null,
                START_DATE
        );

        //THEN: 10 days left
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(10L));
    }

    @Test
    void shouldComputeLeftOver_HalfDay() {
        //GIVEN a timeSheet with a limit of 4 half day

        //WHEN user add 1 entries of 8 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                4,
                TimeUnit.HALFDAY,
                generateTestEntries(1,8L),
                START_DATE
        );

        //THEN: only 2 half day (1 day)
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(1L));
    }

    @Test
    void shouldComputeLeftOver_HalfDayRounded() {
        //GIVEN a timeSheet with a limit of 4 half day

        //WHEN user add 3 entries of 4 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                4,
                TimeUnit.HALFDAY,
                generateTestEntries(3,4L),
                START_DATE
        );

        //THEN: only 1 half day left (rounded to 0 day)
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(0L));
    }

    @Test
    void shouldComputeLeftOver_Hours() {
        //GIVEN a timeSheet with a limit of 20 hours

        //WHEN user add 1 entries of 8 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                20,
                TimeUnit.HOURLY,
                generateTestEntries(1,8L),
                START_DATE
        );

        //THEN: only 12 hours left (1,5 day -> rounded 1 day)
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(1L));
    }

    @Test
    void shouldComputeLeftOver_HoursRounded() {
        //GIVEN a timeSheet with a limit of 8 hours

        //WHEN user add 1 entries of 1 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                8,
                TimeUnit.HOURLY,
                generateTestEntries(1,1L),
                START_DATE
        );

        //THEN: only 7 hours left (rounded 0 day)
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(0L));
    }

    @Test
    void shouldComputeLeftOver_negativeResult() {
        //GIVEN a timeSheet with a limit of 2 DAYS

        //WHEN user add 4 entries of 8 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.HOURLY,
                true,
                null,
                2,
                TimeUnit.DAY,
                generateTestEntries(4,8L),
                START_DATE
        );

        //THEN: the result is negative
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(-2L));
    }

    @Test
    void shouldComputeLeftOver_WrongInput() {
        //GIVEN a timeSheet with a limit of 8 hours

        //WHEN the user entry is incorrect
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.startDateTime =LocalDateTime.now().minusHours(8L);
        timeEntry.endDateTime = LocalDateTime.now();
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                8,
                TimeUnit.HOURLY,
                Collections.singletonList(timeEntry),
                START_DATE
        );

        //THEN: only 0 hours left (rounded 0 day)
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(0L));
    }

    @Test
    void shouldReturnNullWhenNoMaxDurationSet() {
        //GIVEN a timeSheet with a limit of 8 hours

        //WHEN user add 1 entries of 1 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                null,
                TimeUnit.HOURLY,
                generateTestEntries(1,1L),
                START_DATE
        );

        //THEN: no calculation are made
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(nullValue()));
    }

    @Test
    void shouldReturnNullWhenWrongMaxDurationSet() {
        //GIVEN a timeSheet with a wrong limit (-2)

        //WHEN user add 1 entries of 1 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                -2,
                TimeUnit.HOURLY,
                generateTestEntries(1,1L),
                START_DATE
        );

        //THEN: no calculation are made
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(nullValue()));
    }

    @Test
    void shouldComputeLeftOver_NoDurationType() {
        //GIVEN a timeSheet with a limit of 4 without type

        //WHEN user add 1 entries of 8 hours
        TimeSheet timesheet = new TimeSheet(
                null,
                null,
                TimeUnit.DAY,
                true,
                null,
                4,
                null,
                generateTestEntries(1,8L),
                START_DATE
        );

        //THEN: limit is consider by day, therefor 3 is returned
        assertThat(TimeSheetUtils.computeLeftOver(timesheet),is(3L));
    }

    private List<TimeEntry> generateTestEntries (int numberOfEntry, long hourPerEntry){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusSomeHours = now.plusHours(hourPerEntry);
        List<TimeEntry> entries = new ArrayList();
        for (int i = 0 ; i<numberOfEntry;i++) {
            TimeEntry entry = new TimeEntry();
            entry.startDateTime = now;
            entry.endDateTime = nowPlusSomeHours;
            entries.add(entry);
        }
        return entries;
    }


}