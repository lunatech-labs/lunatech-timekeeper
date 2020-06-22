package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventTemplateResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    @NotNull
    private final LocalDateTime startDateTime;

    @Null
    private final LocalDateTime endDateTime;

    @Null
    private final List<UserEventResponse> associatedUserEvents;


    public EventTemplateResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull String description,
            @NotNull LocalDateTime startDateTime,
            @Null LocalDateTime endDateTime,
            @Null List<UserEventResponse> associatedUserEvents
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.associatedUserEvents = associatedUserEvents;
    }


    public static EventTemplateResponse bind(@NotNull EventTemplate eventTemplate){
        List<UserEventResponse> associatedUserEvents = eventTemplate.associatedUserEvents.stream().map(UserEventResponse::bind).collect(Collectors.toList());
        return new EventTemplateResponse(eventTemplate.id,eventTemplate.name,eventTemplate.description,eventTemplate.startDateTime,eventTemplate.endDateTime,associatedUserEvents);
    }


    @Override
    public String toString() {
        return "EventTemplateResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", associatedUserEvents=" + associatedUserEvents +
                '}';
    }

    public static final class UserEventResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final String name;

        @NotNull
        private final String description;

        @NotNull
        @Enumerated(EnumType.STRING)
        private final EventType eventType;

        public UserEventResponse(
                @NotNull Long id,
                @NotNull String name,
                @NotNull String description,
                @NotNull EventType eventType) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.eventType = eventType;
        }

        public static UserEventResponse bind(@NotNull UserEvent userEvent){
            return new UserEventResponse(userEvent.id,userEvent.name,userEvent.description,userEvent.eventType);
        }


        public Long getId() { return id; }

        public String getName() { return name; }

        public String getDescription() { return description; }

        public EventType getEventType() { return eventType; }

        @Override
        public String toString() {
            return "UserEventResponse{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", eventType=" + eventType +
                    '}';
        }
    }

}
