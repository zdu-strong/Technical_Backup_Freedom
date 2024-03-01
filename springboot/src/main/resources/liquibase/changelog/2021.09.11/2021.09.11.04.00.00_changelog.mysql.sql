-- liquibase formatted sql
-- changeset zdu:latest

ALTER DATABASE CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE DATABASECHANGELOG  convert to CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE DATABASECHANGELOGLOCK  convert to CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;