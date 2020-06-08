package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.services.AuthenticationContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class ClientRequest {

    @NotBlank
    private final String name;

    @NotNull
    private final String description;

    public ClientRequest(@NotBlank String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public Client unbind(@NotNull Client client, @NotNull AuthenticationContext ctx) {
        client.organization = ctx.getOrganization();
        client.name = getName();
        client.description = getDescription();
        return client;
    }

    public Client unbind(@NotNull AuthenticationContext ctx) {
        return unbind(new Client(), ctx);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
