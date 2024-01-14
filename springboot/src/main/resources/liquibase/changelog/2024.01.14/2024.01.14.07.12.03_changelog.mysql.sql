-- liquibase formatted sql

-- changeset John:1705216338699-1
CREATE TABLE user_black_organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, update_date datetime NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id));

-- changeset John:1705216338699-2
CREATE TABLE user_black_organize_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, update_date datetime NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1705216338699-3
CREATE INDEX FKay1r2ryhqdkvs5ll7ltr1hthu ON user_black_organize_entity(organize_id);

-- changeset John:1705216338699-4
CREATE INDEX FKgeq95vaaswvbtq6g2evd4csp0 ON user_black_organize_closure_entity(organize_id);

-- changeset John:1705216338699-5
CREATE INDEX FKhkqth37t013xa04vx2myptqtk ON user_black_organize_entity(user_id);

-- changeset John:1705216338699-6
CREATE INDEX FKlac7iem33ph9eneiyxld7v6q8 ON user_black_organize_closure_entity(user_id);

-- changeset John:1705216338699-7
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKay1r2ryhqdkvs5ll7ltr1hthu FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1705216338699-8
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKgeq95vaaswvbtq6g2evd4csp0 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1705216338699-9
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKhkqth37t013xa04vx2myptqtk FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1705216338699-10
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKlac7iem33ph9eneiyxld7v6q8 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

