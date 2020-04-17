create sequence hibernate_sequence;

create table customers
(
    id int8 not null
        constraint customers_pkey
            primary key,
    description varchar(255),
    name varchar(255)
);

create table projects
(
    id int8 not null
        constraint projects_pkey
            primary key,
    billable bool,
    description varchar(255),
    name varchar(255),
    customer_id int8 not null
        constraint fk_projects_customer_id
            references customers
);

create table task
(
    id int8 not null
        constraint task_pkey
            primary key,
    category varchar(255),
    name varchar(255)
);

create table entry
(
    durationtype varchar(31) not null,
    id int8 not null
        constraint entry_pkey
            primary key,
    duration int8 not null,
    startdatetime timestamp(6),
    stopdatetime timestamp(6),
    project_id int8
        constraint fk_entry_project_id
            references projects,
    task_id int8
        constraint fk_entry_task_id
            references task
);

create TABLE public.users (
	id int8 NOT NULL
	    constraint users_pkey
            primary key,
	email varchar(255) not null,
	firstname varchar(255) not null,
	lastname varchar(255) not null,
	profiles varchar(255) NULL
);

create table public.members (
	id int8 NOT null
	    constraint members_pkey
	        primary key,
	role int4,
	user_id int8 not null
	    constraint fk_members_user_id
	        references users,
	project_id int8 not null
        constraint fk_members_project_id
            references projects
);


