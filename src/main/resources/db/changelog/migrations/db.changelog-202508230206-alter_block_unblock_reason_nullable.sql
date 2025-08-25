--liquibase formatted sql

--changeset daniel:202508230206
-- Comment: Alter BLOCKS.unblock_reason to allow NULL
ALTER TABLE BLOCKS
MODIFY COLUMN unblock_reason VARCHAR(255) NULL;

--rollback ALTER TABLE BLOCKS
--rollback MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;