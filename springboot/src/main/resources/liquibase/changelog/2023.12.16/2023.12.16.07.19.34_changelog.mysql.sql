-- liquibase formatted sql

-- changeset John:1702711192904-1
ALTER TABLE organize_entity MODIFY name VARCHAR(255) NOT NULL;

