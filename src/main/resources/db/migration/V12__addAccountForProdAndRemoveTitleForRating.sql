DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-prod' THEN
            INSERT INTO account (version, email, password, phone_number, full_name, status, role, id)
            VALUES (0, 'keithgriffin@whateat.com', '$2a$10$q3Xh0A5prNqnkEic1CiaMOM6JPvF70k5tNCVSi5dlRQhU/iUAZQim',
                    '68670851607',
                    'Keith Griffin', 'ACTIVE', 'MANAGER', 10036952476966753),
                   (0, 'andrewkim@whateat.com', '$2a$10$mOU1YYp5h4S0eikcIuwXZe0VVrxGvk.h64WT8es3Z3.eeCcF6kZXK',
                    '97397290119',
                    'Andrew Kim', 'ACTIVE', 'MANAGER', 10036952476966754),
                   (0, 'edgarpark@whateat.com', '$2a$10$SqHSLAUHvmGg/4LSTO3H7uAbjoYZhNhSVJKX3MrhZM1yfk329pR/.',
                    '11146469687',
                    'Edgar Park', 'ACTIVE', 'USER', 10036952476966755),
                   (0, 'shirleybrown@whateat.com', '$2a$10$OUWUaG6LVGt3Z4BhZzqS6.lLppbsDlBNDsWy4R5gf/6r7eotRrBF2',
                    '98078330745',
                    'Shirley Brown', 'ACTIVE', 'USER', 10036952476966756),
                   (0, 'richardwalker@whateat.com', '$2a$10$NxnZixaRZLgzk8K4MHXexeHt0X4IouJaXQZVumloou8vNnyLn61BC',
                    '64295278219',
                    'Richard Walker', 'ACTIVE', 'RESTAURANT', 10036952476966757),
                   (0, 'mr.ryanhaynes@whateat.com', '$2a$10$wEZnouD47JT20ZwuRfs/peBD5Imw.Qv2Y9vP5C0juX833lEwCRygK',
                    '25039563999',
                    'Mr. Ryan Haynes', 'ACTIVE', 'RESTAURANT', 10036952476966758);
        END IF;
    END;
$$;

ALTER TABLE rating
    DROP COLUMN title;

DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-prod' THEN
            INSERT INTO restaurant (id, name, description, image, account_id, address)
            VALUES ('10043100038975614', 'Bella Italia',
                    'Nằm giữa trung tâm thành phố, Bella Italia mang đến một không gian quyến rũ với hương vị mộc mạc của nước Ý. Thực đơn của chúng tôi bao gồm nhiều món ăn truyền thống, mỗi món được chế biến từ nguyên liệu tươi ngon, nguồn gốc địa phương và được làm ra với tất cả tình yêu thương. Từ pizza ném bằng tay đến pasta mềm mịn và một lựa chọn các loại rượu vang tuyệt hảo, Bella Italia hứa hẹn mang đến một trải nghiệm ẩm thực Ý chân thực.',
                    'https://firebasestorage.googleapis.com/v0/b/whateat-9d316.appspot.com/o/static%2Fbella-italia.jpg?alt=media',
                    '10036952476966757', 'Lê Duẩn, Bến Nghé, Quận 1, Hồ Chí Minh, Việt Nam'),
                   ('10043100038975615', 'Green Bamboo',
                    'Green Bamboo phục vụ sự kết hợp giữa ẩm thực Á châu truyền thống và hiện đại trong một không gian thanh bình, hiện đại. Chuyên về các món ăn Thái và Việt Nam, chúng tôi mang đến hương vị rực rỡ của Đông Nam Á lên bàn ăn của bạn. Thưởng thức một hành trình ẩm thực với phở đặc trưng, gỏi cuốn tươi ngon, và các món cà ri cay, tất cả được chế biến từ nguyên liệu tươi nhất và gia vị đậm đà.',
                    'https://firebasestorage.googleapis.com/v0/b/whateat-9d316.appspot.com/o/static%2Fgreen-bamboo.jpg?alt=media',
                    '10036952476966758', 'Nguyễn Huệ, Bến Thành, Quận 1, Hồ Chí Minh, Việt Nam');
        END IF;
    END;
$$;
