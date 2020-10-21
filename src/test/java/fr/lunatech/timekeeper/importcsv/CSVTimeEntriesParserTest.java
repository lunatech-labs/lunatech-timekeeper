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

import fr.lunatech.timekeeper.models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldReturnTrueForAnEmptyListOfProjects(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        List<Project> projects = Collections.emptyList();
        assertTrue(tested.isNotExistingProject(projects, "Project", "Client"));
    }

    @Test
    void shouldReturnFalseIfProjectIsInProjectList(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Project project = new Project();
        project.name = "Project";
        project.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project2 = new Project();
        project2.name = "Project 2";
        project2.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project3 = new Project();
        project3.name = "Project 3";
        project3.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project4 = new Project();
        project4.name = "Project 4";
        project4.client = new Client("Client","",new Organization(),Collections.emptyList());

        List<Project> projects = Arrays.asList(project,project2,project3, project3);

        assertFalse(tested.isNotExistingProject(projects, "Project", "Client"));
    }

    @Test
    void shouldReturnTrueIfProjectIsNotInProjectListSameNameDifferentClient(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Project project = new Project();
        project.name = "Project";
        project.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project2 = new Project();
        project2.name = "Project 2";
        project2.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project3 = new Project();
        project3.name = "Project 3";
        project3.client = new Client("Client","",new Organization(),Collections.emptyList());

        List<Project> projects = Arrays.asList(project,project2,project3, project3);

        assertTrue(tested.isNotExistingProject(projects, "Project", "Client 2"));
    }

    @Test
    void shouldReturnTrueIfProjectIsNotInProjectListSameClientDifferentName(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Project project = new Project();
        project.name = "Project";
        project.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project2 = new Project();
        project2.name = "Project 2";
        project2.client = new Client("Client","",new Organization(),Collections.emptyList());
        Project project3 = new Project();
        project3.name = "Project 3";
        project3.client = new Client("Client","",new Organization(),Collections.emptyList());

        List<Project> projects = Arrays.asList(project,project2,project3, project3);

        assertTrue(tested.isNotExistingProject(projects, "Project 1", "Client"));
    }

    @Test
    void shouldReturnTrueIfUserIsNotInProject(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();

        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        User userForProject = User.createUserForImport("john.doe@lunatech.fr", "John Doe", organization);

        Project project = new Project();
        project.name = "Project";

        ProjectUser projectUser = new ProjectUser(project, userForProject, false);
        project.users = Arrays.asList(projectUser);

        User user = User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", organization);

        assertTrue(tested.isNotAUserProject(project, user));
    }

    @Test
    void shouldReturnFalseIfUserIsInProject(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();

        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "Lunatech France";
        organization.tokenName = "lunatech.fr";
        organization.projects = new ArrayList<>();
        organization.users = new ArrayList<>();
        organization.clients = new ArrayList<>();

        User userForProject = User.createUserForImport("john.doe@lunatech.fr", "John Doe", organization);

        Project project = new Project();
        project.name = "Project";

        ProjectUser projectUser = new ProjectUser(project, userForProject, false);
        project.users = Arrays.asList(projectUser);

        User user = User.createUserForImport("john.doe@lunatech.fr", "John Doe", organization);

        assertFalse(tested.isNotAUserProject(project, user));
    }

    @Test
    void shouldReturnAListOf1ProjectWith2Users(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        importedTimeEntry.setProject("Project 1");
        importedTimeEntry.setClient("Client 1");

        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@lunatech.fr");
        importedTimeEntry2.setProject("Project 1");
        importedTimeEntry2.setClient("Client 1");

//        Client client = new Client("Client 1", "Imported from CSV file", Organization.findById(1L), Collections.emptyList());
        Client client = new Client("Client 1", "Imported from CSV file", new Organization(), Collections.emptyList());

//        User john = User.createUserForImport("john.doe@lunatech.fr", "John Doe", Organization.findById(1L));
//        User jane = User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", Organization.findById(1L));
        User john = User.createUserForImport("john.doe@lunatech.fr", "John Doe", new Organization());
        User jane = User.createUserForImport("jane.doe@lunatech.fr", "Jane Doe", new Organization());

        Project project = new Project();
        project.name = "Project 1";
//        project.organization = Organization.findById(1L);
        project.organization = new Organization();
        project.client = client;
        project.billable = true;
        project.description = "Imported from CSV file";
        project.version = 1L;

        ProjectUser projectUser = new ProjectUser();
        projectUser.user = john;
        projectUser.project = project;
        projectUser.manager = false;

        ProjectUser projectUser2 = new ProjectUser();
        projectUser2.user = jane;
        projectUser2.project = project;
        projectUser2.manager = false;

        project.users = Collections.emptyList();
//        project.users = Arrays.asList(projectUser, projectUser2);

        Project[] expected = {
                project,
        };

        var actual = csvTimeEntriesParser.computeProjects(List.of(importedTimeEntry, importedTimeEntry2), 1L);

        assertThat(actual, Matchers.<Collection<Project>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }
}