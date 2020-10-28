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

package fr.lunatech.timekeeper.services.exports;

import fr.lunatech.timekeeper.importcsv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.imports.UserImportExtension;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class ExportServiceIntegrationTest {
    @Inject
    Flyway flyway;

    @Inject
    ExportService exportService;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @Transactional
    void shouldReturnTimeEntries() {
        Organization organization = Organization.findById(1L);
        var start = LocalDate.of(2020, 10, 26);
        var end = LocalDate.of(2020, 10, 28);

        User user = new User();
        user.email = "john.doe@lunatech.fr";
        user.lastName = "Doe";
        user.firstName = "John";
        user.organization = organization;
        user.profiles = Collections.singletonList(Profile.USER);
        user.persistAndFlush();

        Client client = new Client();
        client.name = "Client";
        client.organization = organization;
        client.description = "Description";
        client.persistAndFlush();

        Project project = new Project();
        project.name = "Project";
        project.organization = organization;
        project.version = 1L;
        project.description = "Description";
        project.client = client;
        project.persistAndFlush();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.owner = user;
        timeSheet.project = project;
        timeSheet.persistAndFlush();

        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.comment = "timeEntry comment";
        timeEntry1.startDateTime = LocalDateTime.of(2020, 10, 26, 9, 0, 0);
        timeEntry1.endDateTime = LocalDateTime.of(2020, 10, 26, 17, 0, 0);
        timeEntry1.timeSheet = timeSheet;
        timeEntry1.persistAndFlush();

        TimeEntry timeEntry2 = new TimeEntry();
        timeEntry2.comment = "timeEntry2 comment";
        timeEntry2.startDateTime = LocalDateTime.of(2020, 10, 27, 9, 0, 0);
        timeEntry2.endDateTime = LocalDateTime.of(2020, 10, 27, 17, 0, 0);
        timeEntry2.timeSheet = timeSheet;
        timeEntry2.persistAndFlush();

        TimeEntry timeEntry3 = new TimeEntry();
        timeEntry3.comment = "timeEntry3 comment";
        timeEntry3.startDateTime = LocalDateTime.of(2020, 10, 28, 9, 0, 0);
        timeEntry3.endDateTime = LocalDateTime.of(2020, 10, 28, 17, 0, 0);
        timeEntry3.timeSheet = timeSheet;
        timeEntry3.persistAndFlush();

        var actual = exportService.getTimeEntriesBetweenTwoDate(start, end);
        TimeEntry[] expected = {timeEntry1, timeEntry2, timeEntry3};

        assertThat(actual, Matchers.<Collection<TimeEntry>>allOf(
                hasSize(expected.length)
        ));
    }

    @Test
    @Transactional
    void shouldNotReturnTimeEntriesThatAreNotBetweenTheDate() {
        Organization organization = Organization.findById(1L);
        var start = LocalDate.of(2020, 10, 26);
        var end = LocalDate.of(2020, 10, 28);

        User user = new User();
        user.email = "john.doe@lunatech.fr";
        user.lastName = "Doe";
        user.firstName = "John";
        user.organization = organization;
        user.profiles = Collections.singletonList(Profile.USER);
        user.persistAndFlush();

        Client client = new Client();
        client.name = "Client";
        client.organization = organization;
        client.description = "Description";
        client.persistAndFlush();

        Project project = new Project();
        project.name = "Project";
        project.organization = organization;
        project.version = 1L;
        project.description = "Description";
        project.client = client;
        project.persistAndFlush();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.owner = user;
        timeSheet.project = project;
        timeSheet.persistAndFlush();

        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.comment = "timeEntry comment";
        timeEntry1.startDateTime = LocalDateTime.of(2020, 10, 25, 9, 0, 0);
        timeEntry1.endDateTime = LocalDateTime.of(2020, 10, 25, 17, 0, 0);
        timeEntry1.timeSheet = timeSheet;
        timeEntry1.persistAndFlush();

        TimeEntry timeEntry2 = new TimeEntry();
        timeEntry2.comment = "timeEntry2 comment";
        timeEntry2.startDateTime = LocalDateTime.of(2020, 10, 27, 9, 0, 0);
        timeEntry2.endDateTime = LocalDateTime.of(2020, 10, 27, 17, 0, 0);
        timeEntry2.timeSheet = timeSheet;
        timeEntry2.persistAndFlush();

        TimeEntry timeEntry3 = new TimeEntry();
        timeEntry3.comment = "timeEntry3 comment";
        timeEntry3.startDateTime = LocalDateTime.of(2020, 10, 29, 9, 0, 0);
        timeEntry3.endDateTime = LocalDateTime.of(2020, 10, 29, 17, 0, 0);
        timeEntry3.timeSheet = timeSheet;
        timeEntry3.persistAndFlush();

        var actual = exportService.getTimeEntriesBetweenTwoDate(start, end);
        TimeEntry[] expected = {timeEntry2};

        assertThat(actual, Matchers.<Collection<TimeEntry>>allOf(
                hasSize(expected.length)
        ));
    }

    @Test
    @Transactional
    void shouldReturnAnEmptyListIfNoneMatch() {
        Organization organization = Organization.findById(1L);
        var start = LocalDate.of(2020, 9, 26);
        var end = LocalDate.of(2020, 9, 28);

        User user = new User();
        user.email = "john.doe@lunatech.fr";
        user.lastName = "Doe";
        user.firstName = "John";
        user.organization = organization;
        user.profiles = Collections.singletonList(Profile.USER);
        user.persistAndFlush();

        Client client = new Client();
        client.name = "Client";
        client.organization = organization;
        client.description = "Description";
        client.persistAndFlush();

        Project project = new Project();
        project.name = "Project";
        project.organization = organization;
        project.version = 1L;
        project.description = "Description";
        project.client = client;
        project.persistAndFlush();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.owner = user;
        timeSheet.project = project;
        timeSheet.persistAndFlush();

        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.comment = "timeEntry comment";
        timeEntry1.startDateTime = LocalDateTime.of(2020, 10, 26, 9, 0, 0);
        timeEntry1.endDateTime = LocalDateTime.of(2020, 10, 26, 17, 0, 0);
        timeEntry1.timeSheet = timeSheet;
        timeEntry1.persistAndFlush();

        TimeEntry timeEntry2 = new TimeEntry();
        timeEntry2.comment = "timeEntry2 comment";
        timeEntry2.startDateTime = LocalDateTime.of(2020, 10, 27, 9, 0, 0);
        timeEntry2.endDateTime = LocalDateTime.of(2020, 10, 27, 17, 0, 0);
        timeEntry2.timeSheet = timeSheet;
        timeEntry2.persistAndFlush();

        TimeEntry timeEntry3 = new TimeEntry();
        timeEntry3.comment = "timeEntry3 comment";
        timeEntry3.startDateTime = LocalDateTime.of(2020, 10, 28, 9, 0, 0);
        timeEntry3.endDateTime = LocalDateTime.of(2020, 10, 28, 17, 0, 0);
        timeEntry3.timeSheet = timeSheet;
        timeEntry3.persistAndFlush();

        var actual = exportService.getTimeEntriesBetweenTwoDate(start, end);
        TimeEntry[] expected = {};

        assertThat(actual, Matchers.<Collection<TimeEntry>>allOf(
                hasSize(expected.length)
        ));
    }

    @Test
    @Transactional
    void shouldThrowAnExceptionIfStartIsAfterEnd() {
        Organization organization = Organization.findById(1L);
        var start = LocalDate.of(2020, 10, 28);
        var end = LocalDate.of(2020, 10, 26);

        assertThrows(IllegalArgumentException.class, () -> exportService.getTimeEntriesBetweenTwoDate(start, end));
    }

    @Test
    @Transactional
    void shouldReturnImportedTimeEntryWithEmailFromUserImportExtension() {
        Organization organization = Organization.findById(1L);

        User user = new User();
        user.email = "john.doe@lunatech.fr";
        user.lastName = "Doe";
        user.firstName = "John";
        user.organization = organization;
        user.profiles = Collections.singletonList(Profile.USER);
        user.persistAndFlush();

        UserImportExtension userImportExtension = new UserImportExtension();
        userImportExtension.user = user;
        userImportExtension.userEmailFromImport = "john.doe@lunatech.com";
        userImportExtension.persistAndFlush();

        Client client = new Client();
        client.name = "Client";
        client.organization = organization;
        client.description = "Description";
        client.persistAndFlush();

        Project project = new Project();
        project.name = "Project";
        project.organization = organization;
        project.version = 1L;
        project.description = "Description";
        project.client = client;
        project.persistAndFlush();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.owner = user;
        timeSheet.project = project;
        timeSheet.persistAndFlush();

        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.comment = "timeEntry comment";
        timeEntry1.startDateTime = LocalDateTime.of(2020, 10, 26, 9, 0, 0);
        timeEntry1.endDateTime = LocalDateTime.of(2020, 10, 26, 17, 0, 0);
        timeEntry1.timeSheet = timeSheet;
        timeEntry1.persistAndFlush();

        var actual = exportService.getExportedTimeEntry(Collections.singletonList(timeEntry1));
        ImportedTimeEntry[] expected = {
                new ImportedTimeEntry("John Doe",
                        "john.doe@lunatech.com",
                        "Client",
                        "Project",
                        "",
                        "timeEntry comment",
                        "Yes",
                        "2020-10-26",
                        "09:00:00",
                        "2020-10-26",
                        "17:00:00",
                        "08:00:00",
                        "",
                        ""),
        };

        assertThat(actual, Matchers.<Collection<ImportedTimeEntry>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    @Transactional
    void shouldReturnImportedTimeEntryWithEmailFromUser() {
        Organization organization = Organization.findById(1L);

        User user = new User();
        user.email = "john.doe@lunatech.fr";
        user.lastName = "Doe";
        user.firstName = "John";
        user.organization = organization;
        user.profiles = Collections.singletonList(Profile.USER);
        user.persistAndFlush();

        Client client = new Client();
        client.name = "Client";
        client.organization = organization;
        client.description = "Description";
        client.persistAndFlush();

        Project project = new Project();
        project.name = "Project";
        project.organization = organization;
        project.version = 1L;
        project.description = "Description";
        project.client = client;
        project.persistAndFlush();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.owner = user;
        timeSheet.project = project;
        timeSheet.persistAndFlush();

        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.comment = "timeEntry comment";
        timeEntry1.startDateTime = LocalDateTime.of(2020, 10, 26, 9, 0, 0);
        timeEntry1.endDateTime = LocalDateTime.of(2020, 10, 26, 17, 0, 0);
        timeEntry1.timeSheet = timeSheet;
        timeEntry1.persistAndFlush();

        var actual = exportService.getExportedTimeEntry(Collections.singletonList(timeEntry1));
        ImportedTimeEntry[] expected = {
                new ImportedTimeEntry("John Doe",
                        "john.doe@lunatech.fr",
                        "Client",
                        "Project",
                        "",
                        "timeEntry comment",
                        "Yes",
                        "2020-10-26",
                        "09:00:00",
                        "2020-10-26",
                        "17:00:00",
                        "08:00:00",
                        "",
                        ""),
        };

        assertThat(actual, Matchers.<Collection<ImportedTimeEntry>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }
}