-- liquibase formatted sql

-- changeset John:1705124602982-1
CREATE TABLE organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, is_deleted BIT(1) NOT NULL, trait LONGTEXT NOT NULL, update_date datetime NOT NULL, organize_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id), UNIQUE (organize_id));

-- changeset John:1705124602982-2
ALTER TABLE organize_closure_entity ADD CONSTRAINT FK3jvm85xhiucbqu9nmbysm92g2 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

