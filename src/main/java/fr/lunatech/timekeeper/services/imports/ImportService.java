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
import fr.lunatech.timekeeper.importcsv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedClientProject;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedUserProjectClient;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ImportService {

    private static Logger logger = LoggerFactory.getLogger(ImportService.class);
    @Transactional
    protected void updateOrCreateProjects(List<ImportedClientProject> clientsAndProjects) {
        Organization defaultOrganization = Organization.findById(1L); // NOSONAR

        clientsAndProjects.stream()
                .filter(projectAndClient -> !projectAndClient.getClientName().isBlank() && !projectAndClient.getProjectName().isBlank())
                .forEach(projectAndClient -> {

            String projectName = clientsAndProjects.getProjectName();
            String clientName = projectAndClient.getValue();

            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional(); // NOSONAR

            if (maybeClient.isPresent()) {
                Optional<Project> maybeProject = Project.find("name", projectName).firstResultOptional(); // NOSONAR
                if (maybeProject.isPresent()) {
                    logger.debug(String.format("Skip existing project [%s]", maybeProject.get().name));
                } else {
                    Project project = new Project();
                    project.name = projectName;
                    project.publicAccess = true;
                    project.organization = defaultOrganization;
                    project.billable = true;
                    project.client = maybeClient.get();
                    project.description = "Imported from CSV File";
                    project.version = 1L;
                    project.persistAndFlush();
                }
            } else {
                logger.warn(String.format("Client not found, cannot import project %s", projectName));
            }
        });
    }

    @Transactional
    protected void checkUserMembership(List<ImportedUserProjectClient> userEmailAndProjectName) {
        Organization defaultOrganization = Organization.findById(1L); // NOSONAR

        // TODO il faut que je fasse l'update lorsque la Timeentry existe déjà...

        userEmailAndProjectName.stream().forEach(entry -> {

            String correctEmail = ImportUtils.updateEmailToOrganization(entry.getItem1(), defaultOrganization);
            String userName = entry.getItem2();
            String projectName = entry.getItem3();
            String clientName = entry.getItem4();

            Optional<User> maybeUser = User.find("email", correctEmail).firstResultOptional();
            if (maybeUser.isEmpty()) {
                User newUser = new User();
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
                maybeUser = User.find("email", correctEmail).firstResultOptional(); // NOSONAR
            }
            Optional<Project> maybeProject = Project.find("name", projectName).firstResultOptional(); // NOSONAR
            if (maybeProject.isPresent()) {
                if (maybeProject.get().client.name.equals(clientName)) {
                    var project = maybeProject.get();
                    if (project.users.stream().anyMatch(u -> u.user.email.equals(correctEmail))) {
                        logger.debug("User already belongs to project " + project.name);
                    } else {
                        logger.info("Add user [" + correctEmail + " as member of project [" + projectName + "]");
                        ProjectUser projectUser = new ProjectUser();
                        projectUser.user = maybeUser.get();
                        projectUser.manager = false;
                        projectUser.project = project;
                        project.users.add(projectUser);
                        project.persistAndFlush();
                    }
                } else {
                    logger.warn("client [" + clientName + "] does not match the project's client [" + maybeProject.get().name + "] we found for this user");
                }
            } else {
                logger.warn("project " + entry.getItem2() + " not found");
            }


        });
    }

    @Transactional
    protected void createClients(List<String> clients) {
        Organization defaultOrganization = Organization.findById(1L);//NOSONAR

        clients.stream().filter(clientName -> !clientName.isBlank()).forEach(clientName -> {
            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional(); // NOSONAR
            if (maybeClient.isPresent()) {
                logger.debug(String.format("Skip existing client [%s]", maybeClient.get().name));
            } else {
                logger.debug(String.format("Create client [%s]", clientName));
                Client c = new Client();
                c.name = clientName;
                c.description = "Imported from CSV file";
                c.organization = defaultOrganization;
                c.projects = Collections.emptyList();
                c.persistAndFlush();
            }
        });
    }
    }

    @Transactional
    protected void insertOrUpdateTimeEntries(List<ImportedTimeEntry> timeEntries) {
        timeEntries.stream().forEach(importedTimeEntry -> {

            String projectName = importedTimeEntry.getProject();
            String clientName = importedTimeEntry.getClient();
            String userEmail = importedTimeEntry.getEmail();

            if (projectName.isBlank()) {
                logger.error("Cannot create a TimeEntry cause project is empty");
                logger.error("importedTimeEntry =>" + importedTimeEntry);
                return;
            }

            logger.debug("--> project=" + projectName + " client=" + clientName + " user=" + userEmail);

            Optional<Project> maybeProject = Project.find("name", projectName).firstResultOptional();

            if (maybeProject.isPresent()) {
                if (maybeProject.get().client.name.equalsIgnoreCase(clientName)) {
                    Optional<User> maybeUser = User.find("email", userEmail).firstResultOptional();
                    if (maybeUser.isPresent()) {

                        Optional<TimeSheet> maybeTimeSheet = TimeSheet.find("user_id=?1 and project_id=?2", maybeUser.get().id, maybeProject.get().id).firstResultOptional();

                        if (maybeTimeSheet.isEmpty()) {
                            logger.warn("Create a TimeSheet for user=" + userEmail + " and project=" + projectName);
                            TimeSheet ts = new TimeSheet();
                            ts.owner = maybeUser.get();
                            ts.entries = Lists.newArrayListWithExpectedSize(1);
                            ts.durationUnit = TimeUnit.HOURLY;
                            ts.startDate = LocalDate.of(2020, 1, 1);
                            ts.timeUnit = TimeUnit.HOURLY;
                            ts.defaultIsBillable = true;
                            ts.project = maybeProject.get();
                            ts.persistAndFlush();
                            maybeTimeSheet = TimeSheet.find("user_id=?1 and project_id=?2", maybeUser.get().id, maybeProject.get().id).firstResultOptional();
                            if (maybeTimeSheet.isEmpty()) {
                                logger.error("FATAL : Could not create a TimeSheet for user=" + userEmail + " and project=" + projectName);
                                return;
                            }
                        }

                        TimeEntry timeEntry = new TimeEntry();
                        timeEntry.timeSheet = maybeTimeSheet.get();
                        timeEntry.comment = importedTimeEntry.getDescription();
                        if(timeEntry.comment==null || timeEntry.comment.isBlank()){
                            timeEntry.comment = "Worked on project " + StringUtils.abbreviate(maybeProject.get().name,50);
                        }
                        timeEntry.startDateTime = ImportUtils.parseToLocalDateTime(importedTimeEntry.getStartDate(), importedTimeEntry.getStartTime());
                        timeEntry.endDateTime = ImportUtils.parseToLocalDateTime(importedTimeEntry.getEndDate(), importedTimeEntry.getEndTime());
                        if(timeEntry.getRoundedNumberOfHours()==0){
                            logger.warn("--- Fixed a timeEntry to 1h min");
                            timeEntry.endDateTime = timeEntry.startDateTime.plusHours(1);
                        }
                        try {
                            timeEntry.persistAndFlush();
                        }catch (javax.persistence.PersistenceException e){
                            logger.error("Cannot persist a timeEntry");
                            logger.error("TimeEntry=" + timeEntry);
                            logger.error(e.getMessage());
                        }

                    } else {
                        logger.warn("User " + userEmail + " not found");
                    }
                } else {
                    logger.warn("Client not found name=[" + clientName + "]");
                }
            } else {
                logger.warn("Project not found name=[" + projectName + "]");
            }
        });
    }
}
