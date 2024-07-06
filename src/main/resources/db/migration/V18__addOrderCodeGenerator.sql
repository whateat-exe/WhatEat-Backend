CREATE OR REPLACE FUNCTION generate_restaurant_order_code()
    RETURNS INT AS
$$
DECLARE
    random_order_code INT;
BEGIN
    LOOP
        random_order_code := floor(random() * 1000000)::int;
        IF NOT EXISTS (SELECT 1 FROM restaurant_subscription_tracker WHERE order_code = random_order_code) THEN
            RETURN random_order_code;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_user_order_code()
    RETURNS INT AS
$$
DECLARE
    random_order_code INT;
BEGIN
    LOOP
        random_order_code := floor(random() * 1000000)::int;
        IF NOT EXISTS (SELECT 1 FROM user_subscription_tracker WHERE order_code = random_order_code) THEN
            RETURN random_order_code;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;