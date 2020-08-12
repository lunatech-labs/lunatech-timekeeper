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

package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserEventResponse {
    private Long id;
    private String name;
    private String description;
    private List<EventUserDayResponse> eventUserDaysResponse;
    private String startDateTime;
    private String endDateTime;
    private String date;
    private String eventType;
    private String duration;

    private UserEventResponse() {

    }

    public static UserEventResponse bind(UserEvent event) {
        UserEventResponse response = UserEventResponse.createFromUserEvent(event);

        if(event.startDateTime != null && event.endDateTime != null){
            response = createEventUserDayResponse(event, response);
        }
        return formatUserEventResponse(event, response);
    }

    /**
     * Factory method
     * @param event
     * @return a new UserEventResponse initialized
     */
    protected static UserEventResponse createFromUserEvent(UserEvent event) {
        UserEventResponse response = new UserEventResponse();
        response.id = event.id;
        response.name = event.name;
        response.description = event.description;
        return response;
    }

    protected static UserEventResponse formatUserEventResponse(UserEvent event, UserEventResponse response) {
        if (event.startDateTime != null) {
            response.startDateTime = TimeKeeperDateUtils.formatToString(event.startDateTime);
        }
        if (event.getDay() != null) {
            response.date = TimeKeeperDateUtils.formatToString(event.getDay());
        }
        if (event.endDateTime != null) {
            response.endDateTime = TimeKeeperDateUtils.formatToString(event.endDateTime);
        }
        if(event.eventType!=null){
            response.eventType = event.eventType.name();
        }
        if(event.startDateTime != null && event.endDateTime!=null) {
            response.duration = Duration.between(event.startDateTime, event.endDateTime).toString();
        }
        return response;
    }


    /**
     * Create UserEventResponse with an eventUserDayResponse
     * For the eventUserDayResponse, it uses a sequential ordered stream of dates. The returned stream starts from this date
     * (inclusive) and goes to {@code endExclusive} (exclusive) by an incremental step of 1 day. Because it goes to the exclusive end date,
     * this uses the endTimeDate + 1.
     * @param event
     * @param response
     * @return UserEventResponse
     */
    protected static UserEventResponse createEventUserDayResponse(UserEvent event, UserEventResponse response) {

        List<LocalDate> dates = event.startDateTime.toLocalDate().datesUntil(event.endDateTime.toLocalDate().plusDays(1))
                .collect(Collectors.toList());
        if(dates.size() > 1){
            response.eventUserDaysResponse = dates.stream()
                    .map(date -> {
                        if(date.isEqual(event.startDateTime.toLocalDate())){
                            return new EventUserDayResponse(
                                    event.name,
                                    event.description,
                                    TimeKeeperDateUtils.formatToString(date.atTime(
                                            event.startDateTime.getHour(),
                                            event.startDateTime.getMinute())),
                                    TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),
                                    TimeKeeperDateUtils.formatToString(date));
                        }else if(date.isEqual(event.endDateTime.toLocalDate())){
                            return new EventUserDayResponse(
                                    event.name,
                                    event.description,
                                    TimeKeeperDateUtils.formatToString(date.atTime(9, 0)),
                                    TimeKeeperDateUtils.formatToString(date.atTime(
                                            event.endDateTime.getHour(),
                                            event.endDateTime.getMinute())),
                                    TimeKeeperDateUtils.formatToString(date));
                        } else {
                            return new EventUserDayResponse(
                                    event.name,
                                    event.description,
                                    TimeKeeperDateUtils.formatToString(date.atTime(9, 0)),
                                    TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),
                                    TimeKeeperDateUtils.formatToString(date));
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            response.eventUserDaysResponse = dates.stream()
                    .map(date -> new EventUserDayResponse(
                            event.name,
                            event.description,
                            TimeKeeperDateUtils.formatToString(date.atTime(
                                    event.startDateTime.getHour(),
                                    event.startDateTime.getMinute())),
                            TimeKeeperDateUtils.formatToString(date.atTime(
                                    event.endDateTime.getHour(),
                                    event.endDateTime.getMinute())),
                            TimeKeeperDateUtils.formatToString(date)))
                    .collect(Collectors.toList());
        }
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<EventUserDayResponse> getEventUserDaysResponse() {
        return eventUserDaysResponse;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEventResponse)) return false;

        UserEventResponse that = (UserEventResponse) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getEventUserDaysResponse() != null ? !getEventUserDaysResponse().equals(that.getEventUserDaysResponse()) : that.getEventUserDaysResponse() != null)
            return false;
        if (getStartDateTime() != null ? !getStartDateTime().equals(that.getStartDateTime()) : that.getStartDateTime() != null)
            return false;
        if (getEndDateTime() != null ? !getEndDateTime().equals(that.getEndDateTime()) : that.getEndDateTime() != null)
            return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null) return false;
        if (getEventType() != null ? !getEventType().equals(that.getEventType()) : that.getEventType() != null)
            return false;
        return getDuration() != null ? getDuration().equals(that.getDuration()) : that.getDuration() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getEventUserDaysResponse() != null ? getEventUserDaysResponse().hashCode() : 0);
        result = 31 * result + (getStartDateTime() != null ? getStartDateTime().hashCode() : 0);
        result = 31 * result + (getEndDateTime() != null ? getEndDateTime().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getEventType() != null ? getEventType().hashCode() : 0);
        result = 31 * result + (getDuration() != null ? getDuration().hashCode() : 0);
        return result;
    }

    public static class EventUserDayResponse {
        private String name;
        private String description;
        private String startDateTime;
        private String endDateTime;
        private String date;

        public EventUserDayResponse(String name, String description, String startDateTime, String endDateTime, String date) {
            this.name = name;
            this.description = description;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.date = date;
        }

        public String getName() {
            return name;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EventUserDayResponse)) return false;

            EventUserDayResponse that = (EventUserDayResponse) o;

            if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
            if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
                return false;
            if (getStartDateTime() != null ? !getStartDateTime().equals(that.getStartDateTime()) : that.getStartDateTime() != null)
                return false;
            if (getEndDateTime() != null ? !getEndDateTime().equals(that.getEndDateTime()) : that.getEndDateTime() != null)
                return false;
            return getDate() != null ? getDate().equals(that.getDate()) : that.getDate() == null;
        }

        @Override
        public int hashCode() {
            int result = getName() != null ? getName().hashCode() : 0;
            result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
            result = 31 * result + (getStartDateTime() != null ? getStartDateTime().hashCode() : 0);
            result = 31 * result + (getEndDateTime() != null ? getEndDateTime().hashCode() : 0);
            result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
            return result;
        }
    }

    @Override
    public String toString() {
        return "UserEventResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventUserDaysResponse=" + eventUserDaysResponse +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", date='" + date + '\'' +
                ", eventType='" + eventType + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
