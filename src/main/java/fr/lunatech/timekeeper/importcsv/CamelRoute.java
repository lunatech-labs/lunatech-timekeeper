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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;


@ApplicationScoped
public class CamelRoute extends RouteBuilder {
    /**
     * An injected bean
     */
    @Inject
    NamedBean csvParser;

    @Override
    public void configure() throws Exception {
        final DataFormat bindy = new BindyCsvDataFormat(fr.lunatech.timekeeper.importcsv.TimerEntry.class);


        from("file:/Users/nmartignole/Dev/lunatech-timekeeper/src/main/resources/file?noop=true&amp;delay=3000&amp;initialDelay=4000&amp;idempotent=false")
                .unmarshal(bindy)
                .setBody( exchange -> "test " + csvParser.hello("coucou"))
                .to("log:example");
    }
}
