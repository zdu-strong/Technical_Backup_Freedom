-- liquibase formatted sql

-- changeset John:1702709662753-1
ALTER TABLE organize_entity ADD CONSTRAINT UKahtjwkhdlblear5fwgm2t6xvg UNIQUE (name, delete_key);

