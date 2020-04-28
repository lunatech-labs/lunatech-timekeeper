CREATE TABLE public.organizations (
	id int8 NOT NULL,
	name varchar(255) NULL,
	tokenname varchar(255) NULL,
	CONSTRAINT organizations_pkey PRIMARY KEY (id)
);


insert into public.organizations (id, name, tokenname) values (1,'Lunatech NL', 'lunatech.nl');
insert into public.organizations (id, name, tokenname) values (2,'Lunatech FR', 'lunatech.fr');

ALTER TABLE public.users add organization_id int8 not null default 1 ;

ALTER TABLE public.users ADD constraint fk_users_organization_id foreign KEY  (organization_id) REFERENCES public.organizations (id);

ALTER TABLE public.projects add organization_id int8 not null;

ALTER TABLE public.projects ADD constraint fk_projects_oranisation_id FOREIGN KEY (organization_id) REFERENCES public.organizations (id);

update users set organization_id=1 where true = starts_with(reverse(email),'ln');

update users set organization_id=2 where true = starts_with(reverse(email),'fr');