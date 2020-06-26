package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class EventTemplateRequest {

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime startDateTime;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime endDateTime;

    @NotNull
    private final List<UserEventRequest> attendees;

    public EventTemplateRequest(
            @NotBlank String name,
            @NotNull String description,
            @NotNull LocalDateTime startDateTime,
            @NotNull LocalDateTime endDateTime,
            @NotNull List<UserEventRequest> attendees
    ) {
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.attendees = attendees;
    }

    public EventTemplate unbind(
            @NotNull EventTemplate eventTemplate,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        eventTemplate.organization = ctx.getOrganization();
        eventTemplate.name = getName();
        eventTemplate.description = getDescription();
        eventTemplate.startDateTime = getStartDateTime();
        eventTemplate.endDateTime = getEndDateTime();
        eventTemplate.attendees = getAttendees()
                .stream()
                .map(userEventRequest -> userEventRequest.unbind(eventTemplate, findUser, ctx))
                .collect(Collectors.toSet());
        return eventTemplate;
    }

    public EventTemplate unbind(@NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
                                @NotNull AuthenticationContext ctx) {
        return unbind(new EventTemplate(), findUser, ctx);
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public List<UserEventRequest> getAttendees() {
        return attendees;
    }

    /* 👤 UserEventRequest */
    public static final class UserEventRequest {

        @NotNull
        private final Long userId;

        @JsonCreator
        public UserEventRequest( @NotNull Long userId) {
            this.userId = userId;
        }

        public UserEvent unbind(
                @NotNull EventTemplate eventTemplate,
                @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
                @NotNull AuthenticationContext ctx
        ) {
            final var userEvent = eventTemplate.getAttendees(getUserId()).orElse(new UserEvent());
            userEvent.name = eventTemplate.name;
            userEvent.description = eventTemplate.description;
            userEvent.eventType = EventType.COMPANY;
            userEvent.startDateTime = eventTemplate.startDateTime;
            userEvent.endDateTime = eventTemplate.endDateTime;
            userEvent.owner = findUser.apply(getUserId(), ctx)
                    .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown User. userId=%s", getUserId())));
            userEvent.eventTemplate = eventTemplate;
            return userEvent;
        }

        public Long getUserId() {
            return userId;
        }

        @Override
        public String toString() {
            return "UserEventRequest{" +
                    "userId=" + userId +
                    '}';
        }
    }

}
