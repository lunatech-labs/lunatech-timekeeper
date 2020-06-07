package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
@JsonIgnoreProperties(ignoreUnknown = true)
public interface TimeEntryRequest {
    TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    );
}
