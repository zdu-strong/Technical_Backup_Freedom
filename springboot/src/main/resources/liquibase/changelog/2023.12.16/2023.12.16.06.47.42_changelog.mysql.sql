-- liquibase formatted sql

-- changeset John:1702709279116-2
ALTER TABLE organize_entity ADD delete_key VARCHAR(255) NOT NULL;

-- changeset John:1702709279116-1
ALTER TABLE organize_entity MODIFY name VARCHAR(512);

