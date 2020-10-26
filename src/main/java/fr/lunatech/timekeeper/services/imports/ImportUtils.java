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

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.imports.UserImportExtension;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class ImportUtils {

    private static Logger logger = LoggerFactory.getLogger(ImportUtils.class);

    private ImportUtils() {
    }

    /**
     * Parse a date and a time as a Strings to a LocalDateTime
     *
     * @param date as a String
     * @param time as a String
     * @return a LocalDateTime
     */
    protected static LocalDateTime parseToLocalDateTime(String date, String time) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null date");
        }
        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null time");
        }
        LocalDate dateUpdated = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        LocalTime timeUpdated = LocalTime.parse(time);

        return LocalDateTime.of(dateUpdated, timeUpdated);
    }

    /**
     * Updates an email with the domain name of the organization.
     *
     * @param email     as a String
     * @param tokenName as an String
     * @return the final email as a String
     */
    protected static String updateEmailToOrganization(String email, String tokenName) {
        if (email == null) return null;

        String emailAddress = email.substring(0, email.indexOf("@"));
        String domain = email.substring(email.indexOf("@") + 1);

        if (!domain.endsWith(tokenName)) {
            return (emailAddress + "@" + tokenName).trim().toLowerCase();
        }
        return email.trim().toLowerCase();
    }

    /**
     * Computes a comment from the description.
     *
     * @param importedTimeEntryDescription as a String
     * @param projectName                  as a String
     * @return the comment as a String
     */
    protected static String computeComment(String importedTimeEntryDescription, String projectName) {
        if (importedTimeEntryDescription == null || importedTimeEntryDescription.isBlank()) {
            return "Worked on project " + StringUtils.abbreviate(projectName, 50);
        }
        return importedTimeEntryDescription;
    }

    /**
     * Try to find the timesheet in DB, if not existing, creates and persists it and then return the timesheet
     *
     * @param user    as a User
     * @param project as a Project
     * @return a Timesheet
     */
    protected static TimeSheet getTimeSheet(User user, Project project) {
        Optional<TimeSheet> maybeTimeSheet = TimeSheet.find("user_id=?1 and project_id=?2", user.id, project.id).firstResultOptional();

        if (maybeTimeSheet.isEmpty()) {
            logger.warn("Create a TimeSheet for user=" + user.email + " and project=" + project.name);
            Long timeSheetId = createTimesheetAndPersist(user, project);
            TimeSheet timeSheet = TimeSheet.findById(timeSheetId);
            Objects.requireNonNull(timeSheet);
            return timeSheet;
        }
        return maybeTimeSheet.get();
    }

    /**
     * Get the user from the existing user list, create a UserProject with the project's name
     * and the client's name
     *
     * @param projectName        as a String
     * @param clientName         as a String
     * @param correctEmail       as a String
     * @param existingUsersEmail as a Map<String, User>
     * @param organizationId     as a Long
     * @return the id of the project updated
     */
    protected static Long addUserInProjectAndUpdate(String projectName, String clientName, String correctEmail, Map<String, User> existingUsersEmail, Long organizationId) {
        Map<String, Project> existingProjects = getExistingProject(organizationId);

        if (existingProjects.containsKey(projectName)) {
            if (existingProjects.get(projectName).client.name.equalsIgnoreCase(clientName)) {
                var project = existingProjects.get(projectName);
                Objects.requireNonNull(project);
                if (project.users.stream().anyMatch(u -> u.user.email.equals(correctEmail))) {
                    logger.debug("User already belongs to project " + project.name);
                } else {
                    logger.info("Add user [" + correctEmail + " as member of project [" + projectName + "]");
                    var user = existingUsersEmail.get(correctEmail);
                    Objects.requireNonNull(user);
                    return createUserProjectAddInProjectAndPersist(user, project);
                }
            } else {
                logger.warn("client [" + clientName + "] does not match the project's client [" + projectName + "] we found for this user");
            }
        } else {
            logger.warn("checkUserMembership -->  project " + projectName + " not found");
        }
        return null;
    }

    /**
     * Create a TimeEntry and persists it in BD
     *
     * @param comment       as a String
     * @param startDateTime as a LocalDateTime
     * @param endDateTime   as a LocalDateTime
     * @param timeSheet     as a Timesheet
     * @return the id of the TimeEntry persisted as a Long
     */
    protected static Long createTimeEntryAndPersist(String comment, LocalDateTime startDateTime, LocalDateTime endDateTime, TimeSheet timeSheet) {
        var timeEntry = new TimeEntry();
        timeEntry.timeSheet = timeSheet;
        timeEntry.comment = comment;
        timeEntry.startDateTime = startDateTime;
        timeEntry.endDateTime = endDateTime;
        if (timeEntry.getRoundedNumberOfHours() == 0) {
            logger.warn("--- Fixed a timeEntry to 1h min");
            timeEntry.endDateTime = timeEntry.startDateTime.plusHours(1);
        }
        timeEntry.persistAndFlush();
        return timeEntry.id;
    }

    /**
     * Create a Client and persists it in BD
     *
     * @param clientName          as a String
     * @param defaultOrganization as an Organization
     * @return the id of the client persisted as a Long
     */
    protected static Long createClientAndPersist(String clientName, Organization defaultOrganization) {
        Client client = new Client();
        client.name = clientName;
        client.description = "Imported from CSV file";
        client.organization = defaultOrganization;
        client.projects = Collections.emptyList();
        client.persistAndFlush();
        return client.id;
    }

    /**
     * Create a project and persists it in DB.
     *
     * @param projectName         as a String
     * @param defaultOrganization as an Organization
     * @param client              as a Client
     * @return the id of the project persisted as a Long
     */
    protected static Long createProjectAndPersist(String projectName, Organization defaultOrganization, Client client) {
        Project project = new Project();
        project.name = projectName;
        project.publicAccess = true;
        project.organization = defaultOrganization;
        project.billable = true;
        project.client = client;
        project.description = "Imported from CSV File";
        project.version = 1L;
        project.persistAndFlush();
        return project.id;
    }

    /**
     * Create a User and persists it in DB
     *
     * @param correctEmail        as a String
     * @param userName            as a String
     * @param defaultOrganization as an Organization
     * @return the id of the user persisted as a Long
     */
    protected static Long createUserAndPersist(String correctEmail, String userName, Organization defaultOrganization, String email) {
        var newUser = new User();
        newUser.email = correctEmail;
        if (userName.contains(" ")) {
            newUser.firstName = userName.substring(0, userName.indexOf(" "));
            newUser.lastName = userName.substring(userName.indexOf(" ") + 1);
        } else {
            newUser.lastName = userName;
        }
        newUser.organization = defaultOrganization;
        newUser.profiles = new ArrayList<>(1);
        newUser.profiles.add(Profile.USER);
        newUser.persistAndFlush();

        //Create the extension table that contains the email from the import file
        var userImportExtension = new UserImportExtension();
        userImportExtension.user = newUser;
        userImportExtension.userEmailFromImport = email;
        userImportExtension.persistAndFlush();

        return newUser.id;
    }

    /**
     * Add a user in a Project and persists the project modification in DB
     *
     * @param user    as a User
     * @param project as a Project
     * @return the id of the project persisted as a Long
     */
    protected static Long createUserProjectAddInProjectAndPersist(User user, Project project) {
        var projectUser = new ProjectUser();
        projectUser.user = user;
        projectUser.manager = false;
        projectUser.project = project;
        project.users.add(projectUser);
        project.persistAndFlush();
        return project.id;
    }

    /**
     * Create a timeSheet and persists it in DB
     *
     * @param user    as a User
     * @param project as a Project
     * @return the id of the timesheet persisted as a Long
     */
    protected static Long createTimesheetAndPersist(User user, Project project) {
        var timeSheet = new TimeSheet();
        timeSheet.owner = user;
        timeSheet.entries = Lists.newArrayListWithExpectedSize(1);
        timeSheet.durationUnit = TimeUnit.HOURLY;
        timeSheet.startDate = LocalDate.of(2020, 1, 1);
        timeSheet.timeUnit = TimeUnit.HOURLY;
        timeSheet.defaultIsBillable = true;
        timeSheet.project = project;
        timeSheet.persistAndFlush();
        return timeSheet.id;
    }


    /**
     * Returns the clients names from DB.
     *
     * @param organizationId as a Long
     * @return a Set of Client name as String
     */
    protected static Set<String> getExistingClientsNames(Long organizationId) {
        try (final Stream<Client> clientsStream = Client.streamAll()) {
            return clientsStream
                    .filter(client -> client.organization.id.equals(organizationId))
                    .map(client -> client.name.toLowerCase())
                    .collect(toSet());
        }
    }

    /**
     * Returns the email of a user from DB and the user matching from DB.
     *
     * @param organizationId as a Long
     * @return a Map of an email (String) as a key and a user (User) as value.
     */
    protected static Map<String, User> getExistingEmailAndUser(Long organizationId) {
        try (final Stream<User> userStream = User.streamAll()) {
            return userStream
                    .filter(user -> user.organization.id.equals(organizationId))
                    .collect(Collectors.toMap(user -> user.email, user -> user));
        }
    }

    /**
     * Returns the project name and project matching from DB
     *
     * @param organizationId as a Long
     * @return a Map of project's name (String) as key and project (Project) as value.
     */
    protected static Map<String, Project> getExistingProject(Long organizationId) {
        try (final Stream<Project> projectsStream = Project.streamAll()) {
            return projectsStream
                    .filter(project -> project.organization.id.equals(organizationId))
                    .collect(Collectors.toMap(project -> project.name.toLowerCase(), project -> project));
        }
    }

    /**
     * Returns the clients from DB
     *
     * @param organizationId as a Long
     * @return a List of Client
     */
    protected static List<Client> getExistingClients(Long organizationId) {
        try (final Stream<Client> clientsStream = Client.streamAll()) {
            return clientsStream
                    .filter(client -> client.organization.id.equals(organizationId))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns the projects name by clients name from DB.
     *
     * @param organizationId as a Long
     * @return a Map of clients name (String) as a key and a set of projects name (String) as value
     */
    protected static Map<String, Set<String>> getExistingProjectsByClient(Long organizationId) {
        try (final Stream<Project> projectsStream = Project.streamAll()) {
            return projectsStream
                    .filter(project -> project.organization.id.equals(organizationId))
                    .collect(Collectors.groupingBy(p -> p.client.name.toLowerCase(), mapping(p2 -> p2.name.toLowerCase(), toSet())));
        }
    }
}
