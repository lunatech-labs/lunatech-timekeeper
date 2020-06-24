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

package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateFormat;
import fr.lunatech.timekeeper.timeutils.TimeUnit;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;


public class TimeSheetRequest {

    @Enumerated(EnumType.STRING)
    public TimeUnit timeUnit;

    public Boolean defaultIsBillable;

    // 1---------------
    @Null
    @JsonFormat(pattern = TimeKeeperDateFormat.DEFAULT_DATE_PATTERN)
    public LocalDate expirationDate;

    // 2 ------------------
    @Null
    public Integer maxDuration; // eg 21

    @Null
    public TimeUnit durationUnit; // DAYS

    public TimeSheetRequest( TimeUnit timeUnit, Boolean defaultIsBillable, @Null LocalDate expirationDate, @Null Integer maxDuration, @Null TimeUnit durationUnit) {
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
    }

    public TimeSheet unbind(@NotNull TimeSheet timeSheet){
        timeSheet.timeUnit = getTimeUnit();
        timeSheet.defaultIsBillable = getDefaultIsBillable();
        timeSheet.expirationDate = getExpirationDate();
        timeSheet.maxDuration = getMaxDuration();
        timeSheet.durationUnit = getDurationUnit();
        return timeSheet;
    }

    public TimeSheet unbind() {
        return unbind(new TimeSheet());
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Boolean getDefaultIsBillable() {
        return defaultIsBillable;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }

    @Override
    public String toString() {
        return "TimeSheetRequest{" +
                ", timeUnit=" + timeUnit +
                ", defaultIsBillable=" + defaultIsBillable +
                ", expirationDate=" + expirationDate +
                ", maxDuration=" + maxDuration +
                ", durationUnit=" + durationUnit +
                '}';
    }
}
