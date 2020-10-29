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

import com.ibm.icu.impl.Pair;
import fr.lunatech.timekeeper.resources.openapi.ExportResourceApi;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class ExportResource implements ExportResourceApi {

    @Inject
    ProducerTemplate producerTemplate;

    private static Logger logger = LoggerFactory.getLogger(ExportResource.class);

    @RolesAllowed({"admin"})
    @Override
    public String exportCSV(String startString, String endString) {
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = TimeKeeperDateUtils.parseToLocalDate(startString);
            endDate = TimeKeeperDateUtils.parseToLocalDate(endString);
        } catch (DateTimeParseException e) {
            logger.warn("StartDate or EndDate is not valid to be parse into a LocalDate");
            throw new IllegalArgumentException("Start or end date format error");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("StartDate should be before endDate");
        }
        logger.debug("Start : " + startDate + " end : " + endDate);
        return producerTemplate.requestBodyAndHeaders("direct:export", Pair.of(startDate, endDate), Map.of("startDate", startDate, "endDate", endDate), String.class);
    }
}
