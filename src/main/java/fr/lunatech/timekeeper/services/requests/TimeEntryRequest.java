package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Function;

public interface TimeEntryRequest {
    TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull Function<Long, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    );
}
