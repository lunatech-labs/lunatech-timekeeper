
drop table public.clients cascade;
drop table public.projects cascade;
drop table public.task cascade;
drop table public.entry cascade;
drop table public.users cascade;
drop table public.members cascade;
drop table public.organizations cascade;
drop sequence public.hibernate_sequence;

-- new structure

create sequence hibernate_sequence;

create table clients
(
    id int8 not null
        constraint clients_pkey
            primary key,
    description varchar(255),
    name varchar(255)
		constraint uk_clients_name
			unique
);

create table organizations
(
    id int8 not null
        constraint organizations_pkey
            primary key,
	name varchar(255)
		constraint uk_organizations_name
			unique,
    tokenname varchar(255)
);

create table projects
(
    id int8 not null
        constraint projects_pkey
            primary key,
    billable bool,
    description varchar(255),
    name varchar(255),
    publicaccess bool,
    client_id int8
        constraint fk_projects_client_id
            references clients,
    organization_id int8 not null
        constraint fk_projects_organization_id
			references organizations,
	constraint uk_projects_name_organization_id
		unique (name, organization_id)
);

create table users
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
    profiles varchar(255),
    organization_id int8 not null
        constraint fk_users_organization_id
            references organizations
);

create table projects_users
(
    id int8 not null
        constraint projects_users_pkey
            primary key,
    manager bool,
    project_id int8 not null
        constraint fk_projects_users_project_id
            references projects,
    user_id int8 not null
        constraint fk_projects_users_user_id
            references users,
    constraint uk_projects_users_project_id_user_id
        unique (project_id, user_id)
);

