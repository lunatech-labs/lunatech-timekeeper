package fr.lunatech.timekeeper.resources.providers;

import fr.lunatech.timekeeper.services.OrganizationService;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class OrganizationInitializer {

    @Inject
    OrganizationService organizationService;

    @Inject
    Config config;

    private final static String CONFIG_PREFIX = "timekeeper.organizations.";

    // Example of properties read in config:
    // timekeeper.organizations."lunatech.nl"=Lunatech NL
    // timekeeper.organizations."lunatech.fr"=Lunatech FR
    void startup(@Observes StartupEvent event) {
        config.getPropertyNames().forEach(propertyName -> {
            if (propertyName.startsWith(CONFIG_PREFIX)) {
                final var organizationName = config.getValue(propertyName, String.class);
                final var organizationTokenName = propertyName.replace(CONFIG_PREFIX, "").replace("\"", "");
                organizationService.createIfTokenNameNotFound(new OrganizationRequest(organizationName, organizationTokenName));
            }
        });
    }
}
