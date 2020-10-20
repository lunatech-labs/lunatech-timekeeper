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


import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.smallrye.mutiny.tuples.Tuple4;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Named("CSVTimeEntriesParserBean")
public class CSVTimeEntriesParser {

    private static final Logger logger = LoggerFactory.getLogger(CSVTimeEntriesParser.class);

    public List<Client> computeClients(List<ImportedTimeEntry> importedTimeEntries, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId);//NOSONAR

        return importedTimeEntries.stream()
                .filter(entry -> Objects.nonNull(entry.getClient()) && !entry.getClient().isBlank())
                .map(ImportedTimeEntry::getClient)
                .distinct()
                .map(clientName -> {
                    logger.debug(String.format("Create client [%s]", clientName));
                    return new Client(
                            clientName,
                            "Imported from CSV file",
                            defaultOrganization,
                            Collections.emptyList()
                    );
                }).collect(Collectors.toList());
    }

    public List<User> computeUsers(List<ImportedTimeEntry> importedTimeEntries, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR

        return importedTimeEntries.stream()
                .filter(entry -> Objects.nonNull(entry.getEmail()) &&
                        Objects.nonNull(entry.getUser()) &&
                        !entry.getUser().isBlank() &&
                        !entry.getEmail().isBlank())
                .map(entry -> {
                    String correctEmail = updateEmailToOrganization(entry.getEmail(), defaultOrganization);
                    String userName = entry.getUser();
                    return User.createUserForImport(correctEmail, userName, defaultOrganization);
                }).collect(Collectors.toList());
    }

//    public List<Project> computeProjects(List<ImportedTimeEntry> importedTimeEntries, Long organizationId) {
//        Organization defaultOrganization = Organization.findById(organizationId);//NOSONAR
//
//        //Filter ImportedTimeEntries
//        List<ImportedTimeEntry> filteredImportedTimeEntries = importedTimeEntries.stream()
//                .filter(entry -> Objects.nonNull(entry.getClient()) &&
//                        Objects.nonNull(entry.getProject()) &&
//                        Objects.nonNull(entry.getEmail()) &&
//                        Objects.nonNull(entry.getUser()) &&
//                        !entry.getClient().isBlank() &&
//                        !entry.getProject().isBlank() &&
//                        !entry.getEmail().isBlank() &&
//                        !entry.getUser().isBlank()
//                ).collect(Collectors.toList());
//
//        //Adding project in projectList
//        List<Project> projects = filteredImportedTimeEntries.stream().map(entry -> {
//            Client client = new Client(entry.getClient(), "Imported from CSV file", defaultOrganization, Collections.emptyList());
//            return new Project(
//                    defaultOrganization,
//                    client,
//                    entry.getProject(),
//                    true,
//                    "Imported from CSV File",
//                    true,
//                    Collections.emptyList(),
//                    1L);
//        }).distinct().collect(Collectors.toList());
//
//        //Adding User in project
//        filteredImportedTimeEntries.forEach(entry -> {
//            String correctEmail = updateEmailToOrganization(entry.getEmail(), defaultOrganization);
//            User user = User.createUserForImport(correctEmail, entry.getUser(), defaultOrganization);
//
//            projects.forEach(project -> {
//                if(isNotAUserProject(project, user)){
//                    addUserInProject(project, user);
//                }
//            });
//        });
//
//        return projects;
//    }

    protected boolean isNotProjectInList(List<Project> projects, Project project){
        return projects.stream().noneMatch(project1 -> project1.name.equals(project.name));
    }

    protected boolean isNotAUserProject(Project project, User user){
        return project.users.stream().noneMatch(projectUser -> projectUser.user.firstName.equals(user.firstName) &&
                projectUser.user.lastName.equals(user.lastName) &&
                projectUser.user.email.equals(user.email) &&
                projectUser.user.organization.equals(user.organization)
        );
    }

    //TODO Replace this algo, at this moment we have to force lunatech.fr because we have some lunatech.com in CSV files
    protected String updateEmailToOrganization(String email, Organization organization) {
        if (email == null) return null;

        String emailAddress = email.substring(0, email.indexOf("@"));
        String domain = email.substring(email.indexOf("@") + 1);

        if (!domain.endsWith(organization.tokenName)) {
            return (emailAddress + "@" + organization.tokenName).trim().toLowerCase();
        }
        return email.trim().toLowerCase();
    }

    private void addUserInProject(Project project, User user){
        logger.info("Add user [" + user.email + " as member of project [" + project.name + "]");
        ProjectUser projectUser = new ProjectUser();
        projectUser.user = user;
        projectUser.manager = false;
        projectUser.project = project;
        project.users.add(projectUser);
    }

//    public String importEntries(List<ImportedTimeEntry> entries) {
//        // Step 3 : members of each Project
//        var members = entries
//                .stream()
//                .map(entry -> Tuple4.of(entry.getEmail(), entry.getUser(), entry.getProject(), entry.getClient()))
//                .collect(Collectors.toList());
//
//        checkUserMembership(members);
//
//        // Step 4 : time Entry
//        insertOrUpdateTimeEntries(entries);
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Total number of entries: %d", entries.size()));
//            logger.debug(String.format("Number of clients: %d", clients.size()));
//            logger.debug(String.format("Number of projects : %d", projects.size()));
//        }
//
//        return "ok";
//    }

//    @Transactional
//    protected void checkUserMembership(List<Tuple4<String, String, String, String>> userEmailAndProjectName) {
//        Organization defaultOrganization = Organization.findById(1L); // NOSONAR
//
//        // TODO il faut que je fasse l'update lorsque la Timeentry existe déjà...
//
//        userEmailAndProjectName.stream().forEach(entry -> {
//
//            String correctEmail = updateEmailToOrganization(entry.getItem1(), defaultOrganization);
//            String userName = entry.getItem2();
//            String projectName = entry.getItem3();
//            String clientName = entry.getItem4();
//
//            Optional<User> maybeUser = User.find("email", correctEmail).firstResultOptional();
//            if (maybeUser.isEmpty()) {
//                User newUser = new User();
//                newUser.email = correctEmail;
//                if (userName.contains(" ")) {
//                    newUser.firstName = userName.substring(0, userName.indexOf(" "));
//                    newUser.lastName = userName.substring(userName.indexOf(" ") + 1);
//                } else {
//                    newUser.lastName = userName;
//                }
//                newUser.organization = defaultOrganization;
//                newUser.profiles = new ArrayList<>(1);
//                newUser.profiles.add(Profile.USER);
//                newUser.persistAndFlush();
//                maybeUser = User.find("email", correctEmail).firstResultOptional(); // NOSONAR
//            }
//            Optional<Project> maybeProject = Project.find("name", projectName).firstResultOptional(); // NOSONAR
//            if (maybeProject.isPresent()) {
//                if (maybeProject.get().client.name.equals(clientName)) {
//                    var project = maybeProject.get();
//                    if (project.users.stream().anyMatch(u -> u.user.email.equals(correctEmail))) {
//                        logger.debug("User already belongs to project " + project.name);
//                    } else {
//                        logger.info("Add user [" + correctEmail + " as member of project [" + projectName + "]");
//                        ProjectUser projectUser = new ProjectUser();
//                        projectUser.user = maybeUser.get();
//                        projectUser.manager = false;
//                        projectUser.project = project;
//                        project.users.add(projectUser);
//                        project.persistAndFlush();
//                    }
//                } else {
//                    logger.warn("client [" + clientName + "] does not match the project's client [" + maybeProject.get().name + "] we found for this user");
//                }
//            } else {
//                logger.warn("project " + entry.getItem2() + " not found");
//            }
//
//
//        });
//    }
//
//
//    @Transactional
//    protected void insertOrUpdateTimeEntries(List<ImportedTimeEntry> timeEntries) {
//        timeEntries.stream().forEach(importedTimeEntry -> {
//
//            String projectName = importedTimeEntry.getProject();
//            String clientName = importedTimeEntry.getClient();
//            String userEmail = importedTimeEntry.getEmail();
//
//            if (projectName.isBlank()) {
//                logger.error("Cannot create a TimeEntry cause project is empty");
//                logger.error("importedTimeEntry =>" + importedTimeEntry);
//                return;
//            }
//
//            logger.debug("--> project=" + projectName + " client=" + clientName + " user=" + userEmail);
//
//            Optional<Project> maybeProject = Project.find("name", projectName).firstResultOptional();
//
//            if (maybeProject.isPresent()) {
//                if (maybeProject.get().client.name.equalsIgnoreCase(clientName)) {
//                    Optional<User> maybeUser = User.find("email", userEmail).firstResultOptional();
//                    if (maybeUser.isPresent()) {
//
//                        Optional<TimeSheet> maybeTimeSheet = TimeSheet.find("user_id=?1 and project_id=?2", maybeUser.get().id, maybeProject.get().id).firstResultOptional();
//
//                        if (maybeTimeSheet.isEmpty()) {
//                            logger.warn("Create a TimeSheet for user=" + userEmail + " and project=" + projectName);
//                            TimeSheet ts = new TimeSheet();
//                            ts.owner = maybeUser.get();
//                            ts.entries = Lists.newArrayListWithExpectedSize(1);
//                            ts.durationUnit = TimeUnit.HOURLY;
//                            ts.startDate = LocalDate.of(2020, 1, 1);
//                            ts.timeUnit = TimeUnit.HOURLY;
//                            ts.defaultIsBillable = true;
//                            ts.project = maybeProject.get();
//                            ts.persistAndFlush();
//                            maybeTimeSheet = TimeSheet.find("user_id=?1 and project_id=?2", maybeUser.get().id, maybeProject.get().id).firstResultOptional();
//                            if (maybeTimeSheet.isEmpty()) {
//                                logger.error("FATAL : Could not create a TimeSheet for user=" + userEmail + " and project=" + projectName);
//                                return;
//                            }
//                        }
//
//                        TimeEntry timeEntry = new TimeEntry();
//                        timeEntry.timeSheet = maybeTimeSheet.get();
//                        timeEntry.comment = importedTimeEntry.getDescription();
//                        if (timeEntry.comment == null || timeEntry.comment.isBlank()) {
//                            timeEntry.comment = "Worked on project " + StringUtils.abbreviate(maybeProject.get().name, 50);
//                        }
//                        timeEntry.startDateTime = parseToLocalDateTime(importedTimeEntry.getStartDate(), importedTimeEntry.getStartTime());
//                        timeEntry.endDateTime = parseToLocalDateTime(importedTimeEntry.getEndDate(), importedTimeEntry.getEndTime());
//                        if (timeEntry.getRoundedNumberOfHours() == 0) {
//                            logger.warn("--- Fixed a timeEntry to 1h min");
//                            timeEntry.endDateTime = timeEntry.startDateTime.plusHours(1);
//                        }
//                        try {
//                            timeEntry.persistAndFlush();
//                        } catch (javax.persistence.PersistenceException e) {
//                            logger.error("Cannot persist a timeEntry");
//                            logger.error("TimeEntry=" + timeEntry);
//                            logger.error(e.getMessage());
//                        }
//
//                    } else {
//                        logger.warn("User " + userEmail + " not found");
//                    }
//                } else {
//                    logger.warn("Client not found name=[" + clientName + "]");
//                }
//            } else {
//                logger.warn("Project not found name=[" + projectName + "]");
//            }
//        });
//    }

    protected LocalDateTime parseToLocalDateTime(String date, String time) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null date");
        }
        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null time");
        }
        LocalDate dateUpdated = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        LocalTime timeUpdated = LocalTime.parse(time);

        LocalDateTime localDateTime = LocalDateTime.of(dateUpdated, timeUpdated);
        return localDateTime;
    }

}