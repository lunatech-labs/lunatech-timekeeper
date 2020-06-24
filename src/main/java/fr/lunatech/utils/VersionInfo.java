package fr.lunatech.utils;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;

/**
 * Display application version number.
 * This is a small utility class that reads configuration properties from application.properties.
 * @see https://www.clever-cloud.com/doc/admin-console/environment-variables/
 * @see https://quarkus.io/guides/config
 * @author Nicolas Martignole
 */
@Singleton
public class VersionInfo {
    @ConfigProperty(name = "application.git.hash", defaultValue="develop")
    protected String gitHash;

    @ConfigProperty(name = "application.instance", defaultValue="DEV")
    protected String instanceName;

    public String getGitHash() {
        return gitHash;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
