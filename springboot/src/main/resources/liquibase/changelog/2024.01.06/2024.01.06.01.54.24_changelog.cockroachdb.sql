-- liquibase formatted sql

-- changeset John:1704506085226-1
CREATE TABLE "distributed_execution_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "name" VARCHAR(255) NOT NULL, "page_num" BIGINT NOT NULL, "page_size" BIGINT NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "version" VARCHAR(255) NOT NULL, CONSTRAINT "distributed_execution_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-2
CREATE TABLE "encrypt_decrypt_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "private_key_ofrsa" VARCHAR(1073741824) NOT NULL, "public_key_ofrsa" VARCHAR(1073741824) NOT NULL, "secret_key_ofaes" VARCHAR(1073741824) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT "encrypt_decrypt_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-3
CREATE TABLE "friendship_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "has_initiative" BOOLEAN NOT NULL, "is_friend" BOOLEAN NOT NULL, "is_in_blacklist" BOOLEAN NOT NULL, "secret_key_ofaes" VARCHAR(1073741824) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "friend_id" VARCHAR(255) NOT NULL, "user_id" VARCHAR(255) NOT NULL, CONSTRAINT "friendship_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-4
CREATE TABLE "logger_entity" ("id" VARCHAR(255) NOT NULL, "caller_class_name" VARCHAR(1073741824) NOT NULL, "caller_line_number" BIGINT NOT NULL, "caller_method_name" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "exception_class_name" VARCHAR(1073741824) NOT NULL, "exception_message" VARCHAR(1073741824) NOT NULL, "exception_stack_trace" VARCHAR(1073741824) NOT NULL, "git_commit_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "git_commit_id" VARCHAR(255) NOT NULL, "has_exception" BOOLEAN NOT NULL, "level" VARCHAR(255) NOT NULL, "logger_name" VARCHAR(1073741824) NOT NULL, "message" VARCHAR(1073741824) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT "logger_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-5
CREATE TABLE "long_term_task_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "is_done" BOOLEAN NOT NULL, "result" VARCHAR(1073741824), "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT "long_term_task_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-6
CREATE TABLE "organize_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "deleted_key" VARCHAR(255) NOT NULL, "is_deleted" BOOLEAN NOT NULL, "name" VARCHAR(255) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "parent_id" VARCHAR(255), CONSTRAINT "organize_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-7
CREATE TABLE "storage_space_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "folder_name" VARCHAR(255) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT "storage_space_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-8
CREATE TABLE "token_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "jwt_id" VARCHAR(255) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "user_id" VARCHAR(255) NOT NULL, CONSTRAINT "token_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-9
CREATE TABLE "user_email_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "deleted_key" VARCHAR(255) NOT NULL, "email" VARCHAR(512) NOT NULL, "is_deleted" BOOLEAN NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "user_id" VARCHAR(255) NOT NULL, CONSTRAINT "user_email_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-10
CREATE TABLE "user_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "is_deleted" BOOLEAN NOT NULL, "password" VARCHAR(1073741824) NOT NULL, "private_key_ofrsa" VARCHAR(1073741824) NOT NULL, "public_key_ofrsa" VARCHAR(1073741824) NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "username" VARCHAR(1073741824) NOT NULL, CONSTRAINT "user_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-11
CREATE TABLE "user_message_entity" ("id" VARCHAR(255) NOT NULL, "content" VARCHAR(1073741824) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "file_name" VARCHAR(1073741824), "folder_name" VARCHAR(255), "folder_size" BIGINT, "is_recall" BOOLEAN NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "user_id" VARCHAR(255) NOT NULL, CONSTRAINT "user_message_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-12
CREATE TABLE "verification_code_email_entity" ("id" VARCHAR(255) NOT NULL, "create_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "email" VARCHAR(512) NOT NULL, "has_used" BOOLEAN NOT NULL, "is_passed" BOOLEAN NOT NULL, "update_date" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "verification_code" VARCHAR(255) NOT NULL, CONSTRAINT "verification_code_email_entity_pkey" PRIMARY KEY ("id"));

-- changeset John:1704506085226-13
ALTER TABLE "organize_entity" ADD CONSTRAINT "uk5sbf5vxcdf70l5qsp050ortys" UNIQUE ("parent_id", "name", "deleted_key");

-- changeset John:1704506085226-14
ALTER TABLE "distributed_execution_entity" ADD CONSTRAINT "uk61qqq6cisvchp1mxmk0x63796" UNIQUE ("version", "page_size", "page_num");

-- changeset John:1704506085226-15
ALTER TABLE "organize_entity" ADD CONSTRAINT "ukpf18vv76hywqylixgarh74sj4" UNIQUE ("id", "is_deleted");

-- changeset John:1704506085226-16
ALTER TABLE "user_email_entity" ADD CONSTRAINT "ukqk35yh86v7c9xa0scp4p795be" UNIQUE ("email", "deleted_key");

-- changeset John:1704506085226-17
ALTER TABLE "friendship_entity" ADD CONSTRAINT "ukqsamcn1vehewm87nlwtjw2kfk" UNIQUE ("user_id", "friend_id");

-- changeset John:1704506085226-18
CREATE INDEX "idx46cw449tt4ikqgeepvj4u4d7a" ON "storage_space_entity"("folder_name");

-- changeset John:1704506085226-19
CREATE INDEX "idxc8rxy12jhfct95nxp7ckogevt" ON "user_entity"("id", "is_deleted");

-- changeset John:1704506085226-20
CREATE INDEX "idxg4isidkyw8pui6rnpe5ii9fdb" ON "user_message_entity"("folder_name");

-- changeset John:1704506085226-21
CREATE INDEX "idxh2tw4b9y7sentv7whtxxxnv3o" ON "user_email_entity"("email", "is_deleted");

-- changeset John:1704506085226-22
CREATE INDEX "idxji80sbcordi5yr2a9v2jr91av" ON "token_entity"("jwt_id");

-- changeset John:1704506085226-23
CREATE INDEX "idxnjdxugvq08k7142tgvracteqe" ON "user_email_entity"("id", "is_deleted");

-- changeset John:1704506085226-24
CREATE INDEX "idxoxeihgs43v794u9lhrh79ugm5" ON "verification_code_email_entity"("email", "create_date");

-- changeset John:1704506085226-25
ALTER TABLE "user_message_entity" ADD CONSTRAINT "fk23c715x0gcdv29x9l92r8dc74" FOREIGN KEY ("user_id") REFERENCES "user_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset John:1704506085226-26
ALTER TABLE "friendship_entity" ADD CONSTRAINT "fk4n7gua4wuvh9ymsen9pdt49v6" FOREIGN KEY ("user_id") REFERENCES "user_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset John:1704506085226-27
ALTER TABLE "token_entity" ADD CONSTRAINT "fkchycpasyr16kt66k09e6ompve" FOREIGN KEY ("user_id") REFERENCES "user_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset John:1704506085226-28
ALTER TABLE "organize_entity" ADD CONSTRAINT "fkg1i0kxqrixd8fdpw6me7x5t3q" FOREIGN KEY ("parent_id") REFERENCES "organize_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset John:1704506085226-29
ALTER TABLE "friendship_entity" ADD CONSTRAINT "fkpq2at14h3gljwg848p43aw14w" FOREIGN KEY ("friend_id") REFERENCES "user_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset John:1704506085226-30
ALTER TABLE "user_email_entity" ADD CONSTRAINT "fkq1njl6uveplkpgu70115gbf5o" FOREIGN KEY ("user_id") REFERENCES "user_entity" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

