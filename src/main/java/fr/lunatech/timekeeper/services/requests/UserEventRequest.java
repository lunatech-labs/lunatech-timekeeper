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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;

/* 👤 UserEventRequest */
public class UserEventRequest {

    @NotNull
    private final Long userId;
    @NotBlank
    private String name;

    private String description;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime startDateTime;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private EventType eventType;


    public UserEventRequest(
            @NotNull Long userId
    ) {
        this.userId = userId;
    }

    @JsonCreator
    public UserEventRequest(
            @NotNull Long userId,
            @NotBlank String name,
            @NotNull String description,
            @NotNull LocalDateTime startDateTime,
            @NotNull LocalDateTime endDateTime
    ) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }


    public UserEvent unbind(
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        final var userEvent = new UserEvent();
        userEvent.name = this.name;
        userEvent.description = this.description;
        userEvent.eventType = EventType.PERSONAL;//TODO parameter
        userEvent.startDateTime = this.startDateTime;
        userEvent.endDateTime = this.endDateTime;
        userEvent.owner = findUser.apply(getUserId(), ctx)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown User. userId=%s", getUserId())));
        return userEvent;
    }

    /**
     * Company event behavior
     *
     * @param eventTemplate
     * @param findUser
     * @param ctx
     * @return
     */
    public UserEvent unbind(
            @NotNull EventTemplate eventTemplate,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        final var userEvent = new UserEvent();
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
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

    public EventType getEventType() {
        return eventType;
    }
}
