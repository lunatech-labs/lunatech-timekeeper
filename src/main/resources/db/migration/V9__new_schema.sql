
drop table IF EXISTS projects_users cascade;
drop table IF EXISTS user_events cascade;
drop table IF EXISTS clients cascade;
drop table IF EXISTS projects cascade;
drop table IF EXISTS task cascade;
drop table IF EXISTS entry cascade;
drop table IF EXISTS users cascade;
drop table IF EXISTS members cascade;
drop table IF EXISTS organizations cascade;
DROP TABLE IF EXISTS timesheets_time_entry CASCADE ;
DROP TABLE IF EXISTS time_entry CASCADE ;
DROP TABLE IF EXISTS timesheets CASCADE ;

DROP TABLE IF EXISTS time_week CASCADE ;
DROP TABLE IF EXISTS time_week_time_entry CASCADE ;
DROP TABLE IF EXISTS time_week_time_entry CASCADE ;
DROP TABLE IF EXISTS timeentries CASCADE ;

drop sequence IF EXISTS hibernate_sequence;

create table clients
(
    id serial primary key,
    description varchar(255),
    name varchar(255)
		constraint uk_clients_name
			unique,
    organization_id int8 not null
);

create table organizations
(
    id serial primary key,
	name varchar(255)
		constraint uk_organizations_name
			unique,
    tokenname varchar(255) CONSTRAINT uk_organizations_tokenname UNIQUE
);

ALTER TABLE clients ADD constraint fk_clients_organization_id foreign KEY (organization_id) REFERENCES organizations (id);

create table projects
(
    id serial primary key,
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
    id serial primary key,
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
    id serial primary key,
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

create table user_events
(
    id serial primary key,
    description varchar(255),
    enddatetime timestamp(6),
    startdatetime timestamp(6)
);

create table timesheets
(
    id serial primary key,
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
    id serial
            primary key,
    billable bool,
    comment varchar(255),
    startdatetime timestamp(6),
    enddatetime timestamp(6),
    timesheet_id int8 not null
        constraint fk_timeentries_timesheets
            references timesheets
);
