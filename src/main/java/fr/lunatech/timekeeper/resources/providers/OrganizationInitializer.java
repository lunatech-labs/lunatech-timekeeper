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

package fr.lunatech.timekeeper.resources.providers;

import fr.lunatech.timekeeper.services.OrganizationService;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class OrganizationInitializer {
    private static Logger logger = LoggerFactory.getLogger(OrganizationInitializer.class);

    @Inject
    OrganizationService organizationService;

    @Inject
    Config config;

    private static final String CONFIG_PREFIX = "timekeeper.organizations.";

    // Example of properties read in config:
    // timekeeper.organizations."lunatech.nl"=Lunatech NL
    // timekeeper.organizations."lunatech.fr"=Lunatech FR
    void startup(@Observes StartupEvent event) {
        config.getPropertyNames().forEach(propertyName -> {
            if (propertyName.startsWith(CONFIG_PREFIX)) {
                final var organizationName = config.getValue(propertyName, String.class);
                final var organizationTokenName = propertyName.replace(CONFIG_PREFIX, "").replace("\"", "");
                final var organizationId = organizationService.createIfTokenNameNotFound(new OrganizationRequest(organizationName, organizationTokenName));
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Created organizationName=%s organizationId=%d", organizationName, organizationId));
                }
            }
        });
    }
}
