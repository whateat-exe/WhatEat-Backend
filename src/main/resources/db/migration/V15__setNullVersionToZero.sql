UPDATE food
SET version = 0
WHERE version IS NULL;

UPDATE tag
SET version = 0
WHERE version IS NULL;

UPDATE tag_category
SET version = 0
WHERE version IS NULL;

UPDATE food_tag
SET version = 0
WHERE version IS NULL;

UPDATE restaurant
SET version = 0
WHERE version IS NULL;