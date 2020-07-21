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

package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    private final String tokenName;

    @NotNull
    private final List<OrganizationProjectResponse> projects;

    @NotNull
    private final List<OrganizationUserResponse> users;

    public OrganizationResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull String tokenName,
            @NotNull List<OrganizationProjectResponse> projects,
            @NotNull List<OrganizationUserResponse> users
    ) {
        this.id = id;
        this.name = name;
        this.tokenName = tokenName;
        this.projects = projects;
        this.users = users;
    }

    public static OrganizationResponse bind(@NotNull Organization organization) {
        return new OrganizationResponse(
                organization.id,
                organization.name,
                organization.tokenName,
                organization.projects
                        .stream()
                        .map(OrganizationProjectResponse::bind)
                        .collect(Collectors.toList()),
                organization.users
                        .stream()
                        .map(OrganizationUserResponse::bind)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTokenName() {
        return tokenName;
    }

    public List<OrganizationProjectResponse> getProjects() {
        return projects;
    }

    public List<OrganizationUserResponse> getUsers() {
        return users;
    }


    /* 🎁 OrganizationProjectResponse */
    public static final class OrganizationProjectResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final String name;

        @NotNull
        private final Boolean publicAccess;

        @NotNull
        private final Integer userCount;

        private OrganizationProjectResponse(
                @NotNull Long id,
                @NotNull String name,
                @NotNull Boolean publicAccess,
                @NotNull Integer userCount
        ) {
            this.id = id;
            this.name = name;
            this.publicAccess = publicAccess;
            this.userCount = userCount;
        }

        public static OrganizationProjectResponse bind(@NotNull Project project) {
            return new OrganizationProjectResponse(
                    project.id,
                    project.name,
                    project.publicAccess,
                    project.users.size()
            );
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Boolean isPublicAccess() {
            return publicAccess;
        }

        public Integer getUserCount() {
            return userCount;
        }
    }


    /* 👤 OrganizationUserResponse */
    public static final class OrganizationUserResponse {

        @NotNull
        private final Long id;

        @NotBlank
        private final String name;

        @NotBlank
        @Email
        private final String email;

        @NotNull
        private final String picture;

        @NotNull
        private final Integer projectCount;

        @NotEmpty
        private final List<Profile> profiles;

        public OrganizationUserResponse(
                @NotNull Long id,
                @NotBlank String name,
                @NotBlank @Email String email,
                @NotNull String picture,
                @NotNull Integer projectCount,
                @NotEmpty List<Profile> profiles

        ) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.picture = picture;
            this.projectCount = projectCount;
            this.profiles = profiles;
        }

        public static OrganizationUserResponse bind(@NotNull User user) {
            return new OrganizationUserResponse(
                    user.id,
                    user.getFullName(),
                    user.email,
                    user.picture,
                    user.projects.size(),
                    user.profiles
            );
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPicture() {
            return picture;
        }

        public Integer getProjectCount() {
            return projectCount;
        }

        public List<Profile> getProfiles() {
            return profiles;
        }
    }
}
