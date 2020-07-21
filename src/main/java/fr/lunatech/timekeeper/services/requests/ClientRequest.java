/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
