package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
class TimeSheetUtilsTest {

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
                generateTestEntries(8,8L)
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
                null
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
                generateTestEntries(1,8L)
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
                generateTestEntries(3,4L)
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
                generateTestEntries(1,8L)
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
                generateTestEntries(1,1L)
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
                generateTestEntries(4,8L)
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
                Collections.singletonList(timeEntry)
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
                generateTestEntries(1,1L)
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
                generateTestEntries(1,1L)
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
                generateTestEntries(1,8L)
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