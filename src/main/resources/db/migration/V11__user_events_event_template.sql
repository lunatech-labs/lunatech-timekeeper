DROP TABLE IF EXISTS user_events;
DROP TABLE IF EXISTS event_template;

create table event_template
(
    id serial primary key,
    name varchar(255),
    description varchar(255),
    startdatetime timestamp(6),
    enddatetime timestamp(6),
    organization_id int8
);

alter table event_template
    add constraint fk_event_template_orga
        foreign key (organization_id) references organizations;


create table user_events
(
    id serial primary key,
    name varchar(255),
    description varchar(255),
    startdatetime timestamp(6),
    enddatetime timestamp(6),
    eventtype varchar(255),
    eventtemplate_id int8,
    owner_id int8 not null
);

alter table user_events
    add constraint fk_event_template
        foreign key (eventtemplate_id) references event_template;

alter table user_events
    add constraint fk_owner_user_event
        foreign key (owner_id) references users;

