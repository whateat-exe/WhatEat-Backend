CREATE TABLE tag_category
(
    version  INTEGER,
    tag_id   BIGINT       NOT NULL,
    category VARCHAR(255) NOT NULL,
    id       BIGINT       NOT NULL,
    CONSTRAINT pk_tag_category PRIMARY KEY (id)
);

ALTER TABLE tag_category
    ADD CONSTRAINT UK_tag_id_category UNIQUE (tag_id, category);

ALTER TABLE account_verify
    ADD CONSTRAINT uc_account_verify_account UNIQUE (account_id);

ALTER TABLE tag_category
    ADD CONSTRAINT FK_TAG_CATEGORY_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (id);