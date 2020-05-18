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
