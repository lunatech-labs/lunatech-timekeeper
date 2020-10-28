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

import fr.lunatech.timekeeper.exportcsv.TimeEntryCSVParser;
import fr.lunatech.timekeeper.services.imports.ImportService;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CamelImportRoute extends EndpointRouteBuilder {

    @ConfigProperty(name = "timekeeper.import.folder")
    String path;

    @Override
    public void configure() throws Exception {

        final DataFormat bindy = new BindyCsvDataFormat(ImportedTimeEntry.class);

        from(file(path + "?move=.archive&initialDelay=8000"))
                .unmarshal(bindy)
                .recipientList(constant("direct:processClients, direct:processProjects, direct:processMember, direct:processTimeEntries"))
        ;
        from("direct:processClients")
                .bean(CSVTimeEntriesParser.class, "importClients")
                .bean(ImportService.class, "createClients(*, 1)")
        ;
        from("direct:processProjects")
                .bean(CSVTimeEntriesParser.class, "importClientAndProject")
                .bean(ImportService.class, "createProjects(*, 1)")
        ;
        from("direct:processMember")
                .bean(CSVTimeEntriesParser.class, "importUserProjectClient")
                .bean(ImportService.class, "createUserAndAddInProject(*, 1)")
        ;
        from("direct:processTimeEntries")
                .bean(ImportService.class, "createTimeEntries(*, 1)")
        ;

        from("rest:get:export/startDate={dateStart}&endDate={dateEnd}")
                .bean(TimeEntryCSVParser.class, "checkStringParametersAndParseThemIntoLocalDate(${header.dateStart}, ${header.dateEnd})")
                .bean(TimeEntryCSVParser.class, "getTimeEntriesFromDates(*)")
                .transform().simple("from ${header.dateStart} to ${header.dateEnd}");

    }
}