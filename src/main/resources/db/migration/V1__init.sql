create sequence hibernate_sequence;

create table customers
(
    id int8 not null
        constraint customers_pkey
            primary key,
    description varchar(255),
    name varchar(255)
);

create table activities
(
    id int8 not null
        constraint activities_pkey
            primary key,
    billable bool,
    description varchar(255),
    name varchar(255),
    customer_id int8 not null
        constraint fkeds7272usjbk8laufntn26ooc
            references customers
);

create table flyway_schema_history
(
    installed_rank int4 not null
        constraint flyway_schema_history_pk
            primary key,
    version varchar(50),
    description varchar(200) not null,
    type varchar(20) not null,
    script varchar(1000) not null,
    checksum int4,
    installed_by varchar(100) not null,
    installed_on timestamp(6) default now() not null,
    execution_time int4 not null,
    success bool not null
);

create index flyway_schema_history_s_idx
    on flyway_schema_history (success);

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
    activity_id int8
        constraint fkk6uif1x0tevuau7ihho4sluxg
            references activities,
    task_id int8
        constraint fk5nk4rhn8vvuf2lyk9o69pq6g1
            references task
);

create table users
(
    id int8 not null
        constraint users_pkey
            primary key,
    email varchar(255),
    firstname varchar(255),
    lastname varchar(255),
    profiles varchar(255)
);

create table members
(
    id int8 not null
        constraint members_pkey
            primary key,
    role int4,
    activity_id int8 not null
        constraint fksxaqdp2g912rcesikq3wqbwem
            references activities,
    user_id int8
        constraint fkpj3n6wh5muoeakc485whgs3x5
            references users
);

