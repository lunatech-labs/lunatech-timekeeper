/*
 * Copyright 2020 Lunatech Labs
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
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * An EventTemplate is an event, created manually by an administrator that would concern one
 * to many users. These events are “company events” like conference, hack-breakfast, training,
 * trip, all-staff-meeting...
 * @see https://lunatech.atlassian.net/wiki/spaces/T/pages/1948057763/Domain+model
 */
@Entity
@Table(name = "event_template")
public class EventTemplate extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(nullable = false, length = 100)
    public String name;

    @NotNull
    public String description;

    @ManyToOne(targetEntity = Organization.class)
    public Organization organization;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "eventTemplate")
    private Set<UserEvent> associatedEvents;

    @Override
    public String toString() {
        return "EventTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", organization=" + organization +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", associatedEvents=" + associatedEvents +
                '}';
    }
}
