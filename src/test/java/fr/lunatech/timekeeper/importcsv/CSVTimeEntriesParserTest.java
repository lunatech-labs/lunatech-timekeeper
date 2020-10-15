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

package fr.lunatech.timekeeper.importcsv;

import fr.lunatech.timekeeper.models.Organization;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CSVTimeEntriesParserTest {

    @Test
    void should_throw_exception_for_null_date() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        assertThrows(IllegalArgumentException.class, () -> tested.parseToLocalDateTime(null, "any"));
    }

    @Test
    void should_throw_exception_for_null_time() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        assertThrows(IllegalArgumentException.class, () -> tested.parseToLocalDateTime("any", null));
    }

    @Test
    void should_parse_valid_date_and_time() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        LocalDateTime expected = LocalDateTime.of(2020, 9, 8, 9, 0, 31);
        assertEquals(expected, tested.parseToLocalDateTime("2020-09-08", "09:00:31"));
    }

    @Test
    void should_parse_valid_date_and_time_for_midnight() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        LocalDateTime expected = LocalDateTime.of(2020, 9, 24, 0, 0, 0);
        assertEquals(expected, tested.parseToLocalDateTime("2020-09-24", "00:00:00"));
    }

    @Test
    void should_update_email_to_organization_token_name() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        assertEquals("toto@lunatech.fr", tested.updateEmailToOrganization("toto@donothing.com", organization));
    }

    @Test
    void should_trim_lowercase_email() {
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        assertEquals("toto@lunatech.fr", tested.updateEmailToOrganization(" Toto@donothing.com ", organization));
    }

}