ALTER TABLE rating
    DROP CONSTRAINT uc_rating_account;

ALTER TABLE rating
    ADD CONSTRAINT uc_dish_account UNIQUE (dish_id, account_id);