package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationContextTest {

    @Test
    void project_canAccess_Should_Return_False_If_NoAccess_Through_Organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organization2 = new Organization();
        organization2.id = 2100L;
        organization2.name = "yolo.com";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization2, emptyList());
        assertFalse(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_True_IfAdmin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.ADMIN));
        assertTrue(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_True_If_SuperAdmin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.SUPER_ADMIN));
        assertTrue(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_True_for_public_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = true;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_false_if_not_member_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;
        project.users = emptyList();

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_false_if_members_is_null() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;
        project.users = null;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_true_if_user_is_member_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = zeUser;
        projectUser.manager = false;
        projectUser.project = project;

        project.users = List.of(projectUser);

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(project));
    }

    @Test
    void project_canAccess_Should_Return_true_if_user_is_team_leader_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = zeUser;
        projectUser.manager = true; // Team leader
        projectUser.project = project;

        project.users = List.of(projectUser);

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(project));
    }

    @Test
    void timeEntry_canAccess_should_be_true_for_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id=999L;
        timeEntry.startDateTime= LocalDateTime.now();
        timeEntry.endDateTime= LocalDateTime.now();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = "??";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(timeEntry));
    }

    @Test
    void organization_canAccess_should_be_true_for_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organizationOther = new Organization();
        organizationOther.id = 100L;
        organizationOther.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(organizationOther));
    }

    @Test
    void organization_canAccess_should_be_true_for_superAdmin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organizationOther = new Organization();
        organizationOther.id = 100L;
        organizationOther.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.SUPER_ADMIN);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(organizationOther));
    }

    @Test
    void timeSheet_canAccess_should_be_false_if_different_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organization2 = new Organization();
        organization2.id = 102L;
        organization2.name = "lunatech.com";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization2;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization2, List.of(Profile.USER));
        assertFalse(tested.canAccess(timeSheet));
    }

    @Test
    void timeSheet_canAccess_should_be_true_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";


        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertTrue(tested.canAccess(timeSheet));
    }

    @Test
    void client_canAccess_should_be_true_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization sameOrganization = new Organization();
        sameOrganization.id = 100L;
        sameOrganization.name = "lunatech.fr";

        Client oneClient = new Client();
        oneClient.id= 123L;
        oneClient.organization=organization;

        AuthenticationContext tested = new AuthenticationContext(-99L, sameOrganization, List.of(Profile.USER));
        assertTrue(tested.canAccess(oneClient));
    }

    @Test
    void client_canAccess_should_be_false_for_other_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization otherOrganisation = new Organization();
        otherOrganisation.id = 101L;
        otherOrganisation.name = "lunatech.com";

        Client oneClient = new Client();
        oneClient.id= 123L;
        oneClient.organization=otherOrganisation;

        AuthenticationContext tested = new AuthenticationContext(-99L, organization, List.of(Profile.USER));
        assertFalse(tested.canAccess(oneClient));
    }

    @Test
    void user_canAccess_should_be_true_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization sameOrganization = new Organization();
        sameOrganization.id = 100L;
        sameOrganization.name = "lunatech.fr";

        User user = new User();
        user.id=-99L;
        user.organization=organization;
        user.profiles = List.of(Profile.USER);

        AuthenticationContext tested = new AuthenticationContext(-99L, sameOrganization, List.of(Profile.USER));
        assertTrue(tested.canAccess(user));
    }

    @Test
    void user_canAccess_should_be_false_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization otherOrg = new Organization();
        otherOrg.id = 300L;
        otherOrg.name = "lunatech.be";

        User user = new User();
        user.id=-99L;
        user.organization=organization;
        user.profiles = List.of(Profile.USER);

        AuthenticationContext tested = new AuthenticationContext(-99L, otherOrg, List.of(Profile.USER));
        assertFalse(tested.canAccess(user));
    }

    @Test
    void eventTemplate_canAccess_should_be_true_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization sameOrganization = new Organization();
        sameOrganization.id = 100L;
        sameOrganization.name = "lunatech.fr";

        EventTemplate ev = new EventTemplate();
        ev.organization = organization;

        AuthenticationContext tested = new AuthenticationContext(-99L, sameOrganization, List.of(Profile.USER));
        assertTrue(tested.canAccess(ev));
    }

    @Test
    void eventTemplate_canAccess_should_be_false_same_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization otherOrg = new Organization();
        otherOrg.id = 300L;
        otherOrg.name = "lunatech.be";


        EventTemplate ev = new EventTemplate();
        ev.organization = organization;

        AuthenticationContext tested = new AuthenticationContext(-99L, otherOrg, List.of(Profile.USER));
        assertFalse(tested.canAccess(ev));
    }

    /*********** project can edit ***************/

    @Test
    void project_canEdit_Should_Return_False_If_NoAccess_Through_Organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organization2 = new Organization();
        organization2.id = 2100L;
        organization2.name = "yolo.com";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization2, emptyList());
        assertFalse(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_True_IfAdmin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.ADMIN));
        assertTrue(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_True_If_SuperAdmin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.SUPER_ADMIN));
        assertTrue(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_false_for_public_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = true;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_false_if_not_member_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 999L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 999L;
        projectUser.user = zeUser;
        projectUser.manager = false;

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;
        project.users = List.of(projectUser);

        projectUser.project = project;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_false_if_members_is_null() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;
        project.users = null;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_true_if_user_is_manager_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = zeUser;
        projectUser.manager = true;
        projectUser.project = project;

        project.users = List.of(projectUser);

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_false_if_user_is_member_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = zeUser;
        projectUser.manager = false;
        projectUser.project = project;

        project.users = List.of(projectUser);

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertFalse(tested.canEdit(project));
    }

    @Test
    void project_canEdit_Should_Return_true_if_user_is_team_leader_of_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        var projectUser = new ProjectUser();
        projectUser.id = 1L;
        projectUser.user = zeUser;
        projectUser.manager = true; // Team leader
        projectUser.project = project;

        project.users = List.of(projectUser);

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canEdit(project));
    }


    @Test
    void timeEntry_canCreate_should_be_true_if_can_access_timesheet_and_is_owner() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;
        timeSheet.owner = zeUser; // Same user

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id=999L;
        timeEntry.startDateTime= LocalDateTime.now();
        timeEntry.endDateTime= LocalDateTime.now();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = "??";


        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertTrue(tested.canCreate(timeEntry));
    }

    @Test
    void timeEntry_canCreate_should_be_false_if_can_access_timesheet_and_is_not_owner() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        User anotherUser = new User();
        anotherUser.id = 222L;
        anotherUser.profiles = List.of(Profile.USER);
        anotherUser.organization = organization;
        anotherUser.email = "test222@lunatech.fr";
        anotherUser.picture = null;
        anotherUser.firstName = "Bob";
        anotherUser.lastName = "Test";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;
        timeSheet.owner = anotherUser; // Same user

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id=999L;
        timeEntry.startDateTime= LocalDateTime.now();
        timeEntry.endDateTime= LocalDateTime.now();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = "??";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.USER));
        assertFalse(tested.canCreate(timeEntry));
    }

    @Test
    void timeEntry_canCreate_should_be_true_if_user_is_admin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        User anotherUser = new User();
        anotherUser.id = 222L;
        anotherUser.profiles = List.of(Profile.USER);
        anotherUser.organization = organization;
        anotherUser.email = "test222@lunatech.fr";
        anotherUser.picture = null;
        anotherUser.firstName = "Bob";
        anotherUser.lastName = "Test";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;
        timeSheet.owner = anotherUser; // Same user

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id=999L;
        timeEntry.startDateTime= LocalDateTime.now();
        timeEntry.endDateTime= LocalDateTime.now();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = "??";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.ADMIN));
        assertTrue(tested.canCreate(timeEntry));
    }

    @Test
    void timeEntry_canCreate_should_be_true_if_user_is_super_admin() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User zeUser = new User();
        zeUser.id = 1L;
        zeUser.profiles = List.of(Profile.USER);
        zeUser.organization = organization;
        zeUser.email = "test@lunatech.fr";
        zeUser.picture = null;
        zeUser.firstName = "Nic";
        zeUser.lastName = "Marti";

        User anotherUser = new User();
        anotherUser.id = 222L;
        anotherUser.profiles = List.of(Profile.USER);
        anotherUser.organization = organization;
        anotherUser.email = "test222@lunatech.fr";
        anotherUser.picture = null;
        anotherUser.firstName = "Bob";
        anotherUser.lastName = "Test";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = false;

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = project;
        timeSheet.owner = anotherUser; // Same user

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id=999L;
        timeEntry.startDateTime= LocalDateTime.now();
        timeEntry.endDateTime= LocalDateTime.now();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = "??";

        AuthenticationContext tested = new AuthenticationContext(zeUser.id, organization, List.of(Profile.SUPER_ADMIN));
        assertTrue(tested.canCreate(timeEntry));
    }


    @Test
    void project_canJoin_should_return_true_for_public_project() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = true;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization, List.of(Profile.USER));
        assertTrue(tested.canJoin(project));
    }

    @Test
    void project_canJoin_should_return_false_for_different_organization() {
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        Organization organization2 = new Organization();
        organization.id = 99L;
        organization.name = "lunatech.nl";

        Project project = new Project();
        project.id = 99L;
        project.organization = organization;
        project.publicAccess = true;

        Long userId = 1L;

        AuthenticationContext tested = new AuthenticationContext(userId, organization2, List.of(Profile.USER));
        assertFalse(tested.canJoin(project));
    }

    @Test
    void shouldReturnAuthenticationContext(){
        Organization organization = new Organization();
        organization.id = 100L;
        organization.name = "lunatech.fr";

        User anotherUser = new User();
        anotherUser.id = 222L;
        anotherUser.profiles = List.of(Profile.USER);
        anotherUser.organization = organization;
        anotherUser.email = "test222@lunatech.fr";
        anotherUser.picture = null;
        anotherUser.firstName = "Bob";
        anotherUser.lastName = "Test";

        AuthenticationContext tested = AuthenticationContext.bind(anotherUser);
        Assertions.assertEquals(222L, (long) tested.getUserId());
        Assertions.assertEquals(organization, tested.getOrganization());
    }

    @Test
    void toStringShouldNotCrashWithNulls(){
        AuthenticationContext tested = new AuthenticationContext(1L, null, List.of(Profile.USER));
        assertNull(tested.getOrganization());
        assertNotNull(tested.toString());
    }
}