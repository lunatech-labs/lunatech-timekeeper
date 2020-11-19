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

package fr.lunatech.timekeeper.services.exports;

import fr.lunatech.timekeeper.csv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.imports.UserImportExtension;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExportService {

    private static Logger logger = LoggerFactory.getLogger(ExportService.class);

    private static final int EXPORT_PAGE_SIZE = 50;

    @ConfigProperty(name = "timekeeper.export.folder")
    String rootFile;

    /**
     * get importedTimeEntries between two date from database
     *
     * @param startDate and endDate
     * @return a File filled with the importedTimeEntries
     */
    @Transactional
    public File getImportedTimeEntriesBetweenTwoDate(LocalDate start, LocalDate end) throws IOException {


        final LocalDateTime paramStartDateTime = start.atTime(0, 1);
        final LocalDateTime paramEndDateTime = end.atTime(23, 59);

        PanacheQuery<TimeEntry> query = TimeEntry
                .find("startDateTime >= :paramStartDateTime AND endDateTime <= :paramEndDateTime",
                        Parameters
                                .with("paramStartDateTime", paramStartDateTime)
                                .and("paramEndDateTime", paramEndDateTime)
                ).page(Page.ofSize(EXPORT_PAGE_SIZE));
        var toExport = query.stream().map(this::computeImportedTimeEntry).collect(Collectors.toList());

        var filename = rootFile + "/export_at_" + LocalDate.now() + "_from_" + start + "_to_" + end + ".csv";
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("File can't be create");
            }
            while (!toExport.isEmpty()) {
                String csvLines = toExport.stream().map(importedTimeEntry -> {
                    //todo transform to csv format here
                    var csvLine = importedTimeEntry.toString();
                    return csvLine;
                }).collect(Collectors.joining("\n"));
                writer.append(csvLines);
                writer.append("\n");
                toExport = query.nextPage().stream().map(this::computeImportedTimeEntry).collect(Collectors.toList());
            }
        }
        return file;
    }

    /**
     * Compute an importedTimeEntry from a timeEntry
     *
     * @param timeEntry
     * @return an ImportedTimeEntry
     */
    protected ImportedTimeEntry computeImportedTimeEntry(TimeEntry timeEntry) {
        // Compute userName with firstName and lastName
        String user = timeEntry.timeSheet.owner.firstName + " " + timeEntry.timeSheet.owner.lastName;
        // User Email
        String email = getEmail(timeEntry);
        // Client Name
        String clientName = null != timeEntry.timeSheet.project.client
                ? timeEntry.timeSheet.project.client.name
                : "No client";
        // Project Name
        String projectName = timeEntry.timeSheet.project.name;
        //Description
        String description = timeEntry.comment;
        //Compute billable
        String billable = timeEntry.timeSheet.defaultIsBillable ? "Yes" : "No";
        //StartDate & Time
        String startDate = TimeKeeperDateUtils.formatToString(timeEntry.startDateTime.toLocalDate());
        String startTime = TimeKeeperDateUtils.formatToString(timeEntry.startDateTime.toLocalTime());
        //EndDate & Time
        String endDate = TimeKeeperDateUtils.formatToString(timeEntry.endDateTime.toLocalDate());
        String endTime = TimeKeeperDateUtils.formatToString(timeEntry.endDateTime.toLocalTime());
        // Duration
        String duration = TimeKeeperDateUtils.getDurationIntoHoursMinutesAndSecondAsString(timeEntry.startDateTime, timeEntry.endDateTime);

        logger.debug("Every information are retrieved, now creating an ImportedTimeEntry");

        return new ImportedTimeEntry(
                user,
                email,
                clientName,
                projectName,
                "",
                description,
                billable,
                startDate,
                startTime,
                endDate,
                endTime,
                duration,
                "",
                "");
    }

    /**
     * returns the userImportExtension from DB to get the email of each user
     *
     * @return a list of UserImportExtension
     */
    private List<UserImportExtension> getUserImportExtension() {
        try (final Stream<UserImportExtension> userImportExtensionStream = UserImportExtension.streamAll()) {
            return userImportExtensionStream
                    .collect(Collectors.toList());
        }
    }

    /**
     * Return the email of a specific user
     *
     * @param timeEntry as a TimeEntry
     * @return the email as a String
     */
    private String getEmail(TimeEntry timeEntry) {
        List<UserImportExtension> userImportExtensionList = getUserImportExtension();
        Optional<UserImportExtension> maybeUserImportExtension = userImportExtensionList.stream()
                .filter(userImportExtension -> userImportExtension.user.id.equals(timeEntry.timeSheet.owner.id))
                .findFirst();

        if (maybeUserImportExtension.isPresent()) {
            return maybeUserImportExtension.get().userEmailFromImport;
        } else {
            logger.warn("User Extension not found, set email with user email");
            return timeEntry.timeSheet.owner.email;
        }
    }
}
