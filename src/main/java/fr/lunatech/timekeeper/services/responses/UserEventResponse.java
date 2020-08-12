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
import net.bytebuddy.implementation.bytecode.Throw;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
        return UserEventResponse.createFromUserEvent(event);
    }

    /**
     * Factory method
     * @param event
     * @return a new UserEventResponse initialized
     */
    protected static UserEventResponse createFromUserEvent(UserEvent event) {
        var userEventResponse = new UserEventResponse();
        userEventResponse.id = event.id;
        userEventResponse.name = event.name;
        userEventResponse.description = event.description;
        if(event.startDateTime != null && event.endDateTime != null){
            if(event.startDateTime.isAfter(event.endDateTime)){
                throw new IllegalArgumentException("StartDateTime must be before endDateTime");
            }
            if(event.startDateTime.isEqual(event.endDateTime)){
                throw new IllegalArgumentException("StartDateTime and EndDateTime must be different");
            }
            userEventResponse.eventUserDaysResponse = createEventUserDayResponseList(event);
            userEventResponse.duration = Duration.between(event.startDateTime, event.endDateTime).toString();
        }
        if (event.startDateTime != null) {
            userEventResponse.startDateTime = TimeKeeperDateUtils.formatToString(event.startDateTime);
        }
        if (event.endDateTime != null) {
            userEventResponse.endDateTime = TimeKeeperDateUtils.formatToString(event.endDateTime);
        }
        if (event.getDay() != null) {
            userEventResponse.date = TimeKeeperDateUtils.formatToString(event.getDay());
        }
        if(event.eventType != null){
            userEventResponse.eventType = event.eventType.name();
        }
        return userEventResponse;
    }



    /**
     *  Create a List of UserEventResponse with an event
     *  For the event, it uses a sequential ordered stream of dates. The returned stream starts from this date
     *  (inclusive) and goes to {@code endExclusive} (exclusive) by an incremental step of 1 day. Because it goes to the exclusive end date,
     *  this uses the endTimeDate + 1.
     * @param event
     * @return List<EventUserDayResponse>
     */
    protected static List<EventUserDayResponse> createEventUserDayResponseList(UserEvent event) {
        List<LocalDate> dates = event.startDateTime.toLocalDate().datesUntil(event.endDateTime.toLocalDate().plusDays(1))
                .collect(Collectors.toList());
            return dates.stream()
                    .map(date -> createEventUserDayResponse(event, date, dates.size()))
                    .collect(Collectors.toList());
    }

    /**
     * Create One day of EventUserDayResponse with the startDateTime and EndDateTime of the event.
     * @param event
     * @param date
     * @return EventUserDayResponse
     */
    private static EventUserDayResponse createEventUserOneDayResponse(UserEvent event, LocalDate date){
            return new EventUserDayResponse(
                    event.name,
                    event.description,
                    TimeKeeperDateUtils.formatToString(date.atTime(
                            event.startDateTime.getHour(),
                            event.startDateTime.getMinute())),
                    TimeKeeperDateUtils.formatToString(date.atTime(
                            event.endDateTime.getHour(),
                            event.endDateTime.getMinute())),
                    TimeKeeperDateUtils.formatToString(date)
            );
    }

    /**
     * Create an EventUserDayResponse from the startDateTime to 5PM if the date is the first day of the Event,
     * from 9AM to endDateTime if the date is the last day of the Event,
     * from 9AM to 5PM if the date is a day between the first day and the last day of the Event.
     * @param event
     * @param date
     * @return EventUserDayResponse
     */
    private static EventUserDayResponse createEventUserDayResponse(UserEvent event, LocalDate date, int datesSize) {
        if(datesSize <= 1) {
            return createEventUserOneDayResponse(event, date);
        } else {
            if (date.isEqual(event.startDateTime.toLocalDate())) {
                return createUserEventFirstDayResponse(event, date);
            } else if (date.isEqual(event.endDateTime.toLocalDate())) {
                return createUserEventLastDayResponse(event, date);
            } else {
                return createUserEventMiddleDayResponse(event, date);
            }
        }
    }

    private static EventUserDayResponse createUserEventMiddleDayResponse(UserEvent event, LocalDate date) {
        return new EventUserDayResponse(
                event.name,
                event.description,
                TimeKeeperDateUtils.formatToString(date.atTime(9, 0)),
                TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),
                TimeKeeperDateUtils.formatToString(date));
    }

    private static EventUserDayResponse createUserEventLastDayResponse(UserEvent event, LocalDate date) {
        return new EventUserDayResponse(
                event.name,
                event.description,
                TimeKeeperDateUtils.formatToString(date.atTime(9, 0)),
                TimeKeeperDateUtils.formatToString(date.atTime(
                        event.endDateTime.getHour(),
                        event.endDateTime.getMinute())),
                TimeKeeperDateUtils.formatToString(date));
    }

    private static EventUserDayResponse createUserEventFirstDayResponse(UserEvent event, LocalDate date) {
        return new EventUserDayResponse(
                event.name,
                event.description,
                TimeKeeperDateUtils.formatToString(date.atTime(
                        event.startDateTime.getHour(),
                        event.startDateTime.getMinute())),
                TimeKeeperDateUtils.formatToString(date.atTime(17, 0)),
                TimeKeeperDateUtils.formatToString(date));
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
        if (!(o instanceof UserEventResponse)) return false;

        UserEventResponse that = (UserEventResponse) o;

        return this.id.equals(that.id) && this.name.equals(that.name) && this.description.equals(that.description) &&
                (Objects.equals(this.eventUserDaysResponse, that.eventUserDaysResponse)) &&
                (Objects.equals(this.startDateTime, that.startDateTime)) &&
                (Objects.equals(this.endDateTime, that.endDateTime)) &&
                (Objects.equals(this.date, that.date)) &&
                (Objects.equals(this.eventType, that.eventType)) &&
                (Objects.equals(this.duration, that.duration));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, eventUserDaysResponse, startDateTime, endDateTime, date, eventType, duration);
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
            if (!(o instanceof EventUserDayResponse)) return false;

            EventUserDayResponse that = (EventUserDayResponse) o;

            return this.name.equals(that.name) && this.description.equals(that.description) &&
                    (Objects.equals(this.startDateTime, that.startDateTime)) &&
                    (Objects.equals(this.endDateTime, that.endDateTime)) &&
                    (Objects.equals(this.date, that.date));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, startDateTime, endDateTime, date);
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
