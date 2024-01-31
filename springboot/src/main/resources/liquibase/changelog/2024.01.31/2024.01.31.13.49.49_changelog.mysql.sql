-- liquibase formatted sql

-- changeset John:1706709009448-1
ALTER TABLE organize_entity DROP KEY UKpf18vv76hywqylixgarh74sj4;

-- changeset John:1706709009448-2
DROP INDEX IDXc8rxy12jhfct95nxp7ckogevt ON user_entity;

-- changeset John:1706709009448-3
DROP INDEX IDXh2tw4b9y7sentv7whtxxxnv3o ON user_email_entity;

-- changeset John:1706709009448-4
DROP INDEX IDXnjdxugvq08k7142tgvracteqe ON user_email_entity;

