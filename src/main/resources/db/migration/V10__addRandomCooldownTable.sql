CREATE TABLE random_cooldown
(
    version    INTEGER,
    id         BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT pk_randomcooldown PRIMARY KEY (id)
);

ALTER TABLE random_cooldown
    ADD CONSTRAINT FK_RANDOMCOOLDOWN_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);