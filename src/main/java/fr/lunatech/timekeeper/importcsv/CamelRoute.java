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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

@ApplicationScoped
public class CamelRoute extends EndpointRouteBuilder {
    /**
     * An injected bean
     */
    @Inject
    CSVTimeEntriesParser csvParser;

    @Override
    public void configure() throws Exception {

        final DataFormat bindy = new BindyCsvDataFormat(ImportedTimeEntry.class);

        from(file("/Users/nmartignole/Dev/Lunatech/lunatech-timekeeper/src/main/resources/input?noop=true&delay=10000&idempotent=false"))
                .unmarshal(bindy)
                .bean(CSVTimeEntriesParser.class, "importEntries")
                .to("log:done")

        ;
    }
}
