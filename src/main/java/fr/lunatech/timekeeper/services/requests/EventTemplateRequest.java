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

package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public final class EventTemplateRequest {

    @NotBlank
    private final String name;

    private final String description;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime startDateTime;

    @NotNull
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_TIME_PATTERN)
    private final LocalDateTime endDateTime;

    @NotNull
    private final List<UserEventRequest> attendees;

    @JsonCreator
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
            @NotNull AuthenticationContext ctx
    ) {
        eventTemplate.organization = ctx.getOrganization();
        eventTemplate.name = getName();
        eventTemplate.description = getDescription();
        eventTemplate.startDateTime = getStartDateTime();
        eventTemplate.endDateTime = getEndDateTime();
        return eventTemplate;
    }

    public EventTemplate unbind(@NotNull AuthenticationContext ctx) {
        return unbind(new EventTemplate(), ctx);
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

    @Override
    public String toString() {
        return "EventTemplateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", attendees=" + attendees +
                '}';
    }
}
