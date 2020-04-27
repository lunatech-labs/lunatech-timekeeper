package fr.lunatech.timekeeper.services.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Optional;

public final class ProjectRequest {

    @NotBlank
    private final String name;
    @NotNull
    private final Boolean billable;
    @NotNull
    private final String description;
    @Null
    private final Long clientId;
    @NotNull
    private final Boolean isPublic;

    @JsonbCreator
    public ProjectRequest(@NotBlank String name, @NotNull Boolean billable, @NotNull String description, @Null Long clientId, Boolean isPublic) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientId = clientId;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public Boolean isBillable() {
        return billable;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Long> getClientId() {
        return Optional.of(clientId);
    }

    public Boolean getPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return "ProjectRequest{" +
                "name='" + name + '\'' +
                ", billable=" + billable +
                ", description='" + description + '\'' +
                ", clientId=" + clientId +
                ", isPublic=" + isPublic +
                '}';
    }
}
