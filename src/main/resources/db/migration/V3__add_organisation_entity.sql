CREATE TABLE public.organisations (
	id int8 NOT NULL,
	"name" varchar(255) NULL,
	CONSTRAINT organisations_pkey PRIMARY KEY (id)
);


ALTER TABLE public.users add organisation_id int8 not null;

ALTER TABLE public.users ADD constraint fk_users_oranisation_id foreign KEY  (organisation_id) REFERENCES public.organisations (id);

ALTER TABLE public.projects add organisation_id int8 not null;

ALTER TABLE public.projects ADD constraint fk_projects_oranisation_id FOREIGN KEY (organisation_id) REFERENCES public.organisations (id);