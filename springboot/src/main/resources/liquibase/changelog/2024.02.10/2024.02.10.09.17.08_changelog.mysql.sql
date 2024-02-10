-- liquibase formatted sql

-- changeset John:1707556644279-1
CREATE TABLE distributed_execution_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, name VARCHAR(255) NOT NULL, page_num BIGINT NOT NULL, page_size BIGINT NOT NULL, update_date datetime(6) NOT NULL, version VARCHAR(255) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-2
CREATE TABLE encrypt_decrypt_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_ENCRYPT_DECRYPT_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-3
CREATE TABLE friendship_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, has_initiative BIT(1) NOT NULL, is_friend BIT(1) NOT NULL, is_in_blacklist BIT(1) NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, friend_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_FRIENDSHIP_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-4
CREATE TABLE logger_entity (id VARCHAR(255) NOT NULL, caller_class_name LONGTEXT NOT NULL, caller_line_number BIGINT NOT NULL, caller_method_name VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, exception_class_name LONGTEXT NOT NULL, exception_message LONGTEXT NOT NULL, exception_stack_trace LONGTEXT NOT NULL, git_commit_date datetime(6) NOT NULL, git_commit_id VARCHAR(255) NOT NULL, has_exception BIT(1) NOT NULL, level VARCHAR(255) NOT NULL, logger_name LONGTEXT NOT NULL, message LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_LOGGER_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-5
CREATE TABLE long_term_task_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_done BIT(1) NOT NULL, result LONGTEXT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_LONG_TERM_TASK_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-6
CREATE TABLE organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_deleted BIT(1) NOT NULL, trait LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id), UNIQUE (organize_id));

-- changeset John:1707556644279-7
CREATE TABLE organize_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deleted_key VARCHAR(255) NOT NULL, is_deleted BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, parent_id VARCHAR(255) NULL, CONSTRAINT PK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-8
CREATE TABLE storage_space_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, folder_name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_STORAGE_SPACE_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-9
CREATE TABLE token_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, jwt_id VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_TOKEN_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-10
CREATE TABLE user_black_organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-11
CREATE TABLE user_black_organize_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_BLACK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-12
CREATE TABLE user_email_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deleted_key VARCHAR(255) NOT NULL, email VARCHAR(512) NOT NULL, is_deleted BIT(1) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-13
CREATE TABLE user_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_deleted BIT(1) NOT NULL, password LONGTEXT NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, update_date datetime(6) NOT NULL, username LONGTEXT NOT NULL, CONSTRAINT PK_USER_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-14
CREATE TABLE user_message_entity (id VARCHAR(255) NOT NULL, content LONGTEXT NOT NULL, create_date datetime(6) NOT NULL, file_name LONGTEXT NULL, folder_name VARCHAR(255) NULL, folder_size BIGINT NULL, is_recall BIT(1) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-15
CREATE TABLE verification_code_email_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, email VARCHAR(512) NOT NULL, has_used BIT(1) NOT NULL, is_passed BIT(1) NOT NULL, update_date datetime(6) NOT NULL, verification_code VARCHAR(255) NOT NULL, CONSTRAINT PK_VERIFICATION_CODE_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1707556644279-16
ALTER TABLE organize_entity ADD CONSTRAINT UK5sbf5vxcdf70l5qsp050ortys UNIQUE (parent_id, name, deleted_key);

-- changeset John:1707556644279-17
ALTER TABLE distributed_execution_entity ADD CONSTRAINT UK61qqq6cisvchp1mxmk0x63796 UNIQUE (version, page_size, page_num);

-- changeset John:1707556644279-18
ALTER TABLE user_email_entity ADD CONSTRAINT UKqk35yh86v7c9xa0scp4p795be UNIQUE (email, deleted_key);

-- changeset John:1707556644279-19
ALTER TABLE friendship_entity ADD CONSTRAINT UKqsamcn1vehewm87nlwtjw2kfk UNIQUE (user_id, friend_id);

-- changeset John:1707556644279-20
CREATE INDEX FK23c715x0gcdv29x9l92r8dc74 ON user_message_entity(user_id);

-- changeset John:1707556644279-21
CREATE INDEX FKay1r2ryhqdkvs5ll7ltr1hthu ON user_black_organize_entity(organize_id);

-- changeset John:1707556644279-22
CREATE INDEX FKchycpasyr16kt66k09e6ompve ON token_entity(user_id);

-- changeset John:1707556644279-23
CREATE INDEX FKgeq95vaaswvbtq6g2evd4csp0 ON user_black_organize_closure_entity(organize_id);

-- changeset John:1707556644279-24
CREATE INDEX FKhkqth37t013xa04vx2myptqtk ON user_black_organize_entity(user_id);

-- changeset John:1707556644279-25
CREATE INDEX FKlac7iem33ph9eneiyxld7v6q8 ON user_black_organize_closure_entity(user_id);

-- changeset John:1707556644279-26
CREATE INDEX FKpq2at14h3gljwg848p43aw14w ON friendship_entity(friend_id);

-- changeset John:1707556644279-27
CREATE INDEX FKq1njl6uveplkpgu70115gbf5o ON user_email_entity(user_id);

-- changeset John:1707556644279-28
CREATE INDEX IDX46cw449tt4ikqgeepvj4u4d7a ON storage_space_entity(folder_name);

-- changeset John:1707556644279-29
CREATE INDEX IDXg4isidkyw8pui6rnpe5ii9fdb ON user_message_entity(folder_name);

-- changeset John:1707556644279-30
CREATE INDEX IDXji80sbcordi5yr2a9v2jr91av ON token_entity(jwt_id);

-- changeset John:1707556644279-31
CREATE INDEX IDXoxeihgs43v794u9lhrh79ugm5 ON verification_code_email_entity(email, create_date);

-- changeset John:1707556644279-32
ALTER TABLE user_message_entity ADD CONSTRAINT FK23c715x0gcdv29x9l92r8dc74 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-33
ALTER TABLE organize_closure_entity ADD CONSTRAINT FK3jvm85xhiucbqu9nmbysm92g2 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-34
ALTER TABLE friendship_entity ADD CONSTRAINT FK4n7gua4wuvh9ymsen9pdt49v6 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-35
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKay1r2ryhqdkvs5ll7ltr1hthu FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-36
ALTER TABLE token_entity ADD CONSTRAINT FKchycpasyr16kt66k09e6ompve FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-37
ALTER TABLE organize_entity ADD CONSTRAINT FKg1i0kxqrixd8fdpw6me7x5t3q FOREIGN KEY (parent_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-38
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKgeq95vaaswvbtq6g2evd4csp0 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-39
ALTER TABLE user_black_organize_entity ADD CONSTRAINT FKhkqth37t013xa04vx2myptqtk FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-40
ALTER TABLE user_black_organize_closure_entity ADD CONSTRAINT FKlac7iem33ph9eneiyxld7v6q8 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-41
ALTER TABLE friendship_entity ADD CONSTRAINT FKpq2at14h3gljwg848p43aw14w FOREIGN KEY (friend_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1707556644279-42
ALTER TABLE user_email_entity ADD CONSTRAINT FKq1njl6uveplkpgu70115gbf5o FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

