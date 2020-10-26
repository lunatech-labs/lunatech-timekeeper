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

    @NotNull
    private final Long creatorId;

    public UserEventRequest(
            @NotNull Long userId,
            @NotNull Long creatorId
    ) {
        this.userId = userId;
        this.creatorId = creatorId;
    }

    @JsonCreator
    public UserEventRequest(
            @NotNull Long userId,
            @NotBlank String name,
            @NotNull String description,
            @NotNull LocalDateTime startDateTime,
            @NotNull LocalDateTime endDateTime,
            @NotNull Long creatorId
    ) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.creatorId = creatorId;
    }


    public UserEvent unbind(
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        final var userEvent = new UserEvent();
        final var owner = findUser.apply(getUserId(), ctx)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown Owner. userId=%s", getUserId())));
        final var creator = findUser.apply(getCreatorId(), ctx)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown Creator. userId=%s", getCreatorId())));
        userEvent.name = this.name;
        userEvent.description = this.description;
        userEvent.eventType = EventType.PERSONAL;
        userEvent.startDateTime = this.startDateTime;
        userEvent.endDateTime = this.endDateTime;
        userEvent.owner = owner;
        userEvent.creator = creator;
        userEvent.organization = creator.getOrganization();   // Clarify: whether creators or owners organisation to be stored ???
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
        userEvent.creator = eventTemplate.creator;
        userEvent.eventTemplate = eventTemplate;
        userEvent.organization = eventTemplate.creator.getOrganization();
        return userEvent;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    @Override
    public String toString() {
        return "UserEventRequest{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", creatorId=" + creatorId +
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
