-- liquibase formatted sql

-- changeset John:1692169297263-1
CREATE TABLE encrypt_decrypt_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime NOT NULL, CONSTRAINT PK_ENCRYPT_DECRYPT_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-2
CREATE TABLE friendship_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, has_initiative BIT(1) NOT NULL, is_friend BIT(1) NOT NULL, is_in_blacklist BIT(1) NOT NULL, secret_key_ofaes LONGTEXT NOT NULL, update_date datetime NOT NULL, friend_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_FRIENDSHIP_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-3
CREATE TABLE logger_entity (id VARCHAR(255) NOT NULL, caller_class_name LONGTEXT NOT NULL, caller_line_number INT NOT NULL, caller_method_name VARCHAR(255) NOT NULL, create_date datetime NOT NULL, exception_class_name LONGTEXT NOT NULL, exception_message LONGTEXT NOT NULL, exception_stack_trace LONGTEXT NOT NULL, git_commit_date datetime NOT NULL, git_commit_id VARCHAR(255) NOT NULL, has_exception BIT(1) NOT NULL, level VARCHAR(255) NOT NULL, logger_name LONGTEXT NOT NULL, message LONGTEXT NOT NULL, update_date datetime NOT NULL, CONSTRAINT PK_LOGGER_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-4
CREATE TABLE long_term_task_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, is_done BIT(1) NOT NULL, result LONGTEXT NULL, update_date datetime NOT NULL, CONSTRAINT PK_LONG_TERM_TASK_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-5
CREATE TABLE organize_closure_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, gap BIGINT NOT NULL, update_date datetime NOT NULL, ancestor_id VARCHAR(255) NOT NULL, descendant_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_CLOSURE_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-6
CREATE TABLE organize_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, is_deleted BIT(1) NOT NULL, level BIGINT NOT NULL, update_date datetime NOT NULL, organize_shadow_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-7
CREATE TABLE organize_shadow_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, is_deleted BIT(1) NOT NULL, name LONGTEXT NOT NULL, update_date datetime NOT NULL, parent_id VARCHAR(255) NULL, CONSTRAINT PK_ORGANIZE_SHADOW_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-8
CREATE TABLE storage_space_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, folder_name VARCHAR(255) NOT NULL, update_date datetime NOT NULL, CONSTRAINT PK_STORAGE_SPACE_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-9
CREATE TABLE token_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, jwt_id VARCHAR(255) NULL, private_key_ofrsa LONGTEXT NOT NULL, update_date datetime NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_TOKEN_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-10
CREATE TABLE user_email_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, delete_key VARCHAR(255) NOT NULL, email VARCHAR(512) NOT NULL, is_deleted BIT(1) NOT NULL, update_date datetime NOT NULL, verification_code VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_EMAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-11
CREATE TABLE user_entity (id VARCHAR(255) NOT NULL, create_date datetime NOT NULL, has_registered BIT(1) NOT NULL, is_deleted BIT(1) NOT NULL, private_key_ofrsa LONGTEXT NOT NULL, public_key_ofrsa LONGTEXT NOT NULL, update_date datetime NOT NULL, username LONGTEXT NOT NULL, CONSTRAINT PK_USER_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-12
CREATE TABLE user_message_entity (id VARCHAR(255) NOT NULL, content LONGTEXT NOT NULL, create_date datetime NOT NULL, file_name LONGTEXT NULL, folder_name VARCHAR(255) NULL, folder_size BIGINT NULL, is_recall BIT(1) NOT NULL, update_date datetime NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_ENTITY PRIMARY KEY (id));

-- changeset John:1692169297263-13
ALTER TABLE user_email_entity ADD CONSTRAINT UK3p1gui2lihpiaob8vx1y00hum UNIQUE (email, delete_key);

-- changeset John:1692169297263-14
ALTER TABLE friendship_entity ADD CONSTRAINT UK9d80pp0etnmlwc9nqbo1wnjj6 UNIQUE (user_id, friend_id);

-- changeset John:1692169297263-15
CREATE INDEX FK23c715x0gcdv29x9l92r8dc74 ON user_message_entity(user_id);

-- changeset John:1692169297263-16
CREATE INDEX FK87p106yrm418be8a0scsu0ig5 ON organize_shadow_entity(parent_id);

-- changeset John:1692169297263-17
CREATE INDEX FKchycpasyr16kt66k09e6ompve ON token_entity(user_id);

-- changeset John:1692169297263-18
CREATE INDEX FKfrbs0stjmmje4we9n1t0cf0oh ON organize_closure_entity(ancestor_id);

-- changeset John:1692169297263-19
CREATE INDEX FKgacw5qpr2xa2hdenbtqo6tcb0 ON organize_entity(organize_shadow_id);

-- changeset John:1692169297263-20
CREATE INDEX FKpq2at14h3gljwg848p43aw14w ON friendship_entity(friend_id);

-- changeset John:1692169297263-21
CREATE INDEX FKq1njl6uveplkpgu70115gbf5o ON user_email_entity(user_id);

-- changeset John:1692169297263-22
CREATE INDEX FKrq3vl8q12mdppvdre3bcv6fce ON organize_closure_entity(descendant_id);

-- changeset John:1692169297263-23
CREATE INDEX IDX46cw449tt4ikqgeepvj4u4d7a ON storage_space_entity(folder_name);

-- changeset John:1692169297263-24
CREATE INDEX IDXg4isidkyw8pui6rnpe5ii9fdb ON user_message_entity(folder_name);

-- changeset John:1692169297263-25
ALTER TABLE user_message_entity ADD CONSTRAINT FK23c715x0gcdv29x9l92r8dc74 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-26
ALTER TABLE friendship_entity ADD CONSTRAINT FK4n7gua4wuvh9ymsen9pdt49v6 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-27
ALTER TABLE organize_shadow_entity ADD CONSTRAINT FK87p106yrm418be8a0scsu0ig5 FOREIGN KEY (parent_id) REFERENCES organize_shadow_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-28
ALTER TABLE token_entity ADD CONSTRAINT FKchycpasyr16kt66k09e6ompve FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-29
ALTER TABLE organize_closure_entity ADD CONSTRAINT FKfrbs0stjmmje4we9n1t0cf0oh FOREIGN KEY (ancestor_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-30
ALTER TABLE organize_entity ADD CONSTRAINT FKgacw5qpr2xa2hdenbtqo6tcb0 FOREIGN KEY (organize_shadow_id) REFERENCES organize_shadow_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-31
ALTER TABLE friendship_entity ADD CONSTRAINT FKpq2at14h3gljwg848p43aw14w FOREIGN KEY (friend_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-32
ALTER TABLE user_email_entity ADD CONSTRAINT FKq1njl6uveplkpgu70115gbf5o FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1692169297263-33
ALTER TABLE organize_closure_entity ADD CONSTRAINT FKrq3vl8q12mdppvdre3bcv6fce FOREIGN KEY (descendant_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

