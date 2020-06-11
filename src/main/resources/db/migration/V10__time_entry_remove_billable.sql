ALTER TABLE public.timeentries drop column billable;
ALTER TABLE public.timeentries add eventType varchar(255) null;
