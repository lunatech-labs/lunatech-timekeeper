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

package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTemplateTest {

    @Test
    void shouldValidateTwoDates() {
        var tested = new EventTemplate();
        tested.startDateTime = LocalDateTime.of(2020, 2, 29, 12, 23, 29);
        tested.endDateTime = LocalDateTime.of(2020, 4, 2, 13, 29);
        assertTrue(tested.isValid());
    }

    @Test
    void shouldNotValidateEventTemplate() {
        var tested = new EventTemplate();
        tested.startDateTime = LocalDateTime.of(2020, 2, 29, 12, 23, 29);
        tested.endDateTime = LocalDateTime.of(2018, 4, 2, 11, 29);
        assertThrows(CreateResourceException.class, tested::isValid);
    }

    @Test
    void shouldNotThrowAnyNpe() {
        var tested = new EventTemplate();
        tested.startDateTime = null;
        tested.endDateTime = null;
        tested.name=null;
        tested.description=null;
        tested.organization=null;
        tested.id = 2L;
        assertEquals("EventTemplate{" +
                "id=" + 2 +
                ", name='null'" +
                ", description='null'"  +
                ", organization=null" +
                ", startDateTime=null" +
                ", endDateTime=null" +
                '}', tested.toString());
    }
}