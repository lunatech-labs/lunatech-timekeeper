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

-- This statement does not work with H2.
-- Use this SQL statement if you have duplicate event_template in your pgSQL DEV DB :
/*
DELETE
from event_template
WHERE event_template.ID NOT IN
      (SELECT ID FROM (SELECT DISTINCT ON (startdatetime,enddatetime,name) * FROM event_template) as delete_q);
*/
ALTER TABLE event_template
    ADD CONSTRAINT single_event_check UNIQUE (startdatetime, enddatetime, name);
