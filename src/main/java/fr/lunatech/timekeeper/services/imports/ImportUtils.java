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

package fr.lunatech.timekeeper.services.imports;

import fr.lunatech.timekeeper.models.Organization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ImportUtils {

    private ImportUtils() {
    }

    protected static LocalDateTime parseToLocalDateTime(String date, String time) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null date");
        }
        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Cannot parse a null time");
        }
        LocalDate dateUpdated = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        LocalTime timeUpdated = LocalTime.parse(time);

        LocalDateTime localDateTime = LocalDateTime.of(dateUpdated, timeUpdated);
        return localDateTime;
    }

    protected static String updateEmailToOrganization(String email, Organization organization) {
        if (email == null) return null;

        String emailAddress = email.substring(0, email.indexOf("@"));
        String domain = email.substring(email.indexOf("@") + 1);

        if (!domain.endsWith(organization.tokenName)) {
            return (emailAddress + "@" + organization.tokenName).trim().toLowerCase();
        }
        return email.trim().toLowerCase();
    }
}
