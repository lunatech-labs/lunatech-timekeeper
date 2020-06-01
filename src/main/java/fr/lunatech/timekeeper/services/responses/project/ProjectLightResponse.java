package fr.lunatech.timekeeper.services.responses.project;
import fr.lunatech.timekeeper.models.Project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import static java.util.Optional.ofNullable;

public final class ProjectLightResponse {

        @NotNull
        private final Long id;

        @NotBlank
        private final String name;

        @NotNull
        private final Boolean billable;

        @NotNull
        private final String description;

        @Null
        private final ProjectClientResponse client;

        @NotNull
        private final Boolean publicAccess;

        public ProjectLightResponse(
                @NotNull Long id,
                @NotBlank String name,
                @NotNull Boolean billable,
                @NotNull String description,
                @Null ProjectClientResponse client,
                @NotNull Boolean publicAccess
        ) {
            this.id = id;
            this.name = name;
            this.billable = billable;
            this.description = description;
            this.client = client;
            this.publicAccess = publicAccess;
        }
        public static ProjectLightResponse bind(@NotNull Project project) {
            return new ProjectLightResponse(
                    project.id,
                    project.name,
                    project.billable,
                    project.description,
                    ofNullable(project.client)
                            .map(ProjectClientResponse::bind)
                            .orElse(null),
                    project.publicAccess
            );
        }
    }