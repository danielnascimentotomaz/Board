--liquibase formatted sql

--changeset daniel:202508191050
--comment: Alter CARDS.description from INTEGER to VARCHAR(255)
ALTER TABLE CARDS
MODIFY COLUMN description VARCHAR(255) NOT NULL;

--rollback ALTER TABLE CARDS
--rollback MODIFY COLUMN description INT NOT NULL;