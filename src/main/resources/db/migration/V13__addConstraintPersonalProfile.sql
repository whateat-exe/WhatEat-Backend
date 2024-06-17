ALTER TABLE personal_profile
    ADD CONSTRAINT uc_account_tag UNIQUE (account_id, tag_id);