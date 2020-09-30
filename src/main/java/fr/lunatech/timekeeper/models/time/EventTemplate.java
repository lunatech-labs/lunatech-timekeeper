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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * An EventTemplate is an event, created manually by an administrator that would concern one
 * to many users. These events are “company events” like conference, hack-breakfast, training,
 * trip, all-staff-meeting...
 *
 * @see <a href=https://lunatech.atlassian.net/wiki/spaces/T/pages/1948057763/Domain+model>Model Domain documentation</a>
 */
@Entity
@Table(name = "event_template", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})})
public class EventTemplate extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    public String name;

    @NotNull
    public String description;

    @ManyToOne(targetEntity = Organization.class)
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull
    public Organization organization;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    @NotNull
    public User creator;

    @Override
    public String toString() {
        return "EventTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", organization=" + organization +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }

}
