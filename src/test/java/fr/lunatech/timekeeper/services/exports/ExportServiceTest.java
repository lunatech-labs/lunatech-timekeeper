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

package fr.lunatech.timekeeper.services.exports;

import fr.lunatech.timekeeper.csv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

class ExportServiceTest {

    ExportService exportService = new ExportService();

    @Test
    void togglHeaderCsvLineTest() {
        //https://support.toggl.com/en/articles/3928821-toggl-csv-import-guide#importing-time-entries
        List<String> expected = List.of(
                "Email",
                "Duration",
                "Start Time",
                "Start Date",
                "Description",
                "Project",
                "Task",
                "Client",
                "Tags",
                "Billable"
        );

        assertEquals(exportService.togglHeaderCsvLine(), expected);
    }

    @Test
    void importedTimeEntryToTogglCsvLineTest() {
        //https://support.toggl.com/en/articles/3928821-toggl-csv-import-guide#importing-time-entries

        String user = "User1";
        String email = "email@gmail.com";
        String client = "Client1";
        String project = "Projet1";
        String task = "Task1";
        String description = "Une petite description, de cette timeentry";
        String billable = "No";
        String startDate = "2020-11-18";
        String startTime = "09:00:00";
        String endDate = "2020-11-18";
        String endTime = "17:00:00";
        String duration = "08:00:00";
        String tags = "tags1 et tags2";
        String amount = "10";

        ImportedTimeEntry importedTimeEntry = new ImportedTimeEntry(user, email, client, project, task, description, billable, startDate, startTime, endDate, endTime, duration, tags, amount);

        List<String> expected = List.of(
                email,
                duration,
                startTime,
                startDate,
                description,
                project,
                task,
                client,
                tags,
                billable
        );

        assertEquals(exportService.importedTimeEntryToTogglCsvLine(importedTimeEntry), expected);
    }


}