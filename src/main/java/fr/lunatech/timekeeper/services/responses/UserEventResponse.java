package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import java.time.Duration;

public class UserEventResponse {
    private Long id;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private String date;
    private String eventType;
    private String duration;

    private UserEventResponse() {

    }

    public static UserEventResponse bind(UserEvent event) {
        UserEventResponse response = new UserEventResponse();
        response.id = event.id;
        response.description = event.description;

        if (event.startDateTime != null) {
            response.startDateTime = TimeKeeperDateUtils.formatToString(event.startDateTime);
        }
        if (event.getDay() != null) {
            response.date = TimeKeeperDateUtils.formatToString(event.getDay());
        }
        if (event.endDateTime != null) {
            response.endDateTime = TimeKeeperDateUtils.formatToString(event.endDateTime);
        }
        if (event.startDateTime != null) {
            response.endDateTime = TimeKeeperDateUtils.formatToString(event.endDateTime);
        }
        if(event.eventType!=null){
            response.eventType = event.eventType;
        }
        if(event.startDateTime != null && event.endDateTime!=null) {
            response.duration = Duration.between(event.startDateTime, event.endDateTime).toString();
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public String getDate() {
        return date;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDuration() {
        if(startDateTime==null || endDateTime==null){
            return "";
        }
       return duration;
    }

    @Override
    public String toString() {
        return "UserEventResponse{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", date='" + date + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
