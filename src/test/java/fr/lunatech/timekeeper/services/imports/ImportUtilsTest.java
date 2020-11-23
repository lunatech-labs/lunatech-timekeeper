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

package fr.lunatech.timekeeper.services.imports;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.resources.utils.NullableConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ImportUtilsTest {

    @Test
    void should_throw_exception_for_null_date() {
        assertThrows(IllegalArgumentException.class, () -> ImportUtils.parseToLocalDateTime(null, "any"));
    }

    @Test
    void should_throw_exception_for_null_time() {
        assertThrows(IllegalArgumentException.class, () -> ImportUtils.parseToLocalDateTime("any", null));
    }

    @Test
    void should_parse_valid_date_and_time() {
        LocalDateTime expected = LocalDateTime.of(2020, 9, 8, 9, 0, 31);
        var actual = ImportUtils.parseToLocalDateTime("2020-09-08", "09:00:31");
        assertThat(actual, is(expected));
    }

    @Test
    void should_parse_valid_date_and_time_for_midnight() {
        LocalDateTime expected = LocalDateTime.of(2020, 9, 24, 0, 0, 0);
        var actual = ImportUtils.parseToLocalDateTime("2020-09-24", "00:00:00");
        assertThat(actual, is(expected));
    }

    @Test
    void should_update_email_to_organization_token_name() {
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        var expected = "toto@lunatech.fr";
        var actual = ImportUtils.updateEmailToOrganization("toto@donothing.com", organization.tokenName);
        assertThat(actual, is(expected));
    }

    @Test
    void should_trim_lowercase_email() {
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        var expected = "toto@lunatech.fr";
        var actual = ImportUtils.updateEmailToOrganization("Toto@lunatech.fr", organization.tokenName);
        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "null",
            "''"
    })
    void shouldComputeCommentForNullOrBlankDescription(@ConvertWith(NullableConverter.class) String description){
        String projectName = "Project";

        var expected = "Worked on project Project";
        var actual = ImportUtils.computeComment(description, projectName);

        assertThat(actual, is(expected));
    }

    @Test
    void shouldComputeCommentForDescription(){
        var description = "Description of the timeentry";
        var projectName = "Project";

        var expected = "Description of the timeentry";
        var actual = ImportUtils.computeComment(description, projectName);

        assertThat(actual, is(expected));
    }
}