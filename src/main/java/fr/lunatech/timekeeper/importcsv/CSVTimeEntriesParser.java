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


import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Project;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.tuples.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Named("CSVTimeEntriesParserBean")
public class CSVTimeEntriesParser {

    private static Logger logger = LoggerFactory.getLogger(CSVTimeEntriesParser.class);

    public String importEntries(List<ImportedTimeEntry> entries) {
        var clients = entries.stream().map(ImportedTimeEntry::getClient).distinct().collect(Collectors.toList());
        updateOrCreateClients(clients);

        var projects = entries
                .stream()
                .map(entry -> Tuple2.of(entry.getProject(), entry.getClient()))
                .distinct()
                .collect(Collectors.toMap(Tuple2::getItem1, Tuple2::getItem2));
        updateOrCreateProjects(projects);

        var members = entries
                .stream()
                .map(entry -> Tuple3.of(entry.getEmail(), entry.getProject(), entry.getClient()))
                .collect(Collectors.toList());

        checkUserMembership(members);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Total number of entries: %d", entries.size()));
            logger.debug(String.format("Number of clients: %d", clients.size()));
            logger.debug(String.format("Number of projects : %d", projects.size()));
        }

        return "ok";
    }

    @Transactional
    protected void updateOrCreateClients(List<String> clients) {
        Organization defaultOrganization = Organization.findById(1L);//NOSONAR

        clients.stream().filter(clientName -> !clientName.isBlank()).forEach(clientName -> {
            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional(); // NOSONAR
            if (maybeClient.isPresent()) {
                logger.debug(String.format("Skip existing client [%s]", maybeClient.get().name));
            } else {
                logger.debug(String.format("Create client [%s]", clientName));
                Client c = new Client();
                c.name = clientName;
                c.description = "Imported from Toggl";
                c.organization = defaultOrganization;
                c.projects = Collections.emptyList();
                c.persistAndFlush();
            }
        });
    }

    @Transactional
    protected void updateOrCreateProjects(Map<String, String> clientsAndProjects) {
        Organization defaultOrganization = Organization.findById(1L); // NOSONAR

        clientsAndProjects.entrySet().stream().filter(projectAndClient -> !projectAndClient.getKey().isBlank()).forEach(projectAndClient -> {

            String projectName = projectAndClient.getKey();
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
                    project.description = "Imported from Toggl";
                    project.version = 1L;
                    project.persistAndFlush();
                }
            } else {
                logger.warn(String.format("Client not found, cannot import project %s", projectName));
            }
        });
    }

    @Transactional
    protected void checkUserMembership(List<Tuple3<String, String, String>> userEmailAndProjectName) {
        Organization defaultOrganization = Organization.findById(1L); // NOSONAR

        userEmailAndProjectName.stream().forEach(entry -> {
            logger.debug("userEmail:" + entry.getItem1() + " project:" + entry.getItem2() + " client:" + entry.getItem3());
        });
    }
}