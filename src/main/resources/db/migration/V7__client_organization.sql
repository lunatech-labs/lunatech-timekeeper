ALTER TABLE clients add organization_id int8 not null;

ALTER TABLE clients ADD constraint fk_clients_organization_id foreign KEY (organization_id) REFERENCES organizations (id);