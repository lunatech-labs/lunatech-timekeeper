create table public.timesheets
(
    id                int8 not null
        constraint timesheets_pkey
            primary key,
    defaultisbillable bool,
    durationunit      varchar(255),
    expirationdate    date,
    maxduration       int4,
    timeunit          varchar(255),
    user_id           int8
        constraint fk_timesheets_user
            references public.users,
    project_id        int8 not null
        constraint fk_timesheets_project
            references public.projects
);

create table public.timeentry
(
    id            int8 not null
        constraint timeentry_pkey
            primary key,
    description   varchar(255),
    enddatetime   timestamp(6),
    startdatetime timestamp(6),
    billable      bool,
    comment       varchar(255),
    owner_id      int8
        constraint fk_timeentry_owner
            references public.users,
    timesheet_id  int8
        constraint fk_timeentry_timesheet
            references public.timesheets
);

create table public.timesheets_timeentries
(
    timesheet_id int8 not null
        constraint fk_timesheets_timeentries_timesheet
            references public.timesheets,
    entries_id   int8 not null
        constraint uk_timesheets_timeentries_entries
            unique
        constraint fk_timesheets_timeentries_entries
            references public.timeentry
);




