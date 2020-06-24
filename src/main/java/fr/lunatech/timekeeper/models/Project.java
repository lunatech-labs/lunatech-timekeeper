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

package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Entity
@Table(name = "projects", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})})
public class Project extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull
    public Organization organization;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @Null
    public Client client;

    @NotBlank
    public String name;

    @NotNull
    public Boolean billable;

    @NotNull
    public String description;

    @NotNull
    public Boolean publicAccess;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @NotNull
    public List<ProjectUser> users;

    public Optional<ProjectUser> getUser(Long userId) {
        return ofNullable(users)
                .flatMap(projectUsers -> projectUsers
                        .stream()
                        .filter(projectUser -> Objects.equals(projectUser.user.id, userId))
                        .findFirst()
                );
    }

    public Boolean getBillable() {
        return billable;
    }

    @Override
    public String toString() {
        return "Project{" +
                " id=" + id +
                ", name='" + name + '\'' +
                ", billable=" + billable +
                ", description='" + description + '\'' +
                ", publicAccess=" + publicAccess +
                ", organization=" + organization +
                ", client=" + client +
                ", users=" + users +
                '}';
    }
}