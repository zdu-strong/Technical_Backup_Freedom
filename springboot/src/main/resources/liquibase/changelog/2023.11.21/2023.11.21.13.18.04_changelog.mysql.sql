-- liquibase formatted sql

-- changeset John:1700572704715-1
ALTER TABLE organize_entity ADD name LONGTEXT NOT NULL;

-- changeset John:1700572704715-2
ALTER TABLE organize_entity ADD parent_id VARCHAR(255) NULL;

-- changeset John:1700572704715-3
CREATE INDEX FKg1i0kxqrixd8fdpw6me7x5t3q ON organize_entity(parent_id);

-- changeset John:1700572704715-4
ALTER TABLE organize_entity ADD CONSTRAINT FKg1i0kxqrixd8fdpw6me7x5t3q FOREIGN KEY (parent_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1700572704715-5
ALTER TABLE organize_shadow_entity DROP FOREIGN KEY FK87p106yrm418be8a0scsu0ig5;

-- changeset John:1700572704715-6
ALTER TABLE organize_closure_entity DROP FOREIGN KEY FKfrbs0stjmmje4we9n1t0cf0oh;

-- changeset John:1700572704715-7
ALTER TABLE organize_entity DROP FOREIGN KEY FKgacw5qpr2xa2hdenbtqo6tcb0;

-- changeset John:1700572704715-8
ALTER TABLE organize_closure_entity DROP FOREIGN KEY FKrq3vl8q12mdppvdre3bcv6fce;

-- changeset John:1700572704715-9
DROP TABLE organize_closure_entity;

-- changeset John:1700572704715-10
DROP TABLE organize_shadow_entity;

-- changeset John:1700572704715-11
ALTER TABLE organize_entity DROP COLUMN level;

-- changeset John:1700572704715-12
ALTER TABLE organize_entity DROP COLUMN organize_shadow_id;

