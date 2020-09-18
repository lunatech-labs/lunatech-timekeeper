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

import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Named("CSVTimeEntriesParserBean")
public class CSVTimeEntriesParser {

    public String importEntries(List<ImportedTimeEntry> entries) {

        System.out.println("Got entries " + entries.size());

        var clients = entries.stream().map(entry -> entry.getClient()).distinct().collect(Collectors.toList());
        System.out.println("Clients : " + clients);
        //updateOrCreateClients(clients);

        var projects = entries
                .stream()
                .map(entry -> Tuple2.of(entry.getProject(), entry.getClient()))
                .distinct()
                .collect(Collectors.toMap(Tuple2::getItem1, Tuple2::getItem2));
        System.out.println("Projects : " + projects);
        updateOrCreateProjects(projects);

        return "ok";
    }

    @Transactional
    protected void updateOrCreateClients(List<String> clients){
        Organization defaultOrganization = Organization.findById(1L);

        clients.stream().filter(clientName -> !clientName.isBlank()).forEach( clientName -> {
            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional();
            if(maybeClient.isPresent()){
                System.out.println("Skip existing client [" + maybeClient.get().name+"]");
            }else{
                System.out.println("Create client ["+clientName+"]");
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
    protected void updateOrCreateProjects(Map<String,String> clientsAndProjects){
        Organization defaultOrganization = Organization.findById(1L);


        clientsAndProjects.entrySet().stream().filter(projectAndClient -> !projectAndClient.getKey().isBlank()).forEach( projectAndClient -> {

            String projectName = projectAndClient.getKey();
            String clientName = projectAndClient.getValue();

            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional();

            if(maybeClient.isPresent()){
                Optional<Project> maybeProject = Project.find("name",projectName).firstResultOptional();
                if(maybeProject.isPresent()){
                    System.out.println("Skip existing project ["+maybeProject.get().name+"]");
                }else{
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
            }else{
                System.out.println("Client not found, cannot import project");
            }
        });

    }
}
