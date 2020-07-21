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
