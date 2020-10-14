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

package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * An Event is something that "block" a cell in your TimeSheet, so that one cannot enters TimeEntry for this
 * slot.
 * Kind of Event :
 * - vacations
 * - company event like HackBreakfast
 */
@Entity
@Table(name = "user_events")
public class UserEvent extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    public String name;

    @NotNull
    public String description;

    @Enumerated(EnumType.STRING)
    public EventType eventType;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @NotNull
    public User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventtemplate_id")
    public EventTemplate eventTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    @NotNull
    public User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    public Organization organization;

    public UserEvent() {

    }

    public UserEvent(Long id,
                     LocalDateTime startDateTime,
                     LocalDateTime endDateTime,
                     String name,
                     String description,
                     EventType type,
                     User user,
                     User creator,
                     Organization organization
    ) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.name = name;
        this.description = description;
        this.eventType = type;
        this.owner = user;
        this.creator = creator;
        this.organization = organization;
    }

    @Null
    public LocalDate getDay() {
        if (startDateTime == null) {
            return null;
        }
        return startDateTime.toLocalDate();
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventType=" + eventType +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", owner=" + owner.getFullName() +
                ", organization=" + organization.name +
                '}';
    }
}
