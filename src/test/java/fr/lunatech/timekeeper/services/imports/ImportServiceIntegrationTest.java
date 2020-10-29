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

package fr.lunatech.timekeeper.services.imports;

import fr.lunatech.timekeeper.csv.ImportedTimeEntry;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedClientProject;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedUserProjectClient;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class ImportServiceIntegrationTest {

    @Inject
    Flyway flyway;

    @Inject
    ImportService importService;

    @AfterEach
    void cleanDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldPersistClientsFromAListOfName() {
        Long organizationId = 1L;
        List<String> clientsNames = Arrays.asList(
                "Client 1",
                "Client 2"
        );

        //Create Clients
        var clientsId = importService.createClients(clientsNames, organizationId);
        Long[] expected = {1L, 2L};

        //check
        assertThat(clientsId, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistClientThatAlreadyExistsInDB() {
        Long organizationId = 1L;
        List<String> clientsNames = Collections.singletonList(
                "Client 1"
        );

        //Persist Client in DB
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Try to persist an already existing client
        var clientsId = importService.createClients(clientsNames, organizationId);
        Long[] expected = {};

        //check
        assertThat(clientsId, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldThrowAnExceptionIfClientsNameToPersistAreTheSame() {
        Long organizationId = 1L;
        List<String> clientsNames = Arrays.asList(
                "Client 1",
                "Client 1"
        );
        assertThrows(PersistenceException.class, () -> importService.createClients(clientsNames, organizationId));
    }

    @Test
    void shouldPersistProjectFromAListOfClientProject() {
        Long organizationId = 1L;

        List<ImportedClientProject> importedClientProjectList = Arrays.asList(
                ImportedClientProject.of("Client 1", "Project 1"),
                ImportedClientProject.of("Client 2", "Project 2")
        );

        //persist client to match for the project creation
        importService.createClients(Arrays.asList("Client 1", "Client 2"), organizationId);
        //Create Project
        var projectsIds = importService.createProjects(importedClientProjectList, organizationId);
        Long[] expected = {1L, 2L};

        //check
        assertThat(projectsIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistProjectIfClientIsNotFound() {
        Long organizationId = 1L;

        List<ImportedClientProject> importedClientProjectList = Collections.singletonList(
                ImportedClientProject.of("Client 1", "Project 1")
        );

        //Create Project
        var projectsIds = importService.createProjects(importedClientProjectList, organizationId);
        Long[] expected = {};

        //check
        assertThat(projectsIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistProjectIfAlreadyExists() {
        Long organizationId = 1L;

        List<ImportedClientProject> importedClientProjectList = Collections.singletonList(
                ImportedClientProject.of("Client 1", "Project 1")
        );

        //persist client to match for the project creation
        importService.createClients(Arrays.asList("Client 1"), organizationId);
        //Create Project
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Try to persist a project that already exists
        var projectsIds = importService.createProjects(importedClientProjectList, organizationId);
        Long[] expected = {};

        //check
        assertThat(projectsIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldPersistProjectThatAlreadyExistForAnOtherClient() {
        Long organizationId = 1L;

        List<ImportedClientProject> importedClientProjectList = Collections.singletonList(
                ImportedClientProject.of("Client 2", "Project 1")
        );

        //persist client to match for the project creation
        importService.createClients(Arrays.asList("Client 1", "Client 2"), organizationId);
        //Create Project
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Try to persist a project that already exists for an other client
        var projectsIds = importService.createProjects(importedClientProjectList, organizationId);
        Long[] expected = {2L};

        //check
        assertThat(projectsIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldThrowAnExceptionWhenSameProjectArePersisted() {
        Long organizationId = 1L;

        List<ImportedClientProject> importedClientProjectList = Arrays.asList(
                ImportedClientProject.of("Client 1", "Project 1"),
                ImportedClientProject.of("Client 1", "Project 1")
        );

        //persist client to match for the project creation
        importService.createClients(Arrays.asList("Client 1", "Client 2"), organizationId);
        //Create Projects
        assertThrows(PersistenceException.class, () -> importService.createProjects(importedClientProjectList, organizationId));
    }

    @Test
    void shouldCreateAUserAndAddItInProjectAndPersistTheProject() {
        Long organizationId = 1L;

        List<ImportedUserProjectClient> importedUserProjectClientList = Arrays.asList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1"),
                ImportedUserProjectClient.of("jane.doe@lunatech.fr", "Jane Doe", "Project 2", "Client 1")
        );

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Arrays.asList(ImportedClientProject.of("Client 1", "Project 1"), ImportedClientProject.of("Client 1", "Project 2")), organizationId);
        // Create Users and Add it in projects
        var projectsIds = importService.createUserAndAddInProject(importedUserProjectClientList, organizationId);
        Long[] expected = {1L, 2L};

        //check
        assertThat(projectsIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldPersistsTimeEntries() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("Project 1");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@lunatech.fr");
        importedTimeEntry2.setClient("Client 1");
        importedTimeEntry2.setProject("Project 2");
        importedTimeEntry2.setDescription("Comment of the entry of the project2");
        importedTimeEntry2.setStartDate("2020-10-26");
        importedTimeEntry2.setStartTime("09:00:00");
        importedTimeEntry2.setEndDate("2020-10-26");
        importedTimeEntry2.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Arrays.asList(importedTimeEntry, importedTimeEntry2);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Arrays.asList(
                ImportedClientProject.of("Client 1", "Project 1"),
                ImportedClientProject.of("Client 1", "Project 2")), organizationId);
        // Create Users and Add it in projects
        importService.createUserAndAddInProject(Arrays.asList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1"),
                ImportedUserProjectClient.of("jane.doe@lunatech.fr", "Jane Doe", "Project 2", "Client 1")), organizationId);
        // Create TimeEntries
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {1L, 2L};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistsTimeEntriesIfProjectNameIsBlank() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Collections.singletonList(importedTimeEntry);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Create Users and Add it in projects
        importService.createUserAndAddInProject(Collections.singletonList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1")), organizationId);
        // Create TimeEntries
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistsTimeEntriesIfProjectIsNotInDB() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("Project");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Collections.singletonList(importedTimeEntry);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Create Users and Add it in projects
        importService.createUserAndAddInProject(Collections.singletonList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1")), organizationId);
        // Create TimeEntries
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistsTimeEntriesIfClientIsNotInDB() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("Project 1");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Collections.singletonList(importedTimeEntry);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 2"), organizationId);
        //Create Projects
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 2", "Project 1")), organizationId);
        // Create Users and Add it in projects
        importService.createUserAndAddInProject(Collections.singletonList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1")), organizationId);
        // Create TimeEntries
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistsTimeEntriesIfUserIsNotInDB() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("Project 1");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Collections.singletonList(importedTimeEntry);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Create TimeEntries
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldNotPersistsTimeEntriesIfTimeEntryAlreadyExists() {
        Long organizationId = 1L;

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setClient("Client 1");
        importedTimeEntry.setProject("Project 1");
        importedTimeEntry.setDescription("Comment of the entry of the project");
        importedTimeEntry.setStartDate("2020-10-26");
        importedTimeEntry.setStartTime("09:00:00");
        importedTimeEntry.setEndDate("2020-10-26");
        importedTimeEntry.setEndTime("17:00:00");

        List<ImportedTimeEntry> importedTimeEntries = Collections.singletonList(importedTimeEntry);

        //persist client to match for the project creation
        importService.createClients(Collections.singletonList("Client 1"), organizationId);
        //Create Projects
        importService.createProjects(Collections.singletonList(ImportedClientProject.of("Client 1", "Project 1")), organizationId);
        // Create Users and Add it in projects
        importService.createUserAndAddInProject(Collections.singletonList(
                ImportedUserProjectClient.of("john.doe@lunatech.fr", "John Doe", "Project 1", "Client 1")), organizationId);
        // Create TimeEntries
        importService.createTimeEntries(Collections.singletonList(importedTimeEntry), organizationId);
        var timeEntriesIds = importService.createTimeEntries(importedTimeEntries, organizationId);

        Long[] expected = {};

        //check
        assertThat(timeEntriesIds, Matchers.<Collection<Long>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }
}