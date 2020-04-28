package fr.lunatech.timekeeper.services.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OrganizationResponse {

    @NotNull
    private final Long id;
    @NotBlank
    private final String name;
    @NotNull
    private final String tokenName;
    @NotNull
    private final List<Long> projectsId;
    @NotNull
    private final List<Long> usersId;

    public OrganizationResponse(@NotNull Long id, @NotBlank String name, @NotNull String tokenName, @NotNull List<Long> projectsId, @NotNull List<Long> usersId) {
        this.id = id;
        this.name = name;
        this.tokenName = tokenName;
        this.projectsId = projectsId;
        this.usersId = usersId;
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

    public List<Long> getProjectsId() {
        return projectsId;
    }

    public List<Long> getUsersId() {
        return usersId;
    }
}
