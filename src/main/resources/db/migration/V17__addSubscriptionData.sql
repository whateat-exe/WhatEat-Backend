DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-dev' THEN
            INSERT INTO restaurant_subscription (id, name, description, type, status, price, version, duration)
            VALUES ('8250017681003040', 'Gói Bạc', 'Đăng 10 món miễn phí và 5 bài viết/ngày', 'SILVER', 'ACTIVE', 2000,
                    0, 30),
                   ('8250017681003041', 'Gói Vàng', 'Đăng 30 món miễn phí và 10 bài viết/ngày', 'GOLD', 'ACTIVE', 2000,
                    0, 30),
                   ('8250017681003042', 'Gói Kim cương', 'Đăng 50 món miễn phí và 20 bài viết/ngày', 'DIAMOND',
                    'ACTIVE', 2000, 0, 30);

            INSERT INTO user_subscription (id, name, description, status, price, version, duration)
            VALUES ('8250017681003043', 'Gói VIP', 'Sử dụng chức năng filter - bộ lọc tuỳ biến', 'ACTIVE', 2000,
                    0, 30);
        ELSIF current_db_name = 'whateat-prod' THEN
            INSERT INTO restaurant_subscription (id, name, description, type, status, price, version, duration)
            VALUES ('8250017681003040', 'Gói Bạc', 'Đăng 10 món miễn phí và 5 bài viết/ngày', 'SILVER', 'ACTIVE', 99000,
                    0, 30),
                   ('8250017681003041', 'Gói Vàng', 'Đăng 30 món miễn phí và 10 bài viết/ngày', 'GOLD', 'ACTIVE',
                    199000, 0, 30),
                   ('8250017681003042', 'Gói Kim cương', 'Đăng 50 món miễn phí và 20 bài viết/ngày', 'DIAMOND',
                    'ACTIVE', 299000, 0, 30);

            INSERT INTO user_subscription (id, name, description, status, price, version, duration)
            VALUES ('8250017681003043', 'Gói VIP', 'Sử dụng chức năng filter - bộ lọc tuỳ biến', 'ACTIVE', 29000,
                    0, 30);
        END IF;
    END;
$$;