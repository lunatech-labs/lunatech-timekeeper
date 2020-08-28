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

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventDurationTest {

    @Test
    void shouldReturnZeroIfStartDateIsAfterEndDate(){
        LocalDateTime after = LocalDateTime.of(2020, 8,27,18,0,0);
        LocalDateTime before = LocalDateTime.of(2020, 8,21,18,0,0);
        assertEquals(0L, EventDuration.durationInHours(after,before));
    }

    @Test
    void shouldReturnTheCorrectDurationForValidStartAndEndDate() {
        LocalDateTime after = LocalDateTime.of(2020, 8,27,9,0,0);
        LocalDateTime before = LocalDateTime.of(2020, 8,27,17,0,0);
        assertEquals(8, EventDuration.durationInHours(after,before));

    }

    @Test
    void shouldComputeDurationIfEndDateIsTwoDaysAfterStartDate() {
        LocalDateTime after = LocalDateTime.of(2020, 8,27,9,0,0);
        LocalDateTime before = LocalDateTime.of(2020, 8,29,9,0,0);
        assertEquals(15, EventDuration.durationInHours(after,before));
    }


}