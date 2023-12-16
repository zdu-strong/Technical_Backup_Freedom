-- liquibase formatted sql

-- changeset John:1702762423480-1
CREATE TABLE distributed_execution_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, name VARCHAR(255) NOT NULL, page_num BIGINT NOT NULL, page_size BIGINT NOT NULL, update_date datetime NOT NULL, version VARCHAR(255) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_ENTITY PRIMARY KEY (id));

-- changeset John:1702762423480-2
ALTER TABLE distributed_execution_entity ADD CONSTRAINT UK61qqq6cisvchp1mxmk0x63796 UNIQUE (version, page_size, page_num);

