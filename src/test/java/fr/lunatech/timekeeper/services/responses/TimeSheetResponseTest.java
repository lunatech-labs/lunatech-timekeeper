package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSheetResponseTest {

    @Test
    void shouldFilterSixWeeksTo_EmptyListIfTimeSheetResponseHasNoEntries(){
        TimeSheetResponse tested = new TimeSheetResponse(
                1L,
                null,
                2L,
                TimeUnit.DAY,
                true,
                LocalDate.MAX,
                -1,
                "toto",
                Collections.emptyList(),
                3L
        );
        assertTrue(tested.filterToSixWeeksRange(2020,2).entries.isEmpty());
    }


    @Test
    void should_drop_entries_not_in_6_weeks_timerange(){
        TimeSheetResponse.TimeEntryResponse july14 = new TimeSheetResponse.TimeEntryResponse(1L,
                "comment",
                LocalDateTime.of(2020,7,14,9,00),
                LocalDateTime.of(2020,7,14,17,00)
        );
        TimeSheetResponse.TimeEntryResponse august15 = new TimeSheetResponse.TimeEntryResponse(2L,
                "comment",
                LocalDateTime.of(2020,8,15,9,00),
                LocalDateTime.of(2020,8,15,17,00)
        );
        TimeSheetResponse.TimeEntryResponse junePreviousYear = new TimeSheetResponse.TimeEntryResponse(3L,
                "comment",
                LocalDateTime.of(2019,6,14,9,00),
                LocalDateTime.of(2019,6,14,13,00)
        );

        List<TimeSheetResponse.TimeEntryResponse> initialDates = List.of(july14,junePreviousYear,august15);
        List<TimeSheetResponse.TimeEntryResponse> expectedList = List.of(july14);


        TimeSheetResponse tested = new TimeSheetResponse(
                1L,
                null,
                2L,
                TimeUnit.DAY,
                true,
                LocalDate.MAX,
                -1,
                "toto",
                initialDates,
                3L
        );
        assertEquals(expectedList, tested.filterToSixWeeksRange(2020,7).entries);
    }


    // Open the 2020 calendar on a Mac, go to the "Year" view and look at January 2020
    // We expected to see the last week of 2019 and the first week of february on the "6 weeks view" of january 2020
    @Test
    void should_keepdates_in_6_weeks_timerange(){
        TimeSheetResponse.TimeEntryResponse december29 = new TimeSheetResponse.TimeEntryResponse(1L,
                "comment",
                LocalDateTime.of(2019,12,29,9,00),
                LocalDateTime.of(2019,12,29,17,00)
        );
        TimeSheetResponse.TimeEntryResponse december30 = new TimeSheetResponse.TimeEntryResponse(2L,
                "comment",
                LocalDateTime.of(2019,12,30,9,00),
                LocalDateTime.of(2019,12,30,17,00)
        );
        TimeSheetResponse.TimeEntryResponse january20 = new TimeSheetResponse.TimeEntryResponse(3L,
                "comment",
                LocalDateTime.of(2020,1,20,9,00),
                LocalDateTime.of(2020,1,20,17,00)
        );
        TimeSheetResponse.TimeEntryResponse february9 = new TimeSheetResponse.TimeEntryResponse(4L,
                "comment",
                LocalDateTime.of(2020,2,9,9,00),
                LocalDateTime.of(2020,2,9,13,00)
        );
        TimeSheetResponse.TimeEntryResponse february10 = new TimeSheetResponse.TimeEntryResponse(5L,
                "comment",
                LocalDateTime.of(2020,2,10,9,00),
                LocalDateTime.of(2020,2,10,13,00)
        );

        List<TimeSheetResponse.TimeEntryResponse> initialDates = List.of(december29,december30,january20,february9,february10);
        List<TimeSheetResponse.TimeEntryResponse> expectedList = List.of(december30,january20,february9);

        TimeSheetResponse tested = new TimeSheetResponse(
                1L,
                null,
                2L,
                TimeUnit.DAY,
                true,
                LocalDate.MAX,
                -1,
                "toto",
                initialDates,
                3L
        );
        assertEquals(expectedList, tested.filterToSixWeeksRange(2020,1).entries);
    }

}
