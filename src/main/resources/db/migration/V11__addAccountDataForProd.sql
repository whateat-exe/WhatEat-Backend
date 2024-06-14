DO
$$
    DECLARE
        current_db_name TEXT;
    BEGIN
        current_db_name := current_database();
        IF current_db_name = 'whateat-prod' THEN
            INSERT INTO account (version, email, password, phone_number, full_name, status, role, id)
            VALUES (0, 'nicolestevenson@whateat.com', '$2a$10$jo777I2T5HlPBiSnyrcFmeQa0jsHKZgUe82tr0yW.HICSZFANMWy.',
                    '32344287708',
                    'Nicole Stevenson', 'ACTIVE', 'ADMIN', 10036952476966751),
                   (0, 'jeffklein@whateat.com', '$2a$10$UL1Us5UBregS9J0FkysOg.XWjt03Q46WKpt3gRAps.EpghumiNfQa',
                    '71966728740',
                    'Jeff Klein', 'ACTIVE', 'ADMIN', 10036952476966752);
        END IF;
    END;
$$;