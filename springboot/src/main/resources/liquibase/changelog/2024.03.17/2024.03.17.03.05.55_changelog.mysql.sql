-- liquibase formatted sql

-- changeset John:1710644771818-3
ALTER TABLE storage_space_entity ADD CONSTRAINT UK_dfa38od24bw4oioe3ju04tpt3 UNIQUE (folder_name);

-- changeset John:1710644771818-1
ALTER TABLE storage_space_entity DROP KEY UK_dfa38od24bw4oioe3ju04tpt3;

-- changeset John:1710644771818-2
ALTER TABLE storage_space_entity ADD CONSTRAINT UK_dfa38od24bw4oioe3ju04tpt3 UNIQUE (folder_name);

