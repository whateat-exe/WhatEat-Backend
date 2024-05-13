CREATE TABLE account
(
    version       INTEGER,
    email         VARCHAR(255),
    password      VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    image         VARCHAR(255),
    status        VARCHAR(255) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    id            BIGINT       NOT NULL,
    restaurant_id BIGINT,
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE dish
(
    version       INTEGER,
    name          VARCHAR(255),
    description   VARCHAR(1000) NOT NULL,
    image         VARCHAR(255)  NOT NULL,
    status        VARCHAR(255)  NOT NULL,
    food_id       BIGINT        NOT NULL,
    restaurant_id BIGINT        NOT NULL,
    id            BIGINT        NOT NULL,
    price         DECIMAL(19)   NOT NULL,
    CONSTRAINT pk_dish PRIMARY KEY (id)
);

CREATE TABLE food
(
    version        INTEGER,
    name           VARCHAR(255) NOT NULL,
    image          VARCHAR(255) NOT NULL,
    parent_food_id BIGINT,
    status         VARCHAR(255) NOT NULL,
    id             BIGINT       NOT NULL,
    CONSTRAINT pk_food PRIMARY KEY (id)
);

CREATE TABLE food_tag
(
    version INTEGER,
    food_id BIGINT       NOT NULL,
    tag_id  BIGINT       NOT NULL,
    status  VARCHAR(255) NOT NULL,
    id      BIGINT       NOT NULL,
    CONSTRAINT pk_food_tag PRIMARY KEY (id)
);

CREATE TABLE personal_profile
(
    version    INTEGER,
    account_id BIGINT       NOT NULL,
    tag_id     BIGINT       NOT NULL,
    type       VARCHAR(255) NOT NULL,
    id         BIGINT       NOT NULL,
    CONSTRAINT pk_personal_profile PRIMARY KEY (id)
);

CREATE TABLE post
(
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified TIMESTAMP WITHOUT TIME ZONE,
    version       INTEGER,
    title         VARCHAR(255)                NOT NULL,
    content       VARCHAR(5000)               NOT NULL,
    account_id    BIGINT                      NOT NULL,
    id            BIGINT                      NOT NULL,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

CREATE TABLE post_comment
(
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified TIMESTAMP WITHOUT TIME ZONE,
    version       INTEGER,
    content       VARCHAR(255)                NOT NULL,
    post_id       BIGINT                      NOT NULL,
    account_id    BIGINT                      NOT NULL,
    id            BIGINT                      NOT NULL,
    CONSTRAINT pk_post_comment PRIMARY KEY (id)
);

CREATE TABLE post_image
(
    version INTEGER,
    caption VARCHAR(255) NOT NULL,
    image   VARCHAR(255) NOT NULL,
    post_id BIGINT       NOT NULL,
    id      BIGINT       NOT NULL,
    CONSTRAINT pk_post_image PRIMARY KEY (id)
);

CREATE TABLE post_voting
(
    version    INTEGER,
    type       VARCHAR(255) NOT NULL,
    post_id    BIGINT       NOT NULL,
    account_id BIGINT       NOT NULL,
    id         BIGINT       NOT NULL,
    CONSTRAINT pk_post_voting PRIMARY KEY (id)
);

CREATE TABLE random_history
(
    version    INTEGER,
    account_id BIGINT                      NOT NULL,
    food_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id         BIGINT                      NOT NULL,
    CONSTRAINT pk_random_history PRIMARY KEY (id)
);

CREATE TABLE random_history_dish
(
    version           INTEGER,
    random_history_id BIGINT NOT NULL,
    dish_id           BIGINT NOT NULL,
    id                BIGINT NOT NULL,
    CONSTRAINT pk_random_history_dish PRIMARY KEY (id)
);

CREATE TABLE rating
(
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified TIMESTAMP WITHOUT TIME ZONE,
    version       INTEGER,
    dish_id       BIGINT,
    account_id    BIGINT,
    stars         INTEGER                     NOT NULL,
    title         VARCHAR(255)                NOT NULL,
    feedback      VARCHAR(1000)               NOT NULL,
    id            BIGINT                      NOT NULL,
    CONSTRAINT pk_rating PRIMARY KEY (id)
);

CREATE TABLE restaurant
(
    version     INTEGER,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(5000) NOT NULL,
    image       VARCHAR(255)  NOT NULL,
    status      VARCHAR(10)   NOT NULL,
    account_id  BIGINT        NOT NULL,
    address     VARCHAR(255)  NOT NULL,
    id          BIGINT        NOT NULL,
    CONSTRAINT pk_restaurant PRIMARY KEY (id)
);

CREATE TABLE restaurant_request
(
    version                        INTEGER,
    title                          VARCHAR(255)                NOT NULL,
    content                        VARCHAR(5000)               NOT NULL,
    type                           VARCHAR(255)                NOT NULL,
    restaurant_id                  BIGINT                      NOT NULL,
    created_at                     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id                             BIGINT                      NOT NULL,
    restaurant_request_response_id BIGINT,
    CONSTRAINT pk_restaurant_request PRIMARY KEY (id)
);

CREATE TABLE restaurant_response
(
    version               INTEGER,
    title                 VARCHAR(255)                NOT NULL,
    content               VARCHAR(5000)               NOT NULL,
    status                VARCHAR(255)                NOT NULL,
    restaurant_request_id BIGINT                      NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id                    BIGINT                      NOT NULL,
    CONSTRAINT pk_restaurant_response PRIMARY KEY (id)
);

CREATE TABLE restaurant_subscription
(
    version         INTEGER,
    subcription_id  BIGINT                      NOT NULL,
    restaurant_id   BIGINT                      NOT NULL,
    status          VARCHAR(255)                NOT NULL,
    activation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITHOUT TIME ZONE,
    id              BIGINT                      NOT NULL,
    CONSTRAINT pk_restaurant_subscription PRIMARY KEY (id)
);

CREATE TABLE restaurant_subscription_payment
(
    version                    INTEGER,
    status                     VARCHAR(255)                NOT NULL,
    created_at                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    payment_time               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    restaurant_subscription_id BIGINT,
    transaction_history_id     BIGINT,
    id                         BIGINT                      NOT NULL,
    CONSTRAINT pk_restaurant_subscription_payment PRIMARY KEY (id)
);

CREATE TABLE subscription
(
    version     INTEGER,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(5000) NOT NULL,
    type        VARCHAR(255)  NOT NULL,
    duration    INTEGER       NOT NULL,
    status      VARCHAR(255)  NOT NULL,
    id          BIGINT        NOT NULL,
    price       DECIMAL(19)   NOT NULL,
    CONSTRAINT pk_subscription PRIMARY KEY (id)
);

CREATE TABLE tag
(
    version INTEGER,
    name    VARCHAR(255) NOT NULL,
    type    VARCHAR(255) NOT NULL,
    id      BIGINT       NOT NULL,
    CONSTRAINT pk_tag PRIMARY KEY (id)
);

CREATE TABLE trasaction_history
(
    version                            INTEGER,
    type                               VARCHAR(255)                NOT NULL,
    status                             VARCHAR(255)                NOT NULL,
    created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id                                 BIGINT                      NOT NULL,
    paid_amount                        DECIMAL                     NOT NULL,
    restaurant_subscription_payment_id BIGINT,
    CONSTRAINT pk_trasaction_history PRIMARY KEY (id)
);

ALTER TABLE account
    ADD CONSTRAINT uc_account_email UNIQUE (email);

ALTER TABLE account
    ADD CONSTRAINT uc_account_password UNIQUE (password);

ALTER TABLE food
    ADD CONSTRAINT uc_food_name UNIQUE (name);

ALTER TABLE rating
    ADD CONSTRAINT uc_rating_account UNIQUE (account_id);

ALTER TABLE restaurant
    ADD CONSTRAINT uc_restaurant_account UNIQUE (account_id);

ALTER TABLE restaurant_response
    ADD CONSTRAINT uc_restaurant_response_restaurant_request UNIQUE (restaurant_request_id);

ALTER TABLE restaurant_subscription_payment
    ADD CONSTRAINT uc_restaurant_subscription_payment_transaction_history UNIQUE (transaction_history_id);

ALTER TABLE account
    ADD CONSTRAINT FK_ACCOUNT_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);

ALTER TABLE dish
    ADD CONSTRAINT FK_DISH_ON_FOOD FOREIGN KEY (food_id) REFERENCES food (id);

ALTER TABLE dish
    ADD CONSTRAINT FK_DISH_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);

ALTER TABLE food
    ADD CONSTRAINT FK_FOOD_ON_PARENT_FOOD FOREIGN KEY (parent_food_id) REFERENCES food (id);

ALTER TABLE food_tag
    ADD CONSTRAINT FK_FOOD_TAG_ON_FOOD FOREIGN KEY (food_id) REFERENCES food (id);

ALTER TABLE food_tag
    ADD CONSTRAINT FK_FOOD_TAG_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (id);

ALTER TABLE personal_profile
    ADD CONSTRAINT FK_PERSONAL_PROFILE_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE personal_profile
    ADD CONSTRAINT FK_PERSONAL_PROFILE_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (id);

ALTER TABLE post_comment
    ADD CONSTRAINT FK_POST_COMMENT_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE post_comment
    ADD CONSTRAINT FK_POST_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE post_image
    ADD CONSTRAINT FK_POST_IMAGE_ON_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE post
    ADD CONSTRAINT FK_POST_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE post_voting
    ADD CONSTRAINT FK_POST_VOTING_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE post_voting
    ADD CONSTRAINT FK_POST_VOTING_ON_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE random_history_dish
    ADD CONSTRAINT FK_RANDOM_HISTORY_DISH_ON_DISH FOREIGN KEY (dish_id) REFERENCES dish (id);

ALTER TABLE random_history_dish
    ADD CONSTRAINT FK_RANDOM_HISTORY_DISH_ON_RANDOM_HISTORY FOREIGN KEY (random_history_id) REFERENCES random_history (id);

ALTER TABLE random_history
    ADD CONSTRAINT FK_RANDOM_HISTORY_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE random_history
    ADD CONSTRAINT FK_RANDOM_HISTORY_ON_FOOD FOREIGN KEY (food_id) REFERENCES food (id);

ALTER TABLE rating
    ADD CONSTRAINT FK_RATING_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE rating
    ADD CONSTRAINT FK_RATING_ON_DISH FOREIGN KEY (dish_id) REFERENCES dish (id);

ALTER TABLE restaurant
    ADD CONSTRAINT FK_RESTAURANT_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE restaurant_request
    ADD CONSTRAINT FK_RESTAURANT_REQUEST_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);

ALTER TABLE restaurant_request
    ADD CONSTRAINT FK_RESTAURANT_REQUEST_ON_RESTAURANTREQUESTRESPONSE FOREIGN KEY (restaurant_request_response_id) REFERENCES restaurant_response (id);

ALTER TABLE restaurant_response
    ADD CONSTRAINT FK_RESTAURANT_RESPONSE_ON_RESTAURANT_REQUEST FOREIGN KEY (restaurant_request_id) REFERENCES restaurant_request (id);

ALTER TABLE restaurant_subscription
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);

ALTER TABLE restaurant_subscription
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_ON_SUBCRIPTION FOREIGN KEY (subcription_id) REFERENCES subscription (id);

ALTER TABLE restaurant_subscription_payment
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_PAYMENT_ON_RESTAURANT_SUBSCRIPTION FOREIGN KEY (restaurant_subscription_id) REFERENCES restaurant_subscription (id);

ALTER TABLE restaurant_subscription_payment
    ADD CONSTRAINT FK_RESTAURANT_SUBSCRIPTION_PAYMENT_ON_TRANSACTION_HISTORY FOREIGN KEY (transaction_history_id) REFERENCES trasaction_history (id);

ALTER TABLE trasaction_history
    ADD CONSTRAINT FK_TRASACTION_HISTORY_ON_RESTAURANTSUBSCRIPTIONPAYMENT FOREIGN KEY (restaurant_subscription_payment_id) REFERENCES restaurant_subscription_payment (id);