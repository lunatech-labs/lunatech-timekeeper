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

    /**
     * bind UserEvent to UserEventResponse with an eventUserDayResponse
     * For the eventUserDayResponse, it uses a sequential ordered stream of dates. The returned stream starts from this date
     * (inclusive) and goes to {@code endExclusive} (exclusive) by an incremental step of 1 day. Because it goes to the exclusive end date,
     * this uses the endTimeDate + 1.
     * @param event
     * @return UserEventResponse
     */
    public static UserEventResponse bind(UserEvent event) {
        UserEventResponse response = new UserEventResponse();
        response.id = event.id;
        response.name = event.name;
        response.description = event.description;

        if(event.startDateTime != null && event.endDateTime != null){
            List<LocalDate> dates = event.startDateTime.toLocalDate().datesUntil(event.endDateTime.toLocalDate().plusDays(1))
                    .collect(Collectors.toList());
            if(dates.size() > 1){
                response.eventUserDaysResponse = dates.stream()
                        .map(date -> {
                            if(date.isEqual(event.startDateTime.toLocalDate())){
                                return new EventUserDayResponse(event.name,event.description,TimeKeeperDateUtils.formatToString(date.atTime(event.startDateTime.getHour(), event.startDateTime.getMinute())), TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),TimeKeeperDateUtils.formatToString(date));
                            }else if(date.isEqual(event.endDateTime.toLocalDate())){
                                return new EventUserDayResponse(event.name,event.description,TimeKeeperDateUtils.formatToString(date.atTime(9, 0)), TimeKeeperDateUtils.formatToString(date.atTime(event.endDateTime.getHour(), event.endDateTime.getMinute())),TimeKeeperDateUtils.formatToString(date));
                            } else {
                                return new EventUserDayResponse(event.name,event.description,TimeKeeperDateUtils.formatToString(date.atTime(9, 0)), TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),TimeKeeperDateUtils.formatToString(date));
                            }
                        })
                        .collect(Collectors.toList());
            } else {
                response.eventUserDaysResponse = dates.stream()
                        .map(date -> new EventUserDayResponse(event.name,event.description,TimeKeeperDateUtils.formatToString(date.atTime(event.startDateTime.getHour(), event.startDateTime.getMinute())), TimeKeeperDateUtils.formatToString(date.atTime(event.endDateTime.getHour(), event.endDateTime.getMinute())),TimeKeeperDateUtils.formatToString(date)))
                        .collect(Collectors.toList());
            }
        }
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
            response.eventType = event.eventType.name();
        }
        if(event.startDateTime != null && event.endDateTime!=null) {
            response.duration = Duration.between(event.startDateTime, event.endDateTime).toString();
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
