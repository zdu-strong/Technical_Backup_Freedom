-- liquibase formatted sql

-- changeset John:1704504212842-1
ALTER TABLE logger_entity MODIFY caller_line_number BIGINT;

