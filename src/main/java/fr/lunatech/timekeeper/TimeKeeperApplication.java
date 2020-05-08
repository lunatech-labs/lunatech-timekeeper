package fr.lunatech.timekeeper;


import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(title = "TimeKeeper API",
                description = "This API allows CRUD operations and interaction with TimeKeeper",
                version = "1.0",
                contact = @Contact(name = "TimeKeeper GitHub", url = "https://github.com/lunatech-labs/lunatech-timekeeper")),
        externalDocs = @ExternalDocumentation(url = "https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1609695253/Timekeeper", description = "Lunatech doc about TimeKeeper on Confluence")
)
public class TimeKeeperApplication extends Application {

}
