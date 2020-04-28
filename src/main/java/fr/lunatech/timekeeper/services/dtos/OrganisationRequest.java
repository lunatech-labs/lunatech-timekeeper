package fr.lunatech.timekeeper.services.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrganisationRequest {

    @NotBlank
    private final String name;
    @NotNull
    private final String tokenName;

    @JsonbCreator
    public OrganisationRequest(@NotBlank String name, @NotNull String tokenName) {
        this.name = name;
        this.tokenName = tokenName;
    }

    public String getName() {
        return name;
    }

    public String getTokenName() {
        return tokenName;
    }

    @Override
    public String toString() {
        return "OrganisationRequest{" +
                "name='" + name + '\'' +
                ", tokenName='" + tokenName + '\'' +
                '}';
    }
}
