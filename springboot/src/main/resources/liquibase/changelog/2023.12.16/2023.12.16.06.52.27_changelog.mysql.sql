-- liquibase formatted sql

-- changeset John:1702709565119-1
ALTER TABLE organize_entity MODIFY name VARCHAR(512) NOT NULL;

