/*
 * Copyright 2020 Lunatech Labs
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
