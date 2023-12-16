-- liquibase formatted sql

-- changeset John:1702711387876-1
ALTER TABLE organize_entity ADD CONSTRAINT UK14b15mpwxfbv0emqmilfuxk55 UNIQUE (name, parent_id, delete_key);

