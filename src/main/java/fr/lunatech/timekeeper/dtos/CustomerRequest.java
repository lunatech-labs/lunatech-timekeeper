package fr.lunatech.timekeeper.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class CustomerRequest {

    @NotBlank
    private final String name;
    @NotNull
    private final String description;

    @JsonbCreator
    public CustomerRequest(@NotBlank String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
