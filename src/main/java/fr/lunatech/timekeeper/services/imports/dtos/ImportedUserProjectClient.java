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

public class ImportedUserProjectClient {

    private String userEmail;
    private String userName;
    private String projectName;
    private String clientName;

    public static ImportedUserProjectClient of(String userEmail, String userName, String projectName, String clientName){
        return new ImportedUserProjectClient(userEmail, userName, projectName, clientName);
    }

    public ImportedUserProjectClient() {
    }

    public ImportedUserProjectClient(String userEmail, String userName, String projectName, String clientName) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.projectName = projectName;
        this.clientName = clientName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "ImportedUserProjectClient{" +
                "userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", clientName='" + clientName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportedUserProjectClient)) return false;
        ImportedUserProjectClient that = (ImportedUserProjectClient) o;
        return Objects.equal(getUserEmail(), that.getUserEmail()) &&
                Objects.equal(getUserName(), that.getUserName()) &&
                Objects.equal(getProjectName(), that.getProjectName()) &&
                Objects.equal(getClientName(), that.getClientName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserEmail(), getUserName(), getProjectName(), getClientName());
    }
}
