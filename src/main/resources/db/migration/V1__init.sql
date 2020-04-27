create sequence public.hibernate_sequence;

create table public.clients
(
    id int8 not null
        constraint clients_pkey
            primary key,
    description varchar(255),
    name varchar(255)
);

create table public.projects
(
    id int8 not null
        constraint projects_pkey
            primary key,
    billable bool,
    description varchar(255),
    name varchar(255),
    client_id int8
        constraint fk_projects_client_id
            references public.clients
);

create table public.task
(
    id int8 not null
        constraint task_pkey
            primary key,
    category varchar(255),
    name varchar(255)
);

create table public.entry
(
    durationtype varchar(31) not null,
    id int8 not null
        constraint entry_pkey
            primary key,
    duration int8 not null,
    startdatetime timestamp(6),
    stopdatetime timestamp(6),
    project_id int8
        constraint fk_entries_project_id
            references public.projects,
    task_id int8
        constraint fk_entries_task_id
            references public.task
);

create table public.users
(
    id int8 not null
        constraint users_pkey
            primary key,
    email varchar(255)
        constraint uk_users_email
            unique,
    firstname varchar(255),
    lastname varchar(255),
	picture varchar(255),
    profiles varchar(255)
);

create table public.members
(
    id int8 not null
        constraint members_pkey
            primary key,
    role int4,
    project_id int8 not null
        constraint fk_members_project_id
            references public.projects,
    user_id int8 not null
        constraint fk_members_user_id
            references public.users,
    constraint uk_members_user_id_project_id
        unique (user_id, project_id)
);

