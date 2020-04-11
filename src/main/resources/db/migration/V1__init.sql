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
    customer_id int8
        constraint fkhifq8n2q5bsnghr1b0dcxy3ev
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
    activity_id int8
        constraint fkkid6nd3vf5egu7mmigdts307n
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
        constraint fkp74anasx1tabe6pbnk0gubycx
            references activities,
    user_id int8
        constraint fkqblq7520ib0o4sfd5jp13rbf5
            references users
);

