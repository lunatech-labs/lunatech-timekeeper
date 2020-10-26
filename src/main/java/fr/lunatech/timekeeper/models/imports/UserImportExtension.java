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

package fr.lunatech.timekeeper.models.imports;

import fr.lunatech.timekeeper.models.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users_import_extension", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class UserImportExtension extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    public User user;

    @Column(name = "user_email_from_import")
    @NotBlank
    public String userEmailFromImport;

    public UserImportExtension() {
    }

    public UserImportExtension(Long id, @NotNull User user, @NotBlank String userEmailFromImport) {
        this.id = id;
        this.user = user;
        this.userEmailFromImport = userEmailFromImport;
    }
}
