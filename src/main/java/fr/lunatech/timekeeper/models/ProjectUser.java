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

package fr.lunatech.timekeeper.models;

import com.google.common.base.Objects;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "projects_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "user_id"})})
public class ProjectUser extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    public Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    public User user;

    @NotNull
    public Boolean manager;

    public ProjectUser() {
    }

    public ProjectUser(@NotNull Project project, @NotNull User user, @NotNull Boolean manager) {
        this.project = project;
        this.user = user;
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectUser)) return false;
        ProjectUser that = (ProjectUser) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(project, that.project) &&
                Objects.equal(user, that.user) &&
                Objects.equal(manager, that.manager);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, project, user, manager);
    }
}
