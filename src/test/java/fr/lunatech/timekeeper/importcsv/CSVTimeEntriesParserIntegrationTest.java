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

package fr.lunatech.timekeeper.importcsv;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class CSVTimeEntriesParserIntegrationTest {

    @Inject
    Flyway flyway;

    @Inject
    CSVTimeEntriesParser csvTimeEntriesParser;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldReturnAnEmptyListOfClientsWithAnEmptyListOfImportedTimeEntry() {
        assertEquals(Collections.EMPTY_LIST,
                csvTimeEntriesParser.computeClients(Collections.emptyList(), 1L));
    }

    @Test
    void shouldNotReturnNullClient() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();

        Client[] expected = {};

        var actual = csvTimeEntriesParser.computeClients(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<Client>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4ClientsFromListOfImportedTimeEntry() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client 1");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");

        Client[] expected = {
                new Client("Client 1", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 2", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 3", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 4", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
        };

        var actual = csvTimeEntriesParser.computeClients(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4), 1L);

        assertThat(actual, Matchers.<Collection<Client>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnClientsFromListOfImportedTimeEntryWithBlankClients() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("");

        Client[] expected = {};
        var actual = csvTimeEntriesParser.computeClients(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4), 1L);

        assertThat(actual, Matchers.<Collection<Client>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn3ClientsFromListOfImportedTimeEntryWithOneBlankClient() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client 1");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("");

        Client[] expected = {
                new Client("Client 1", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 2", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 3", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
        };

        var actual = csvTimeEntriesParser.computeClients(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4), 1L);

        assertThat(actual, Matchers.<Collection<Client>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnTheSameClientTwice() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client 1");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 1");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");

        Client[] expected = {
                new Client("Client 1", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 3", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
                new Client("Client 4", "Imported from CSV file", Organization.findById(1L), Collections.emptyList()),
        };

        var actual = csvTimeEntriesParser.computeClients(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4), 1L);

        assertThat(actual, Matchers.<Collection<Client>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForNullUserAndEmail() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForNullUser() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setEmail("john.doe@organization.fr");

        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForNullEmail() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");

        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForBlankUserAndEmail() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("");
        importedTimeEntry.setEmail("");
        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForBlankUser() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("");
        importedTimeEntry.setEmail("john.doe@organization.fr");
        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotReturnUserForBlankEmail() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("");
        User[] expected = {};

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturnOneUser(){
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@organization.fr");

        User[] expected = {
                User.createUserForImport("john.doe@lunatech.fr", "John Doe", Organization.findById(1L)),
        };
        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4UsersFor4ImportedTimeEntries() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setUser("Alice Doe");
        importedTimeEntry3.setEmail("alice.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setUser("Tim Doe");
        importedTimeEntry4.setEmail("tim.doe@organization.fr");

        User[] expected = {
                User.createUserForImport("john.doe@lunatech.fr", "John Doe", Organization.findById(1L)),
                User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", Organization.findById(1L)),
                User.createUserForImport("alice.doe@lunatech.fr", "Alice Doe", Organization.findById(1L)),
                User.createUserForImport("tim.doe@lunatech.fr", "Tim Doe", Organization.findById(1L)),
        };

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4UsersFor5ImportedTimeEntriesAndTwoDuplicate() {
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setUser("Alice Doe");
        importedTimeEntry3.setEmail("alice.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setUser("Tim Doe");
        importedTimeEntry4.setEmail("tim.doe@organization.fr");
        ImportedTimeEntry importedTimeEntry5 = new ImportedTimeEntry();
        importedTimeEntry4.setUser("Tim Doe");
        importedTimeEntry4.setEmail("tim.doe@organization.fr");

        User[] expected = {
                User.createUserForImport("john.doe@lunatech.fr", "John Doe", Organization.findById(1L)),
                User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", Organization.findById(1L)),
                User.createUserForImport("alice.doe@lunatech.fr", "Alice Doe", Organization.findById(1L)),
                User.createUserForImport("tim.doe@lunatech.fr", "Tim Doe", Organization.findById(1L)),
        };

        var actual = csvTimeEntriesParser.computeUsers(List.of(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4, importedTimeEntry5), 1L);

        assertThat(actual, Matchers.<Collection<User>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

//    @Test
//    void shouldReturnAListOf1ProjectWith2Users(){
//        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
//        importedTimeEntry.setUser("John Doe");
//        importedTimeEntry.setEmail("john.doe@lunatech.fr");
//        importedTimeEntry.setProject("Project 1");
//        importedTimeEntry.setClient("Client 1");
//
//        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
//        importedTimeEntry2.setUser("Jane Doe");
//        importedTimeEntry2.setEmail("jane.doe@lunatech.fr");
//        importedTimeEntry2.setProject("Project 1");
//        importedTimeEntry2.setClient("Client 1");
//
//        Client client = new Client("Client 1", "Imported from CSV file", Organization.findById(1L), Collections.emptyList());
//
//        User john = User.createUserForImport("john.doe@lunatech.fr", "John Doe", Organization.findById(1L));
//        User jane = User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", Organization.findById(1L));
//
//        Project project = new Project();
//        project.name = "Project 1";
//        project.organization = Organization.findById(1L);
//        project.client = client;
//        project.billable = true;
//        project.description = "Imported from CSV file";
//        project.version = 1L;
//
//        ProjectUser projectUser = new ProjectUser();
//        projectUser.user = john;
//        projectUser.project = project;
//        projectUser.manager = false;
//
//        ProjectUser projectUser2 = new ProjectUser();
//        projectUser2.user = jane;
//        projectUser2.project = project;
//        projectUser2.manager = false;
//
//        project.users = Arrays.asList(projectUser, projectUser2);
//
//        Project[] expected = {
//                project,
//        };
//
//        var actual = csvTimeEntriesParser.computeProjects(List.of(importedTimeEntry, importedTimeEntry2), 1L);
//
//        assertThat(actual, Matchers.<Collection<Project>>allOf(
//                hasItems(expected),
//                hasSize(expected.length)
//        ));
//    }

}