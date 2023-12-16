-- liquibase formatted sql

-- changeset John:1702738473675-1
ALTER TABLE organize_entity ADD CONSTRAINT UK14b15mpwxfbv0emqmilfuxk55 UNIQUE (parent_id, name, delete_key);

-- changeset John:1702738473675-2
DROP INDEX FKg1i0kxqrixd8fdpw6me7x5t3q ON organize_entity;

