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

package fr.lunatech.timekeeper.importandexportcsv;

import fr.lunatech.timekeeper.services.imports.businessClass.ImportedClientProject;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedUserProjectClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CSVTimeEntriesParser {

    private static Logger logger = LoggerFactory.getLogger(CSVTimeEntriesParser.class);

    public List<String> importClients(List<ImportedTimeEntry> importedTimeEntries){
        logger.info("----------- Compute importedTimeEntries to Client Name -----------");
        return importedTimeEntries.stream()
                .filter(entry -> Objects.nonNull(entry.getClient()) && !entry.getClient().isBlank())
                .map(ImportedTimeEntry::getClient)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ImportedClientProject> importClientAndProject(List<ImportedTimeEntry> importedTimeEntries){
        logger.info("----------- Compute importedTimeEntries to ImportedClientProjects -----------");
        return importedTimeEntries
                .stream()
                .filter(entry -> Objects.nonNull(entry.getClient()) &&
                        Objects.nonNull(entry.getProject()) &&
                        !entry.getClient().isBlank() &&
                        !entry.getProject().isBlank())
                .map(entry -> ImportedClientProject.of(entry.getClient(), entry.getProject()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ImportedUserProjectClient> importUserProjectClient(List<ImportedTimeEntry> importedTimeEntries){
        logger.info("----------- Compute importedTimeEntries to ImportedUserProjectClient -----------");
        return importedTimeEntries
                .stream()
                .filter(entry -> Objects.nonNull(entry.getClient()) &&
                        Objects.nonNull(entry.getProject()) &&
                        Objects.nonNull(entry.getUser()) &&
                        Objects.nonNull(entry.getEmail()) &&
                        !entry.getClient().isBlank() &&
                        !entry.getProject().isBlank() &&
                        !entry.getUser().isBlank() &&
                        !entry.getEmail().isBlank())
                .map(entry -> ImportedUserProjectClient.of(entry.getEmail(), entry.getUser(), entry.getProject(), entry.getClient()))
                .collect(Collectors.toList());
    }
}