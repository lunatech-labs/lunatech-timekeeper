ALTER TABLE public.members
RENAME TO roleinproject;

ALTER TABLE public.roleinproject
RENAME CONSTRAINT members_pkey TO roleinproject_pkey;

ALTER TABLE public.roleinproject
RENAME CONSTRAINT fk_members_project_id TO fk_roleinproject_project_id;

ALTER TABLE public.roleinproject
RENAME CONSTRAINT fk_members_user_id TO fk_roleinproject_user_id;

ALTER TABLE public.roleinproject
RENAME CONSTRAINT uk_members_user_id_project_id TO uk_roleinproject_user_id_project_id;