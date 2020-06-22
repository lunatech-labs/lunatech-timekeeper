package fr.lunatech.timekeeper;


import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.*;
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
                implicit = @OAuthFlow(authorizationUrl = "https://acceptance.api.timekeeper.lunatech.fr/auth/realms/Timekeeper/protocol/openid-connect/auth")
        )
)

public class TimeKeeperApplication extends Application {

}
