package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSheetResponse {
    public Long id;

    public ProjectResponse project;

    public Long ownerId;

    private String timeUnit;

    public Boolean defaultIsBillable;

    public LocalDate expirationDate;

    public Integer maxDuration; // eg 21

    public String durationUnit; // DAYS

    public List<TimeEntryResponse> entries;

    // This functional helper should indicate if a TimeSheet is over budget in term of time or days
    // TODO it requires unit test and more work
    public Boolean isOverTimeOrBudget() {
        var isOverTime = LocalDate.now().isAfter(expirationDate);
        var isOverBudget = entries.size() > maxDuration;
        return isOverBudget || isOverTime;
    }

    public TimeSheetResponse(Long id, ProjectResponse project, UserResponse owner, String timeUnit, Boolean defaultIsBillable, LocalDate expirationDate, Integer maxDuration, String durationUnit, List<TimeEntryResponse> entries) {
        this.id = id;
        this.project = project;
        this.ownerId = owner.getId();
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
    }

    public static TimeSheetResponse bind(@NotNull TimeSheet sheet) {
        return new TimeSheetResponse(
                sheet.id,
                ProjectResponse.bind(sheet.project),
                UserResponse.bind(sheet.owner),
                sheet.timeUnit.name(),
                sheet.defaultIsBillable,
                sheet.expirationDate,
                sheet.maxDuration,
                sheet.durationUnit.name(),
                sheet.entries.stream().map(TimeSheetResponse.TimeEntryResponse::bind)
                        .collect(Collectors.toList())
        );
    }

    public static final class TimeEntryResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean billable;

        @NotNull
        private final String comment;

        @NotNull
        private final LocalDateTime startDateTime;

        @NotNull
        private final LocalDateTime endDateTime;

        public TimeEntryResponse(
                @NotNull Long id,
                @NotNull Boolean billable,
                @NotNull String comment,
                @NotNull LocalDateTime startDateTime,
                @NotNull LocalDateTime endDateTime
        ) {
            this.id = id;
            this.billable = billable;
            this.comment = comment;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public static TimeSheetResponse.TimeEntryResponse bind(@NotNull TimeEntry timeEntry) {
            return new TimeSheetResponse.TimeEntryResponse(
                    timeEntry.id,
                    timeEntry.billable,
                    timeEntry.comment,
                    timeEntry.startDateTime,
                    timeEntry.endDateTime
            );
        }

        public Long getId() {
            return id;
        }

        public Boolean getBillable() {
            return billable;
        }

        public String getComment() {
            return comment;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }
    }
}
