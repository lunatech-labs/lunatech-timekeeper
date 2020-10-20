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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull
    public Organization organization;

    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    @NotBlank
    @Email
    public String email;

    @NotNull
    public String picture;

    @Column
    @Convert(converter = Profile.ListConverter.class)
    @NotEmpty
    public List<Profile> profiles;

    @OneToMany(mappedBy = "user")
    @NotNull
    public List<ProjectUser> projects;

    public static User createUserForImport(String email, String userName, Organization organization){
        var newUser = new User();
        newUser.email = email;
        if (userName.contains(" ")) {
            newUser.firstName = userName.substring(0, userName.indexOf(" "));
            newUser.lastName = userName.substring(userName.indexOf(" ") + 1);
        } else {
            newUser.lastName = userName;
        }
        newUser.organization = organization;
        newUser.profiles = new ArrayList<>(1);
        newUser.profiles.add(Profile.USER);
        return newUser;
    }

    public User() {

    }

    public User(Long id, String firstName, String lastName, String email, String picture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
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

    public List<Profile> getProfiles() {
        return profiles;
    }

    public List<ProjectUser> getProjects() {
        return projects;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", organization=" + organization +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", profiles=" + profiles +
                ", projects=" + projects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equal(getId(), user.getId()) &&
                Objects.equal(getOrganization(), user.getOrganization()) &&
                Objects.equal(getFirstName(), user.getFirstName()) &&
                Objects.equal(getLastName(), user.getLastName()) &&
                Objects.equal(getEmail(), user.getEmail()) &&
                Objects.equal(getPicture(), user.getPicture()) &&
                Objects.equal(getProfiles(), user.getProfiles()) &&
                Objects.equal(getProjects(), user.getProjects());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getOrganization(), getFirstName(), getLastName(), getEmail(), getPicture(), getProfiles(), getProjects());
    }
}
