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

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ExportResourceApi;
import fr.lunatech.timekeeper.services.exports.ExportService;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExportResource implements ExportResourceApi {

    private static Logger logger = LoggerFactory.getLogger(ExportResource.class);

    @Inject
    ExportService exportService;

    @RolesAllowed({"admin"})
    @Override
    public Response exportCSV(String startString, String endString) {
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = TimeKeeperDateUtils.parseToLocalDate(startString);
            endDate = TimeKeeperDateUtils.parseToLocalDate(endString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Start or end date format error");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("StartDate should be before endDate");
        }

        //todo chunked response can be better?
        try {
            StringBuilder sb = exportService.getTimeEntriesBetweenTwoDateForExportToTogglCsv(startDate, endDate);
            return Response.ok(sb.toString()).build();
        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
            return Response.serverError().build();
        }
    }
}
