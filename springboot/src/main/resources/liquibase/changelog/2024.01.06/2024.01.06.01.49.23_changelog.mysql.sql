-- liquibase formatted sql

-- changeset John:1704505781513-1
ALTER TABLE organize_entity ADD CONSTRAINT UKpf18vv76hywqylixgarh74sj4 UNIQUE (id, is_deleted);

-- changeset John:1704505781513-2
CREATE INDEX IDXc8rxy12jhfct95nxp7ckogevt ON user_entity(id, is_deleted);

-- changeset John:1704505781513-3
CREATE INDEX IDXji80sbcordi5yr2a9v2jr91av ON token_entity(jwt_id);

-- changeset John:1704505781513-4
CREATE INDEX IDXnjdxugvq08k7142tgvracteqe ON user_email_entity(id, is_deleted);

