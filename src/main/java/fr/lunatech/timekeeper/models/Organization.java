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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "organizations", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Organization extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String name;

    @NotBlank
    public String tokenName;

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<User> users;

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<Project> projects;

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<Project> clients;

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tokenName='" + tokenName + '\'' +
                ", users=" + users +
                ", projects=" + projects +
                ", clients=" + clients +
                '}';
    }
}
