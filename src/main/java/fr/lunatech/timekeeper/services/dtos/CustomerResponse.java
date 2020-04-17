package fr.lunatech.timekeeper.services.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class CustomerResponse {

    @NotNull
    private final Long id;
    @NotBlank
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final List<Long> projectsId;

    public CustomerResponse(@NotNull Long id, @NotBlank String name, @NotNull String description, @NotNull List<Long> projectsId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projectsId = projectsId;
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

    public List<Long> getProjectId() {
        return projectsId;
    }
}
