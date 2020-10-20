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
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.ProjectUser;
import fr.lunatech.timekeeper.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    void shouldReturnFalseForAnEmptyListOfProjects(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        List<Project> projects = Collections.emptyList();
        Project project = new Project();
        project.name = "Project";
        assertTrue(tested.isNotProjectInList(projects, project));
    }

    @Test
    void shouldReturnTrueIfProjectIsInProjectList(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Project project = new Project();
        project.name = "Project";
        Project project2 = new Project();
        project2.name = "Project 2";
        Project project3 = new Project();
        project3.name = "Project 3";
        Project project4 = new Project();
        project4.name = "Project 4";

        List<Project> projects = Arrays.asList(project,project2,project3, project3);

        Project projectToCheck = new Project();
        projectToCheck.name = "Project";

        assertFalse(tested.isNotProjectInList(projects, projectToCheck));
    }

    @Test
    void shouldReturnFalseIfProjectIsNotInProjectList(){
        CSVTimeEntriesParser tested = new CSVTimeEntriesParser();
        Project project = new Project();
        project.name = "Project 1";
        Project project2 = new Project();
        project2.name = "Project 2";
        Project project3 = new Project();
        project3.name = "Project 3";
        Project project4 = new Project();
        project4.name = "Project 4";

        List<Project> projects = Arrays.asList(project,project2,project3, project3);

        Project projectToCheck = new Project();
        projectToCheck.name = "Project";

        assertTrue(tested.isNotProjectInList(projects, projectToCheck));
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

}