package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.services.AuthenticationContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrganizationRequest {

    @NotBlank
    private final String name;

    @NotNull
    private final String tokenName;

    public OrganizationRequest(@NotBlank String name, @NotNull String tokenName) {
        this.name = name;
        this.tokenName = tokenName;
    }

    public Organization unbind(@NotNull Organization organization, @NotNull AuthenticationContext ctx) {
        organization.name = getName();
        organization.tokenName = getTokenName();
        return organization;
    }

    public Organization unbind(@NotNull AuthenticationContext ctx) {
        return unbind(new Organization(), ctx);
    }

    public String getName() {
        return name;
    }

    public String getTokenName() {
        return tokenName;
    }

    @Override
    public String toString() {
        return "OrganizationRequest{" +
                "name='" + name + '\'' +
                ", tokenName='" + tokenName + '\'' +
                '}';
    }
}
