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
import org.jose4j.jwk.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class ImportService {

    private static Logger logger = LoggerFactory.getLogger(ImportService.class);

    //TODO : mettre des var

    @Transactional
    protected void createClients(List<String> clientsName, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId);//NOSONAR
        Objects.requireNonNull(defaultOrganization);

        Set<String> existingClientsNames;
        try (final Stream<Client> clientsStream = Client.streamAll()) {
             existingClientsNames = clientsStream
                     .filter(client -> client.organization.id.equals(defaultOrganization.id))
                     .map(client -> client.name.toLowerCase())
                     .collect(toSet());
        }

        clientsName.forEach(clientName -> {
            if (existingClientsNames.contains(clientName.toLowerCase())) {
                logger.debug(String.format("Skip existing client [%s]", clientName));
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

    @Transactional
    protected void updateOrCreateProjects(List<ImportedClientProject> clientsAndProjects, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        List<Client> existingClients;
        try (final Stream<Client> clientsStream = Client.streamAll()) {
            existingClients = clientsStream
                    .filter(client -> client.organization.id.equals(defaultOrganization.id))
                    .collect(Collectors.toList());
        }

        Map<String, Set<String>> existingProjectsByClient;
        try (final Stream<Project> projectsStream = Project.streamAll()) {
            existingProjectsByClient = projectsStream
                    .filter(project -> project.organization.id.equals(defaultOrganization.id))
                    .collect(Collectors.groupingBy(p -> p.client.name.toLowerCase(), mapping(p2 -> p2.name.toLowerCase(), toSet())));
        }

        clientsAndProjects.forEach(projectAndClient -> {

            String projectName = projectAndClient.getProjectName();
            String clientName = projectAndClient.getClientName();

            if (existingProjectsByClient.containsKey(clientName.toLowerCase())) {
                if (existingProjectsByClient.get(clientName.toLowerCase()).contains(projectName.toLowerCase())) {
                    logger.debug(String.format("Skip existing project [%s]", projectName));
                } else {
                    Client client = existingClients.stream().filter(client1 -> client1.name.equalsIgnoreCase(clientName)).findAny().orElseThrow(() -> new IllegalStateException("Client " + clientName + " not in list"));
                    Project project = new Project();
                    project.name = projectName;
                    project.publicAccess = true;
                    project.organization = defaultOrganization;
                    project.billable = true;
                    project.client = client;
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
    protected void checkUserMembership(List<ImportedUserProjectClient> userEmailAndProjectName, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        Map<String, User> existingUsersEmail;
        try (final Stream<User> userStream = User.streamAll()) {
            existingUsersEmail = userStream
                    .filter(user -> user.organization.id.equals(defaultOrganization.id))
                    .collect(Collectors.toMap(user -> user.email, user -> user));
        }

        Map<String, Project> existingProjects;
        try (final Stream<Project> projectsStream = Project.streamAll()) {
            existingProjects = projectsStream
                    .filter(project -> project.organization.id.equals(defaultOrganization.id))
                    .collect(Collectors.toMap(project -> project.name.toLowerCase(), project -> project));
        }

        userEmailAndProjectName.forEach(entry -> {

            String correctEmail = ImportUtils.updateEmailToOrganization(entry.getUserEmail(), defaultOrganization);
            String userName = entry.getUserName();
            String projectName = entry.getProjectName();
            String clientName = entry.getClientName();

            if (!existingUsersEmail.containsKey(correctEmail)) {
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
                User user = User.find("email", correctEmail).firstResult();
                Objects.requireNonNull(user);
                existingUsersEmail.put(user.email, user);
            }
            if (existingProjects.containsKey(projectName.toLowerCase())) {
                if (existingProjects.get(projectName.toLowerCase()).client.name.equalsIgnoreCase(clientName)) {
                    var project = existingProjects.get(projectName.toLowerCase());
                    Objects.requireNonNull(project);
                    if (project.users.stream().anyMatch(u -> u.user.email.equals(correctEmail))) {
                        logger.debug("User already belongs to project " + project.name);
                    } else {
                        logger.info("Add user [" + correctEmail + " as member of project [" + projectName + "]");
                        var user = existingUsersEmail.get(correctEmail);
                        Objects.requireNonNull(user);

                        var projectUser = new ProjectUser();
                        projectUser.user =  user;
                        projectUser.manager = false;
                        projectUser.project = project;
                        project.users.add(projectUser);
                        project.persistAndFlush();
                    }
                } else {
                    logger.warn("client [" + clientName + "] does not match the project's client [" + projectName + "] we found for this user");
                }
            } else {
                logger.warn("project " + entry.getProjectName() + " not found");
            }
        });
    }

    @Transactional
    protected void insertOrUpdateTimeEntries(List<ImportedTimeEntry> timeEntries, Long organizationId) {
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        timeEntries.stream()
                .filter(entry -> Objects.nonNull(entry.getProject()) &&
                        Objects.nonNull(entry.getClient()) &&
                        Objects.nonNull(entry.getEmail()) &&
                        Objects.nonNull(entry.getDescription()))
                .forEach(importedTimeEntry -> {

                    String projectName = importedTimeEntry.getProject();
                    String clientName = importedTimeEntry.getClient();
                    String userEmail = ImportUtils.updateEmailToOrganization(importedTimeEntry.getEmail(), defaultOrganization);

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
                                //TODO vérifier l'existance d'une timeEntry, skip si existante
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
