ALTER TABLE restaurant_subscription_tracker
    ADD expiration_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE user_subscription_tracker
    ADD expiration_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE restaurant_subscription
    ALTER COLUMN type SET NOT NULL;