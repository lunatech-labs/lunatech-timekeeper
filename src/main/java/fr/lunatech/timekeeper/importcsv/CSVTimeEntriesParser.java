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
import fr.lunatech.timekeeper.services.ClientService;

import javax.inject.Inject;
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
        updateOrCreateClients(clients);

        var projects = entries.stream().map(entry -> {
            String[][] toReturn = new String[1][1];
             toReturn[0][0] = entry.getClient();
             toReturn[0][1] = entry.getClient();
             return toReturn;
        }).collect(Collectors.toMap( e->e[0],e -> e[1]));
        System.out.println("Projects : " + projects);

        return "ok";
    }

    @Transactional
    protected void updateOrCreateClients(List<String> clients){
        Organization defaultOrganization = Organization.findById(1L);

        clients.stream().filter(clientName -> !clientName.isBlank()).forEach( clientName -> {
            Optional<Client> maybeClient = Client.find("name", clientName).firstResultOptional();
            if(maybeClient.isPresent()){
                System.out.println("Skip existing client " + maybeClient.get());
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


    }
}
