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

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.ProjectUser;
import fr.lunatech.timekeeper.models.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeSheetBuilderTest {

    @Test
    void shouldCreateATimeSheet() {
        Organization defaultOrganization = new Organization();
        defaultOrganization.name = "Test";
        defaultOrganization.tokenName = "lunatechTest";

        User user = new User();
        user.email = "bob@lunatech.fr";
        user.firstName = "bob";
        user.lastName = "martin";
        user.organization = defaultOrganization;

        ProjectUser projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = user;
        projectUser.manager = false;

        Project project = new Project();
        project.billable = true;
        project.name = "project 1";
        project.organization = defaultOrganization;
        project.users = List.of(projectUser);

        var tested = TimeSheetBuilder.build(user, project);
        assertEquals(user, tested.owner);
        assertEquals(project, tested.project);

    }


}
