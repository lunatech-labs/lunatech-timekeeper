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

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "timesheets")
public class TimeSheet extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    public Project project;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    public User owner;

    @Enumerated(EnumType.STRING)
    public TimeUnit timeUnit;

    @NotNull
    public Boolean defaultIsBillable;

    // 1---------------
    @Null
    public LocalDate expirationDate;

    // 2 ------------------
    @Null
    public Integer maxDuration; // eg 21

    @Enumerated(EnumType.STRING)
    @Null
    public TimeUnit durationUnit; // DAYS

    // -------------------------------
    @OneToMany(mappedBy = "timeSheet")
    public List<TimeEntry> entries;

    @NotNull
    public LocalDate startDate;

    public TimeSheet() {}

    public TimeSheet(@NotNull Project project,
                     User owner,
                     TimeUnit timeUnit,
                     Boolean defaultIsBillable,
                     @Null LocalDate expirationDate,
                     @Null Integer maxDuration,
                     @Null TimeUnit durationUnit,
                     List<TimeEntry> entries,
                     @NotNull LocalDate startDate) {
        this.project = project;
        this.owner = owner;
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "TimeSheet{" +
                ", id=" + id +
                ", project=" + project +
                ", owner=" + owner +
                ", timeUnit=" + timeUnit +
                ", defaultIsBillable=" + defaultIsBillable +
                ", expirationDate=" + expirationDate +
                ", maxDuration=" + maxDuration +
                ", durationUnit=" + durationUnit +
                ", entries=" + entries +
                ", startDate=" + startDate +
                '}';
    }
}
