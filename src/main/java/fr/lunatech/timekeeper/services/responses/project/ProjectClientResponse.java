package fr.lunatech.timekeeper.services.responses.project;

import fr.lunatech.timekeeper.models.Client;

import javax.validation.constraints.NotNull;

public final class ProjectClientResponse {

    @NotNull
    private final Long id;

    @NotNull
    private final String name;

    public ProjectClientResponse(
            @NotNull Long id,
            @NotNull String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static ProjectClientResponse bind(@NotNull Client client) {
        return new ProjectClientResponse(
                client.id,
                client.name
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
