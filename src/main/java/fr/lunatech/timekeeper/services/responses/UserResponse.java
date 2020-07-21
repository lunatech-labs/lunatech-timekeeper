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

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.models.ProjectUser;
import fr.lunatech.timekeeper.models.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public final class UserResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotBlank
    @Email
    private final String email;

    @NotNull
    private final String picture;

    @NotEmpty
    private final List<Profile> profiles;

    @NotNull
    private final List<ProjectUserResponse> projects;

    public UserResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotNull String picture,
            @NotEmpty List<Profile> profiles,
            @NotNull List<ProjectUserResponse> projects
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.profiles = profiles;
        this.projects = projects;
    }

    public static UserResponse bind(@NotNull User user) {
        return new UserResponse(
                user.id,
                user.getFullName(),
                user.email,
                user.picture,
                user.profiles,
                user.projects
                        .stream()
                        .map(ProjectUserResponse::bind)
                        .collect(Collectors.toList())
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

    public List<Profile> getProfiles() {
        return profiles;
    }

    public List<ProjectUserResponse> getProjects() {
        return projects;
    }


    /* 🎁 ProjectUserResponse */
    public static final class ProjectUserResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean manager;

        @NotNull
        private final String name;

        @NotNull
        private final Boolean publicAccess;

        public ProjectUserResponse(
                @NotNull Long id,
                @NotNull Boolean manager,
                @NotNull String name,
                @NotNull Boolean publicAccess
        ) {
            this.id = id;
            this.manager = manager;
            this.name = name;
            this.publicAccess = publicAccess;
        }

        public static ProjectUserResponse bind(@NotNull ProjectUser projectUser) {
            return new ProjectUserResponse(
                    projectUser.project.id,
                    projectUser.manager,
                    projectUser.project.name,
                    projectUser.project.publicAccess
            );
        }

        public Long getId() {
            return id;
        }

        public Boolean isManager() {
            return manager;
        }

        public String getName() {
            return name;
        }

        public Boolean isPublicAccess() {
            return publicAccess;
        }
    }
}
