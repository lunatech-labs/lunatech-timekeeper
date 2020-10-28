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

import fr.lunatech.timekeeper.importcsv.ImportedTimeEntry;
import fr.lunatech.timekeeper.models.imports.UserImportExtension;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.services.imports.ImportService;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExportService {

    private static Logger logger = LoggerFactory.getLogger(ImportService.class);

    /**
     * get timeentries between two date from database
     *
     * @param start as a LocalDate
     * @param end   as a LocalDate
     * @return a List of TimeEntry
     */
    protected List<TimeEntry> getTimeEntriesBetweenTwoDate(final LocalDate start, final LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start should be before end");
        }

        final LocalDateTime startDateTime = start.atTime(0, 1);
        final LocalDateTime endDateTime = end.atTime(23, 59);

        try (final Stream<TimeEntry> timeEntryStream = TimeEntry.streamAll()) {
            return timeEntryStream
                    .filter(timeEntry -> (timeEntry.startDateTime.isAfter(startDateTime) || timeEntry.startDateTime.isEqual(startDateTime))
                            && (timeEntry.endDateTime.isBefore(endDateTime) || timeEntry.endDateTime.isEqual(endDateTime))
                    )
                    .collect(Collectors.toList());
        }
    }

    /**
     * Compute a List of TimeEntry into a List of ExportedTimeEntry
     *
     * @param timeEntries as a List of TimeEntry
     * @return a List of ExportedTimeEntry
     */
    protected List<ImportedTimeEntry> getExportedTimeEntry(List<TimeEntry> timeEntries) {
        return timeEntries.stream().map(timeEntry -> {
            // Compute userName with firstName and lastName
            String user = timeEntry.timeSheet.owner.firstName + " " + timeEntry.timeSheet.owner.lastName;
            // User Email
            String email = getEmail(timeEntry);
            // Client Name
            String clientName = timeEntry.timeSheet.project.client.name;
            // Project Name
            String projectName = timeEntry.timeSheet.project.name;
            //Description
            String description = timeEntry.comment;
            //Compute billable
            String billable = (timeEntry.timeSheet.defaultIsBillable ? "Yes" : "No");
            //StartDate & Time
            String startDate = TimeKeeperDateUtils.formatToString(timeEntry.startDateTime.toLocalDate());
            String startTime = TimeKeeperDateUtils.formatToString(timeEntry.startDateTime.toLocalTime());
            //EndDate & Time
            String endDate = TimeKeeperDateUtils.formatToString(timeEntry.endDateTime.toLocalDate());
            String endTime = TimeKeeperDateUtils.formatToString(timeEntry.endDateTime.toLocalTime());
            // Duration
            String duration = TimeKeeperDateUtils.getDurationIntoHoursMinutesAndSecondAsString(timeEntry.startDateTime, timeEntry.endDateTime);

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

        }).collect(Collectors.toList());
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
