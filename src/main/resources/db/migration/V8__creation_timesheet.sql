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

DROP TABLE IF EXISTS timesheets_time_entry CASCADE ;
DROP TABLE IF EXISTS time_entry CASCADE ;
DROP TABLE IF EXISTS timesheets CASCADE ;

create table user_events
(
    id int8 not null
        constraint user_events_pkey
            primary key,
    description varchar(255),
    enddatetime timestamp(6),
    startdatetime timestamp(6)
);

create table timesheets
(
    id int8 not null
        constraint timesheets_pkey
            primary key,
    defaultisbillable bool,
    durationunit varchar(255),
    expirationdate date,
    maxduration int4,
    timeunit varchar(255),
    user_id int8
        constraint fk_timesheets_users
            references users,
    project_id int8 not null
        constraint fk_timesheets_projects
            references projects
);

create table timeentries
(
    id int8 not null
        constraint timeentries_pkey
            primary key,
    billable bool,
    comment varchar(255),
    enddatetime timestamp(6),
    startdatetime timestamp(6),
    timesheet_id int8 not null
        constraint fk_timeentries_timesheets
            references timesheets
);