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

package fr.lunatech.timekeeper.importcsv;
import com.google.common.base.Objects;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", skipFirstLine = true)
public class ImportedTimeEntry {

    @DataField(name="User", pos = 1)
    private String user;
    @DataField(name="Email", pos = 2)
    private String email;
    @DataField(name="Client", pos = 3)
    private String client;
    @DataField(name="Project", pos = 4)
    private String project;
    @DataField(name="Task", pos = 5)
    private String task;
    @DataField(name="Description", pos = 6)
    private String description;
    @DataField(name="Billable", pos = 7)
    private String billable;
    @DataField(name="Start date", pos = 8)
    private String startDate;
    @DataField(name="Start time", pos = 9)
    private String startTime;
    @DataField(name="End date", pos = 10)
    private String endDate;
    @DataField(name="End time", pos = 11)
    private String endTime;
    @DataField(name="Duration", pos = 12)
    private String duration;
    @DataField(name="Tags", pos = 13)
    private String tags;
    @DataField(name="Amount (EUR)", pos = 14)
    private String amount;

    //User,Email,Client,Project,Task,Description,Billable,Start date,Start time,End date,End time,Duration,Tags,Amount (EUR)

//    @DataField(pos = 9, pattern = "dd-MM-yyyy")
//    private Date orderDate;


    public ImportedTimeEntry() {
    }

    public ImportedTimeEntry(String user, String email, String client, String project, String task, String description, String billable, String startDate, String startTime, String endDate, String endTime, String duration, String tags, String amount) {
        this.user = user;
        this.email = email;
        this.client = client;
        this.project = project;
        this.task = task;
        this.description = description;
        this.billable = billable;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.duration = duration;
        this.tags = tags;
        this.amount = amount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBillable() {
        return billable;
    }

    public void setBillable(String billable) {
        this.billable = billable;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ImportedTimeEntry{" +
                "user='" + user + '\'' +
                ", email='" + email + '\'' +
                ", client='" + client + '\'' +
                ", project='" + project + '\'' +
                ", task='" + task + '\'' +
                ", description='" + description + '\'' +
                ", billable='" + billable + '\'' +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endDate='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration='" + duration + '\'' +
                ", tags='" + tags + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportedTimeEntry)) return false;
        ImportedTimeEntry that = (ImportedTimeEntry) o;
        return Objects.equal(getUser(), that.getUser()) &&
                Objects.equal(getEmail(), that.getEmail()) &&
                Objects.equal(getClient(), that.getClient()) &&
                Objects.equal(getProject(), that.getProject()) &&
                Objects.equal(getTask(), that.getTask()) &&
                Objects.equal(getDescription(), that.getDescription()) &&
                Objects.equal(getBillable(), that.getBillable()) &&
                Objects.equal(getStartDate(), that.getStartDate()) &&
                Objects.equal(getStartTime(), that.getStartTime()) &&
                Objects.equal(getEndDate(), that.getEndDate()) &&
                Objects.equal(getEndTime(), that.getEndTime()) &&
                Objects.equal(getDuration(), that.getDuration()) &&
                Objects.equal(getTags(), that.getTags()) &&
                Objects.equal(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUser(), getEmail(), getClient(), getProject(), getTask(), getDescription(), getBillable(), getStartDate(), getStartTime(), getEndDate(), getEndTime(), getDuration(), getTags(), getAmount());
    }
}
