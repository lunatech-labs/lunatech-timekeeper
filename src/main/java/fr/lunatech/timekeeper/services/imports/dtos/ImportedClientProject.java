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

package fr.lunatech.timekeeper.services.imports.dtos;

import com.google.common.base.Objects;

public class ImportedClientProject {

    private String clientName;
    private String projectName;

    public static ImportedClientProject of(String clientName, String projectName){
        return new ImportedClientProject(clientName, projectName);
    }

    public ImportedClientProject() {
    }

    public ImportedClientProject(String clientName, String projectName) {
        this.clientName = clientName;
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "ImportedClientProject{" +
                "clientName='" + clientName + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportedClientProject)) return false;
        ImportedClientProject that = (ImportedClientProject) o;
        return Objects.equal(clientName, that.clientName) &&
                Objects.equal(projectName, that.projectName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientName, projectName);
    }
}
