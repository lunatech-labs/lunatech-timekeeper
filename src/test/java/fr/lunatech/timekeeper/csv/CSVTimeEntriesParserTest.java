/*
 * Copyright 2020 Lunatech Labs
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

package fr.lunatech.timekeeper.csv;

import fr.lunatech.timekeeper.resources.utils.NullableConverter;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedClientProject;
import fr.lunatech.timekeeper.services.imports.businessClass.ImportedUserProjectClient;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;


class CSVTimeEntriesParserTest {

    @ParameterizedTest
    @CsvSource({
            "null",
            "''"
    })
    void shouldNotReturnAnythingForNullClient(@ConvertWith(NullableConverter.class) String clientName){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient(clientName);

        String[] expected = {};

        var actual = csvTimeEntriesParser.importClients(Arrays.asList(importedTimeEntry));

        assertThat(actual, Matchers.<Collection<String>>allOf(
            hasItems(expected),
            hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4ClientNameFor4ImportedTimeEntries(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");

        String[] expected = {
                "Client",
                "Client 2",
                "Client 3",
                "Client 4",
        };

        var actual = csvTimeEntriesParser.importClients(Arrays.asList(importedTimeEntry,importedTimeEntry2,importedTimeEntry3,importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<String>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn3ClientNameFor4ImportedTimeEntriesWithDuplicateClientName(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");

        String[] expected = {
                "Client",
                "Client 3",
                "Client 4",
        };

        var actual = csvTimeEntriesParser.importClients(Arrays.asList(importedTimeEntry,importedTimeEntry2,importedTimeEntry3,importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<String>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "null, Project",
            "Client, null",
            "'', Project",
            "Client, ''"
    })
    void shouldNotReturnAnythingForProjectWithNullClient(@ConvertWith(NullableConverter.class) String clientName,
                                                         @ConvertWith(NullableConverter.class) String projectName){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient(clientName);
        importedTimeEntry.setProject(projectName);

        ImportedClientProject[] expected = {};

        var actual = csvTimeEntriesParser.importClientAndProject(Arrays.asList(importedTimeEntry));

        assertThat(actual, Matchers.<Collection<ImportedClientProject>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4ImportedClientProjectFrom4DifferentImportedTimeEntries(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        importedTimeEntry2.setProject("Project 2");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        importedTimeEntry3.setProject("Project 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");
        importedTimeEntry4.setProject("Project 4");

        ImportedClientProject[] expected = {
                ImportedClientProject.of("Client", "Project"),
                ImportedClientProject.of("Client 2", "Project 2"),
                ImportedClientProject.of("Client 3", "Project 3"),
                ImportedClientProject.of("Client 4", "Project 4"),
        };

        var actual = csvTimeEntriesParser.importClientAndProject(Arrays.asList(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<ImportedClientProject>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn3ImportedClientProjectFrom4ImportedTimeEntriesWithDuplicateClientAndProject(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client");
        importedTimeEntry2.setProject("Project");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        importedTimeEntry3.setProject("Project 3");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");
        importedTimeEntry4.setProject("Project 4");

        ImportedClientProject[] expected = {
                ImportedClientProject.of("Client", "Project"),
                ImportedClientProject.of("Client 3", "Project 3"),
                ImportedClientProject.of("Client 4", "Project 4"),
        };

        var actual = csvTimeEntriesParser.importClientAndProject(Arrays.asList(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<ImportedClientProject>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn2ImportedClientProjectFrom2ImportedTimeEntriesWithSameClient(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client");
        importedTimeEntry2.setProject("Project 2");

        ImportedClientProject[] expected = {
                ImportedClientProject.of("Client", "Project"),
                ImportedClientProject.of("Client", "Project 2"),
        };

        var actual = csvTimeEntriesParser.importClientAndProject(Arrays.asList(importedTimeEntry, importedTimeEntry2));

        assertThat(actual, Matchers.<Collection<ImportedClientProject>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn2ImportedClientProjectFrom2ImportedTimeEntriesWithSameProject(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        importedTimeEntry2.setProject("Project");

        ImportedClientProject[] expected = {
                ImportedClientProject.of("Client", "Project"),
                ImportedClientProject.of("Client 2", "Project"),
        };

        var actual = csvTimeEntriesParser.importClientAndProject(Arrays.asList(importedTimeEntry, importedTimeEntry2));

        assertThat(actual, Matchers.<Collection<ImportedClientProject>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "null, Project, John Doe, john.doe@lunatech.fr",
            "Client, null, John Doe, john.doe@lunatech.fr",
            "Client, Project, null, john.doe@lunatech.fr",
            "Client, Project, John Doe, null",
            "'', Project, John Doe, john.doe@lunatech.fr",
            "Client, '', John Doe, john.doe@lunatech.fr",
            "Client, Project, '', john.doe@lunatech.fr",
            "Client, Project, John Doe, ''"
    })
    void shouldNotReturnAnythingForNullProjectInImportedTimeEntries(@ConvertWith(NullableConverter.class) String clientName,
                                                                    @ConvertWith(NullableConverter.class) String projectName,
                                                                    @ConvertWith(NullableConverter.class) String userName,
                                                                    @ConvertWith(NullableConverter.class) String email){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient(clientName);
        importedTimeEntry.setProject(projectName);
        importedTimeEntry.setUser(userName);
        importedTimeEntry.setEmail(email);

        ImportedUserProjectClient[] expected = {};

        var actual = csvTimeEntriesParser.importUserProjectClient(Arrays.asList(importedTimeEntry));

        assertThat(actual, Matchers.<Collection<ImportedUserProjectClient>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4ImportedUserProjectClientFor4ImportedTimeEntries(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client 2");
        importedTimeEntry2.setProject("Project 2");
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client 3");
        importedTimeEntry3.setProject("Project 3");
        importedTimeEntry3.setUser("Georges Doe");
        importedTimeEntry3.setEmail("georges.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client 4");
        importedTimeEntry4.setProject("Project 4");
        importedTimeEntry4.setUser("Bill Doe");
        importedTimeEntry4.setEmail("bill.doe@lunatech.fr");

        ImportedUserProjectClient[] expected = {
                ImportedUserProjectClient.of("john.doe@lunatech.fr","John Doe","Project","Client"),
                ImportedUserProjectClient.of("jane.doe@lunatech.fr","Jane Doe","Project 2","Client 2"),
                ImportedUserProjectClient.of("georges.doe@lunatech.fr","Georges Doe","Project 3","Client 3"),
                ImportedUserProjectClient.of("bill.doe@lunatech.fr","Bill Doe","Project 4","Client 4"),
        };

        var actual = csvTimeEntriesParser.importUserProjectClient(Arrays.asList(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<ImportedUserProjectClient>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }

    @Test
    void shouldReturn4ImportedUserProjectClientFor4UsersWorkingOnTheSameProject(){
        CSVTimeEntriesParser csvTimeEntriesParser = new CSVTimeEntriesParser();
        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry();
        importedTimeEntry.setClient("Client");
        importedTimeEntry.setProject("Project");
        importedTimeEntry.setUser("John Doe");
        importedTimeEntry.setEmail("john.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry2 = new ImportedTimeEntry();
        importedTimeEntry2.setClient("Client");
        importedTimeEntry2.setProject("Project");
        importedTimeEntry2.setUser("Jane Doe");
        importedTimeEntry2.setEmail("jane.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry3 = new ImportedTimeEntry();
        importedTimeEntry3.setClient("Client");
        importedTimeEntry3.setProject("Project");
        importedTimeEntry3.setUser("Georges Doe");
        importedTimeEntry3.setEmail("georges.doe@lunatech.fr");
        ImportedTimeEntry importedTimeEntry4 = new ImportedTimeEntry();
        importedTimeEntry4.setClient("Client");
        importedTimeEntry4.setProject("Project");
        importedTimeEntry4.setUser("Bill Doe");
        importedTimeEntry4.setEmail("bill.doe@lunatech.fr");

        ImportedUserProjectClient[] expected = {
                ImportedUserProjectClient.of("john.doe@lunatech.fr","John Doe","Project","Client"),
                ImportedUserProjectClient.of("jane.doe@lunatech.fr","Jane Doe","Project","Client"),
                ImportedUserProjectClient.of("georges.doe@lunatech.fr","Georges Doe","Project","Client"),
                ImportedUserProjectClient.of("bill.doe@lunatech.fr","Bill Doe","Project","Client"),
        };

        var actual = csvTimeEntriesParser.importUserProjectClient(Arrays.asList(importedTimeEntry, importedTimeEntry2, importedTimeEntry3, importedTimeEntry4));

        assertThat(actual, Matchers.<Collection<ImportedUserProjectClient>>allOf(
                hasItems(expected),
                hasSize(expected.length)
        ));
    }
}