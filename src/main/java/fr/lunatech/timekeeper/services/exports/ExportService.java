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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private static final int EXPORT_PAGE_SIZE = 50;

    /**
     * get TimeEntries in Toggl import Csv line format between two date from database
     *
     * @param startDate and endDate
     * @return a StringBuilder filled with the timeEntries
     */
    @Transactional
    public StringBuilder getTimeEntriesBetweenTwoDateForExportToTogglCsv(LocalDate start, LocalDate end) throws IOException {
        final LocalDateTime paramStartDateTime = start.atTime(0, 1);
        final LocalDateTime paramEndDateTime = end.atTime(23, 59);

        PanacheQuery<TimeEntry> query = TimeEntry
                .find("startDateTime >= :paramStartDateTime AND endDateTime <= :paramEndDateTime",
                        Parameters
                                .with("paramStartDateTime", paramStartDateTime)
                                .and("paramEndDateTime", paramEndDateTime)
                ).page(Page.ofSize(EXPORT_PAGE_SIZE));
        var toExport = query.stream().map(this::computeImportedTimeEntry).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        try (CSVPrinter csvPrinter = new CSVPrinter(sb, CSVFormat.DEFAULT
                .withHeader(togglHeaderCsvLine().toArray(new String[0]))
                .withQuoteMode(QuoteMode.MINIMAL)
                .withDelimiter(','))) {
            while (!toExport.isEmpty()) {
                toExport.forEach(timeEntry -> {
                    try {
                        csvPrinter.printRecord(importedTimeEntryToTogglCsvLine(timeEntry).toArray());
                    } catch (IOException ioException) {
                        logger.error(ioException.getMessage());
                    }
                });
                toExport = query.nextPage().stream().map(this::computeImportedTimeEntry).collect(Collectors.toList());
            }
            csvPrinter.flush();
        }
        return sb;
    }

    protected List<String> togglHeaderCsvLine() {
        //https://support.toggl.com/en/articles/3928821-toggl-csv-import-guide#importing-time-entries
        return List.of(
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
    }

    protected List<String> importedTimeEntryToTogglCsvLine(ImportedTimeEntry importedTimeEntry) {
        //https://support.toggl.com/en/articles/3928821-toggl-csv-import-guide#importing-time-entries
        return List.of(
                importedTimeEntry.getEmail(),
                importedTimeEntry.getDuration(),
                importedTimeEntry.getStartTime(),
                importedTimeEntry.getStartDate(),
                importedTimeEntry.getDescription(),
                importedTimeEntry.getProject(),
                importedTimeEntry.getTask(),
                importedTimeEntry.getClient(),
                importedTimeEntry.getTags(),
                importedTimeEntry.getBillable()
        );
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
