ALTER TABLE public.timeentries drop column billable;
ALTER TABLE public.user_events add eventType varchar(255) null;
