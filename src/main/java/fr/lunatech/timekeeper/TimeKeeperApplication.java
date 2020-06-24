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

package fr.lunatech.timekeeper;


import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(title = "TimeKeeper API",
                description = "This API allows CRUD operations and interaction with TimeKeeper",
                version = "1.0",
                contact = @Contact(name = "TimeKeeper GitHub", url = "https://github.com/lunatech-labs/lunatech-timekeeper"),
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "DEV Server"
                ),
                @Server(
                        url = "https://acceptance.api.timekeeper.lunatech.fr",
                        description = "ACCEPTANCE Server"
                )
        }
        , security = {
        @SecurityRequirement( name = "dev_timekeeperOAuth2" ,scopes = { "profile"}),
        @SecurityRequirement( name = "acceptance_timekeeperOAuth2" ,scopes = { "profile"})
}
        , externalDocs = @ExternalDocumentation(url = "https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1609695253/Timekeeper", description = "Lunatech doc about TimeKeeper on Confluence")

)
// Quarkus Issue pending with redirect and OAuth2 here https://github.com/quarkusio/quarkus/issues/4766
// To fix this issue I extracted the swagger oauth2-redirect.html file and saved it in the timekeeper folder
@SecurityScheme(
        securitySchemeName = "dev_timekeeperOAuth2",
        type = SecuritySchemeType.OAUTH2,
        description = "authentication for OAuth2 access",
        flows = @OAuthFlows(
                implicit = @OAuthFlow(authorizationUrl = "http://localhost:8082/auth/realms/Timekeeper/protocol/openid-connect/auth")
        )
)
@SecurityScheme(
        securitySchemeName = "acceptance_timekeeperOAuth2",
        type = SecuritySchemeType.OAUTH2,
        description = "authentication for OAuth2 access",
        flows = @OAuthFlows(
                implicit = @OAuthFlow(authorizationUrl = "https://acceptance.keycloak.timekeeper.lunatech.fr/auth/realms/Timekeeper/protocol/openid-connect/auth")
        )
)

public class TimeKeeperApplication extends Application {

}
