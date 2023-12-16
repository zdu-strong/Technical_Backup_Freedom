-- liquibase formatted sql

-- changeset John:1702738886606-1
ALTER TABLE organize_entity ADD deleted_key VARCHAR(255) NOT NULL;

-- changeset John:1702738886606-2
ALTER TABLE user_email_entity ADD deleted_key VARCHAR(255) NOT NULL;

-- changeset John:1702738886606-3
ALTER TABLE organize_entity ADD CONSTRAINT UK5sbf5vxcdf70l5qsp050ortys UNIQUE (parent_id, name, deleted_key);

-- changeset John:1702738886606-4
ALTER TABLE user_email_entity ADD CONSTRAINT UKqk35yh86v7c9xa0scp4p795be UNIQUE (email, deleted_key);

-- changeset John:1702738886606-5
ALTER TABLE organize_entity DROP KEY UK14b15mpwxfbv0emqmilfuxk55;

-- changeset John:1702738886606-6
ALTER TABLE user_email_entity DROP KEY UK3p1gui2lihpiaob8vx1y00hum;

-- changeset John:1702738886606-7
ALTER TABLE organize_entity DROP COLUMN delete_key;

-- changeset John:1702738886606-8
ALTER TABLE user_email_entity DROP COLUMN delete_key;

