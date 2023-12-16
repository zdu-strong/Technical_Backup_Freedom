-- liquibase formatted sql

-- changeset John:1702707093922-1
ALTER TABLE token_entity MODIFY jwt_id VARCHAR(255) NOT NULL;

