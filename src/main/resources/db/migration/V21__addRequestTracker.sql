CREATE TABLE restaurant_request_tracker
(
    version                   INTEGER,
    max_number_of_create_dish INTEGER,
    number_requested_dish     INTEGER,
    validity_start            TIMESTAMP WITHOUT TIME ZONE,
    validity_end              TIMESTAMP WITHOUT TIME ZONE,
    status                    VARCHAR(255),
    id                        BIGINT NOT NULL,
    restaurant_id             BIGINT NOT NULL,
    CONSTRAINT pk_restaurant_request_tracker PRIMARY KEY (id)
);

ALTER TABLE restaurant_request_tracker
    ADD CONSTRAINT FK_RESTAURANT_REQUEST_TRACKER_ON_RESTAURANT FOREIGN KEY (restaurant_id) REFERENCES restaurant (id);