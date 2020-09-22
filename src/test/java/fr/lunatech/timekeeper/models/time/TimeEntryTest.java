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

package fr.lunatech.timekeeper.models.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeEntryTest {

    @Test
    void shouldNotThrowAnyNpe() {
        var tested = new TimeEntry();
        tested.comment = null;
        tested.timeSheet = null;
        tested.startDateTime = null;
        tested.endDateTime = null;
        tested.id = 2L;
        assertDoesNotThrow( tested::toString, "Should not throw any Exception" );
    }

    @Test
    void shouldReturnZeroIfStartDateTimeIsNull() {
        TimeEntry tested= new TimeEntry();
        tested.timeSheet=new TimeSheet();
        tested.comment="ccc";
        tested.startDateTime=null;
        tested.endDateTime= LocalDateTime.of(2020,1,2,10,15);
        tested.id=222L;
        assertEquals(0L , tested.getRoundedNumberOfHours());
    }

    @Test
    void shouldReturnZeroIfEndDateTimeIsNull() {
        TimeEntry tested= new TimeEntry();
        tested.timeSheet=new TimeSheet();
        tested.comment="ccc";
        tested.startDateTime=LocalDateTime.of(2020,1,2,10,15);
        tested.endDateTime=null;
        tested.id=222L;
        assertEquals(0L , tested.getRoundedNumberOfHours());
    }

    @Test
    void shouldReturnZeroIfEndDateTimeIsBeforeStartDateTime() {
        TimeEntry tested= new TimeEntry();
        tested.timeSheet=new TimeSheet();
        tested.comment="ccc";
        tested.startDateTime=LocalDateTime.of(2020,1,2,10,15);
        tested.endDateTime=LocalDateTime.of(2020,1,2,10,15).minusDays(1);
        tested.id=222L;
        assertEquals(0L , tested.getRoundedNumberOfHours());
    }


    @Test
    void shouldReturnZeroIfEndDateTimeIsEqualsToStartDateTime() {
        TimeEntry tested= new TimeEntry();
        tested.timeSheet=new TimeSheet();
        tested.comment="ccc";
        tested.startDateTime=LocalDateTime.of(2020,1,2,10,15);
        tested.endDateTime=LocalDateTime.of(2020,1,2,10,15);
        tested.id=222L;
        assertEquals(0L , tested.getRoundedNumberOfHours());
    }

    @Test
    void shouldReturnADurationInHours() {
        TimeEntry tested= new TimeEntry();
        tested.timeSheet=new TimeSheet();
        tested.comment="ccc";
        tested.startDateTime=LocalDateTime.of(2020,1,2,10,0);
        tested.endDateTime=LocalDateTime.of(2020,1,2,17,10);
        tested.id=221L;
        assertEquals(7L , tested.getRoundedNumberOfHours());
    }
}