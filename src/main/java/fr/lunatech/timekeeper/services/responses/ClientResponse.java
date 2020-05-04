package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public final class ClientResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    @NotNull
    private final List<ProjectClientResponse> projects;

    public ClientResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull String description,
            @NotNull List<ProjectClientResponse> projects
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projects = projects;
    }

    public static ClientResponse bind(Client client) {
        return new ClientResponse(
                client.id,
                client.name,
                client.description,
                client.projects
                        .stream()
                        .map(ProjectClientResponse::bind)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ProjectClientResponse> getProjects() {
        return projects;
    }


    /* 🎁 ProjectClientResponse */
    public static final class ProjectClientResponse {

        @NotNull
        private final Long id;

        @NotNull
        private final String name;

        @NotNull
        private final Integer userCount;

        private ProjectClientResponse(
                @NotNull Long id,
                @NotNull String name,
                @NotNull Integer userCount
        ) {
            this.id = id;
            this.name = name;
            this.userCount = userCount;
        }

        public static ProjectClientResponse bind(@NotNull Project project) {
            return new ProjectClientResponse(
                    project.id,
                    project.name,
                    project.users.size()
            );
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getUserCount() {
            return userCount;
        }
    }
}
