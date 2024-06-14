DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-dev' THEN
            INSERT INTO account (version, email, password, phone_number, full_name, status, role, id)
            VALUES (0, 'nicolestevenson@whateat.com', '$2a$10$qR0GfRzyP8.rJDAmrcUDt.YSQV9.3dSMtJNxN6WZKvwSMPDP5RIai',
                    '32344287708',
                    'Nicole Stevenson', 'ACTIVE', 'ADMIN', 10036952476966751),
                   (0, 'jeffklein@whateat.com', '$2a$10$MAVtKy2CBvDaqug7pjZLAOVLAmDFPB3ZUZTpq4hkUNDPRMtJj8eZG',
                    '71966728740',
                    'Jeff Klein', 'ACTIVE', 'ADMIN', 10036952476966752),
                   (0, 'keithgriffin@whateat.com', '$2a$10$q3Xh0A5prNqnkEic1CiaMOM6JPvF70k5tNCVSi5dlRQhU/iUAZQim',
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
