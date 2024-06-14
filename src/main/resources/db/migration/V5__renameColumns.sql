ALTER TABLE account_verify
    DROP COLUMN last_mofified;

ALTER TABLE account_verify
    DROP COLUMN create_at;

ALTER TABLE account_verify
    RENAME COLUMN verify_code TO verification_code;

ALTER TABLE account_verify
    ADD status VARCHAR(255);

ALTER TABLE account_verify
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE account
    DROP CONSTRAINT fk_account_on_accountverify;

ALTER TABLE account
    DROP COLUMN account_verify_id;