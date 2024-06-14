ALTER TABLE food
    DROP CONSTRAINT fk_food_on_parent_food;

ALTER TABLE food
    DROP COLUMN parent_food_id;