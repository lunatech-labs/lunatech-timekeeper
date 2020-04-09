CREATE SEQUENCE hibernate_sequence;

-- public.customer definition

-- Drop table

-- DROP TABLE public.customer;

CREATE TABLE public.customer (
	id int8 NOT NULL,
	description varchar(255) NULL,
	name varchar(255) NULL,
	CONSTRAINT customer_pkey PRIMARY KEY (id)
);


-- public.task definition

-- Drop table

-- DROP TABLE public.task;

CREATE TABLE public.task (
	id int8 NOT NULL,
	category varchar(255) NULL,
	name varchar(255) NULL,
	CONSTRAINT task_pkey PRIMARY KEY (id)
);


-- public.usertk definition

-- Drop table

-- DROP TABLE public.usertk;

CREATE TABLE public.usertk (
	id int8 NOT NULL,
	email varchar(255) NULL,
	firstname varchar(255) NULL,
	lastname varchar(255) NULL,
	profile int4 NULL,
	CONSTRAINT usertk_pkey PRIMARY KEY (id)
);


-- public.activity definition

-- Drop table

-- DROP TABLE public.activity;

CREATE TABLE public.activity (
	id int8 NOT NULL,
	billale bool NULL,
	description varchar(255) NULL,
	name varchar(255) NULL,
	customer_id int8 NULL,
	CONSTRAINT activity_pkey PRIMARY KEY (id),
	CONSTRAINT activity_customer_id_FK FOREIGN KEY (customer_id) REFERENCES customer(id)
);


-- public.customer_activity definition

-- Drop table

-- DROP TABLE public.customer_activity;

CREATE TABLE public.customer_activity (
	customer_id int8 NOT NULL,
	activities_id int8 NOT NULL,
	CONSTRAINT customer_activity_activities_id_UNIQUE UNIQUE (activities_id),
	CONSTRAINT customer_activity_activity_id_FK FOREIGN KEY (activities_id) REFERENCES activity(id),
	CONSTRAINT customer_activity_customer_id_FK FOREIGN KEY (customer_id) REFERENCES customer(id)
);


-- public.entry definition

-- Drop table

-- DROP TABLE public.entry;

CREATE TABLE public.entry (
	durationtype varchar(31) NOT NULL,
	id int8 NOT NULL,
	duration int8 NOT NULL,
	startdatetime timestamp NULL,
	stopdatetime timestamp NULL,
	activity_id int8 NULL,
	task_id int8 NULL,
	CONSTRAINT entry_pkey PRIMARY KEY (id),
	CONSTRAINT entry_activity_id_FK FOREIGN KEY (activity_id) REFERENCES activity(id),
	CONSTRAINT entry_task_id_FK FOREIGN KEY (task_id) REFERENCES task(id)
);


-- public."member" definition

-- Drop table

-- DROP TABLE public."member";

CREATE TABLE public.member (
	id int8 NOT NULL,
	role int4 NULL,
	user_id int8 NULL,
	CONSTRAINT member_pkey PRIMARY KEY (id),
	CONSTRAINT member_user_id_FK FOREIGN KEY (user_id) REFERENCES usertk(id)
);


-- public.activity_member definition

-- Drop table

-- DROP TABLE public.activity_member;

CREATE TABLE public.activity_member (
	activity_id int8 NOT NULL,
	members_id int8 NOT NULL,
	CONSTRAINT activity_member_PK UNIQUE (members_id),
	CONSTRAINT activity_member_members_id_FK FOREIGN KEY (members_id) REFERENCES member(id),
	CONSTRAINT activity_member_activity_id_FK FOREIGN KEY (activity_id) REFERENCES activity(id)
);