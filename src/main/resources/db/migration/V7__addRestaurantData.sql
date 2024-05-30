DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-dev' THEN
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
