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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
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
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime startDateTime;

    @Null
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime endDateTime;

    @Null
    private final List<EventTemplateResponse.Attendee> attendees;

    public EventTemplateResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull String description,
            @NotNull LocalDateTime startDateTime,
            @Null LocalDateTime endDateTime,
            @Null List<Attendee> attendees
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.attendees = attendees;
    }

    public static EventTemplateResponse bind(@NotNull EventTemplate eventTemplate, List<User> users) {
        var userEventResponses = users.stream().map(Attendee::bind).collect(Collectors.toList());
        return new EventTemplateResponse(
                eventTemplate.id,
                eventTemplate.name,
                eventTemplate.description,
                eventTemplate.startDateTime,
                eventTemplate.endDateTime,
                userEventResponses
        );
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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public List<Attendee> getAttendees() {
        return Collections.unmodifiableList(attendees);
    }

    @JsonIgnore
    public Long getNumberOfHours() {
        if (startDateTime == null) return 0L;
        if (startDateTime.isAfter(endDateTime)) return 0L;
        return Duration.between(startDateTime, endDateTime).toHours();
    }

    @Override
    public String toString() {
        return "EventTemplateResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", attendees=" + attendees +
                '}';
    }

    public static final class Attendee {
        @NotNull
        private final Long userId;
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String picture;

        public Attendee(Long id, String firstName, String lastName, String email, String picture) {
            this.userId = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.picture = picture;
        }

        public static Attendee bind(@NotNull User user) {
            return new Attendee(user.id, user.firstName, user.lastName, user.email, user.picture);
        }

        public Long getUserId() {
            return userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPicture() {
            return picture;
        }

        @Override
        public String toString() {
            return "Attendee{" +
                    "userId=" + userId +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", picture='" + picture + '\'' +
                    '}';
        }
    }
}
