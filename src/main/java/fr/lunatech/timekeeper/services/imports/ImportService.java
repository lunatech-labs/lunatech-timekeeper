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

import fr.lunatech.timekeeper.importandexportcsv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedClientProject;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedUserProjectClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class ImportService {

    private static Logger logger = LoggerFactory.getLogger(ImportService.class);

    /**
     * Create clients from clients name and persists it in DB
     *
     * @param clientsName    as a List of String
     * @param organizationId as a Long
     * @return the list of id of the clients persisted
     */
    @Transactional
    protected List<Long> createClients(List<String> clientsName, Long organizationId) {
        List<Long> clientsIds = new ArrayList<>();
        Organization defaultOrganization = Organization.findById(organizationId);//NOSONAR
        Objects.requireNonNull(defaultOrganization);

        Set<String> existingClientsNames = ImportUtils.getExistingClientsNames(defaultOrganization.id);

        clientsName.forEach(clientName -> {
            if (existingClientsNames.contains(clientName.toLowerCase())) {
                logger.debug(String.format("Skip existing client [%s]", clientName));
            } else {
                logger.debug(String.format("Create client [%s]", clientName));
                clientsIds.add(ImportUtils.createClientAndPersist(clientName, defaultOrganization));
            }
        });
        return clientsIds;
    }

    /**
     * Create project from an importedClientProject and persists it in DB.
     *
     * @param clientsAndProjects as a ImportedClientProject
     * @param organizationId     as a Long
     * @return the list of id of the projects persisted
     */
    @Transactional
    protected List<Long> createProjects(List<ImportedClientProject> clientsAndProjects, Long organizationId) {
        List<Long> projectsIds = new ArrayList<>();
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        List<Client> existingClients = ImportUtils.getExistingClients(defaultOrganization.id);
        Map<String, Set<String>> existingProjectsByClient = ImportUtils.getExistingProjectsByClient(defaultOrganization.id);

        clientsAndProjects.forEach(projectAndClient -> {

            String projectName = projectAndClient.getProjectName();
            String clientName = projectAndClient.getClientName();

            if (existingClients.stream().anyMatch(client -> clientName.equalsIgnoreCase(client.name))) {
                if (existingProjectsByClient.containsKey(clientName.toLowerCase())
                        && existingProjectsByClient.get(clientName.toLowerCase()).contains(projectName.toLowerCase())) {
                    logger.debug(String.format("Skip existing project [%s]", projectName));
                } else {
                    Client client = existingClients.stream().filter(client1 -> client1.name.equalsIgnoreCase(clientName)).findAny().orElseThrow(() -> new IllegalStateException("Client " + clientName + " not in list"));
                    projectsIds.add(ImportUtils.createProjectAndPersist(projectName, defaultOrganization, client));
                }
            } else {
                logger.warn(String.format("Client not found, cannot import project %s", projectName));
            }
        });
        return projectsIds;
    }

    /**
     * Create a user and persists it and add the user in project and update it in DB.
     *
     * @param userEmailAndProjectName as ImportedUserProjectClient
     * @param organizationId          as a Long
     * @return the list of id of the projects updated
     */
    @Transactional
    protected List<Long> createUserAndAddInProject(List<ImportedUserProjectClient> userEmailAndProjectName, Long organizationId) {
        List<Long> projectsIds = new ArrayList<>();
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        Map<String, User> existingUsersEmail = ImportUtils.getExistingEmailAndUser(defaultOrganization.id);

        userEmailAndProjectName.forEach(entry -> {

            String email = entry.getUserEmail();
            String correctEmail = ImportUtils.updateEmailToOrganization(entry.getUserEmail(), defaultOrganization.tokenName);
            String userName = entry.getUserName();
            String projectName = entry.getProjectName();
            String clientName = entry.getClientName();

            if (!existingUsersEmail.containsKey(correctEmail)) {
                ImportUtils.createUserAndPersist(correctEmail, userName, defaultOrganization, email);
                //Needed to get the user Id that is created when persisted
                // get the user just created and add it to the list of existing users
                User user = User.find("email", correctEmail).firstResult();
                Objects.requireNonNull(user);
                existingUsersEmail.put(user.email, user);
            }
            projectsIds.add(ImportUtils.addUserInProjectAndUpdate(projectName.toLowerCase(), clientName, correctEmail, existingUsersEmail, defaultOrganization.id));
        });
        return projectsIds;
    }

    /**
     * Create a timeentry and persists it
     *
     * @param timeEntries    as a List<ImportedTimeEntry>
     * @param organizationId as a Long
     * @return the list of id of the timeEntries persisted
     */
    @Transactional
    protected List<Long> createTimeEntries(List<ImportedTimeEntry> timeEntries, Long organizationId) {
        List<Long> timeEntriesIds = new ArrayList<>();
        Organization defaultOrganization = Organization.findById(organizationId); // NOSONAR
        Objects.requireNonNull(defaultOrganization);

        Map<String, User> existingUsersEmail = ImportUtils.getExistingEmailAndUser(defaultOrganization.id);
        Map<String, Project> existingProjects = ImportUtils.getExistingProject(defaultOrganization.id);

        timeEntries.stream()
                .filter(entry -> Objects.nonNull(entry.getProject()) &&
                        Objects.nonNull(entry.getClient()) &&
                        Objects.nonNull(entry.getEmail()) &&
                        Objects.nonNull(entry.getDescription()))
                .forEach(importedTimeEntry -> {

                    String projectNameLower = importedTimeEntry.getProject().toLowerCase();
                    String clientName = importedTimeEntry.getClient();
                    String userEmail = ImportUtils.updateEmailToOrganization(importedTimeEntry.getEmail(), defaultOrganization.tokenName);

                    if (projectNameLower.isBlank()) {
                        logger.error("Cannot create a TimeEntry cause project is empty");
                        logger.error("importedTimeEntry =>" + importedTimeEntry);
                        return;
                    }

                    logger.debug("--> project=" + projectNameLower + " client=" + clientName + " user=" + userEmail);

                    if (existingProjects.containsKey(projectNameLower)) {
                        if (existingProjects.get(projectNameLower).client.name.equalsIgnoreCase(clientName)) {
                            if (existingUsersEmail.containsKey(userEmail)) {
                                var user = existingUsersEmail.get(userEmail);
                                var project = existingProjects.get(projectNameLower);
                                var timeSheet = ImportUtils.getTimeSheet(user, project);

                                String comment = ImportUtils.computeComment(importedTimeEntry.getDescription(), project.name);
                                LocalDateTime startDateTime = ImportUtils.parseToLocalDateTime(importedTimeEntry.getStartDate(), importedTimeEntry.getStartTime());
                                LocalDateTime endDateTime = ImportUtils.parseToLocalDateTime(importedTimeEntry.getEndDate(), importedTimeEntry.getEndTime());
                                Optional<TimeEntry> maybeTimeEntry = TimeEntry.find("comment=?1 and timesheet_id=?2 and startdatetime=?3 and enddatetime=?4", comment, timeSheet.id, startDateTime, endDateTime).firstResultOptional();

                                if (maybeTimeEntry.isEmpty()) {
                                    timeEntriesIds.add(ImportUtils.createTimeEntryAndPersist(comment, startDateTime, endDateTime, timeSheet));
                                } else {
                                    logger.warn("TimeEntry " + comment + " from " + startDateTime + " to " + endDateTime + " already exists");
                                }
                            } else {
                                logger.warn("User " + userEmail + " not found");
                            }
                        } else {
                            logger.warn("Client not found name=[" + clientName + "]");
                        }
                    } else {
                        logger.warn("insertOrUpdateTimeEntries ----> Project not found name=[" + projectNameLower + "]");
                    }
                });
        return timeEntriesIds;
    }
}
