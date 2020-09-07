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

ALTER TABLE timeentries ADD COLUMN numberofhours int4 default 0;

/*
-- Please note that H2 does not support the follow SQL statement since DATE() function does not exist with H2
UPDATE timeentries as T1
SET  numberOfHours = (select EXTRACT(EPOCH FROM (enddatetime_deprecated - startdatetime ))/3600  as diff_time_hours from timeentries T3 WHERE T3.id = T1.id);

-- We can then drop enddatetime once the data are migrated
ALTER TABLE timeentries DROP COLUMN enddatetime_deprecated;
 */
ALTER TABLE timeentries RENAME COLUMN enddatetime TO enddatetime_deprecated;
