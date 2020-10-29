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

package fr.lunatech.timekeeper.importandexportcsv;

import fr.lunatech.timekeeper.importandexportcsv.TimeEntryCSVParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeEntryCSVParserTest {

    @Test
    void shouldReturnAListOfTwoLocalDate() {
        TimeEntryCSVParser timeEntryCSVParser = new TimeEntryCSVParser();
        var actual = timeEntryCSVParser.checkStringParametersAndParseThemIntoLocalDate("2020-10-28", "2020-10-30");
        LocalDate[] expected = {
                LocalDate.of(2020, 10, 28),
                LocalDate.of(2020, 10, 30),
        };

        assertThat(actual, Matchers.<Collection<LocalDate>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldThrowAnErrorIfDateAreNotValid() {
        TimeEntryCSVParser timeEntryCSVParser = new TimeEntryCSVParser();
        assertThrows(IllegalArgumentException.class, () -> timeEntryCSVParser.checkStringParametersAndParseThemIntoLocalDate("2020-10-28", "2020-10-32"));
    }
}