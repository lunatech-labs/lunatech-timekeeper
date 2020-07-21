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

/* Replace with your SQL commands */
BEGIN ;
TRUNCATE TABLE clients RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_events RESTART IDENTITY CASCADE;
TRUNCATE TABLE timeentries RESTART IDENTITY CASCADE;
TRUNCATE TABLE timesheets RESTART IDENTITY CASCADE;
TRUNCATE TABLE projects_users RESTART IDENTITY CASCADE;
TRUNCATE TABLE projects RESTART IDENTITY CASCADE;
TRUNCATE TABLE event_template RESTART IDENTITY CASCADE;
COMMIT ;