ALTER TABLE restaurant_subscription
    DROP CONSTRAINT fk_restaurant_subscription_on_restaurant;

ALTER TABLE restaurant_subscription
    DROP CONSTRAINT fk_restaurant_subscription_on_subcription;

ALTER TABLE restaurant_subscription_payment
    DROP CONSTRAINT fk_restaurant_subscription_payment_on_restaurant_subscription;

ALTER TABLE restaurant_subscription_payment
    DROP CONSTRAINT fk_restaurant_subscription_payment_on_transaction_history;

ALTER TABLE trasaction_history
    DROP CONSTRAINT fk_trasaction_history_on_restaurantsubscriptionpayment;

CREATE TABLE restaurant_subscription_tracker
(
    version             INTEGER,
    restaurant_id       BIGINT         NOT NULL,
    subscription_id     BIGINT         NOT NULL,
    provider            VARCHAR(255)   NOT NULL,
    payment_id          VARCHAR(255)   NOT NULL,
    order_code          INTEGER        NOT NULL,
    payment_status      VARCHAR(255)   NOT NULL,
    signature           VARCHAR(512)   NOT NULL,
    validity_start      TIMESTAMP WITHOUT TIME ZONE,
    validity_end        TIMESTAMP WITHOUT TIME ZONE,
    subscription_status VARCHAR(255),
    id                  BIGINT         NOT NULL,
    amount              DECIMAL(19, 3) NOT NULL,
    CONSTRAINT pk_restaurant_subscription_tracker PRIMARY KEY (id)
);

CREATE TABLE user_subscription
(
    version     INTEGER,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    status      VARCHAR(255)   NOT NULL,
    duration    INTEGER        NOT NULL,
    id          BIGINT         NOT NULL,
    price       DECIMAL(19, 3) NOT NULL,
    CONSTRAINT pk_user_subscription PRIMARY KEY (id)
);

CREATE TABLE user_subscription_tracker
(
    version             INTEGER,
    user_id             BIGINT         NOT NULL,
    subscription_id     BIGINT         NOT NULL,
    provider            VARCHAR(255)   NOT NULL,
    payment_id          VARCHAR(255)   NOT NULL,
    order_code          INTEGER        NOT NULL,
    payment_status      VARCHAR(255)   NOT NULL,
    signature           VARCHAR(512)   NOT NULL,
    validity_start      TIMESTAMP WITHOUT TIME ZONE,
    validity_end        TIMESTAMP WITHOUT TIME ZONE,
    subscription_status VARCHAR(255),
    id                  BIGINT         NOT NULL,
    amount              DECIMAL(19, 3) NOT NULL,
    CONSTRAINT pk_user_subscription_tracker PRIMARY KEY (id)
);

ALTER TABLE restaurant_subscription
    ADD description VARCHAR(255);

ALTER TABLE restaurant_subscription
    ADD type VARCHAR(255);

ALTER TABLE restaurant_subscription
    ADD duration INTEGER;

ALTER TABLE restaurant_subscription
    ADD name VARCHAR(255);

ALTER TABLE restaurant_subscription
    ADD price DECIMAL(19, 3);

ALTER TABLE restaurant_subscription
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE restaurant_subscription
    ALTER COLUMN duration SET NOT NULL;

ALTER TABLE restaurant_subscription
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE restaurant_subscription
    ALTER COLUMN price SET NOT NULL;

ALTER TABLE restaurant_subscription
    ADD CONSTRAINT uc_restaurant_subscription_name UNIQUE (name);

ALTER TABLE user_subscription
    ADD CONSTRAINT uc_user_subscription_name UNIQUE (name);

ALTER TABLE restaurant_subscription_tracker
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_TRACKER_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);

ALTER TABLE restaurant_subscription_tracker
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_TRACKER_ON_SUBSCRIPTION FOREIGN KEY (subscription_id) REFERENCES restaurant_subscription (id);

ALTER TABLE user_subscription_tracker
    ADD CONSTRAINT FK_USER_SUBSCRIPTION_TRACKER_ON_SUBSCRIPTION FOREIGN KEY (subscription_id) REFERENCES user_subscription (id);

ALTER TABLE user_subscription_tracker
    ADD CONSTRAINT FK_USER_SUBSCRIPTION_TRACKER_ON_USER FOREIGN KEY (user_id) REFERENCES account (id);

DROP TABLE restaurant_subscription_payment CASCADE;

DROP TABLE subscription CASCADE;

DROP TABLE trasaction_history CASCADE;

ALTER TABLE restaurant_subscription
    DROP COLUMN activation_time;

ALTER TABLE restaurant_subscription
    DROP COLUMN end_time;

ALTER TABLE restaurant_subscription
    DROP COLUMN restaurant_id;

ALTER TABLE restaurant_subscription
    DROP COLUMN subcription_id;