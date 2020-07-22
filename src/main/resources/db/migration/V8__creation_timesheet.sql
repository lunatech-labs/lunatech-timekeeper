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