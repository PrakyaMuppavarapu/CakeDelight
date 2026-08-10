INSERT INTO ratings
(cake_id, rating, review, customer_name)
SELECT
    1,
    5,
    'Amazing chocolate cake! Rich and delicious.',
    'Ananya'
    WHERE NOT EXISTS (
    SELECT 1 FROM ratings
    WHERE cake_id = 1
      AND customer_name = 'Ananya'
);

INSERT INTO ratings
(cake_id, rating, review, customer_name)
SELECT
    2,
    4,
    'Very soft and tasty. Loved the vanilla flavor.',
    'Rahul'
    WHERE NOT EXISTS (
    SELECT 1 FROM ratings
    WHERE cake_id = 2
      AND customer_name = 'Rahul'
);

INSERT INTO ratings
(cake_id, rating, review, customer_name)
SELECT
    3,
    5,
    'The red velvet was excellent!',
    'Sneha'
    WHERE NOT EXISTS (
    SELECT 1 FROM ratings
    WHERE cake_id = 3
      AND customer_name = 'Sneha'
);

INSERT INTO ratings
(cake_id, rating, review, customer_name)
SELECT
    4,
    4,
    'Fresh and fruity. Really enjoyed it.',
    'Arjun'
    WHERE NOT EXISTS (
    SELECT 1 FROM ratings
    WHERE cake_id = 4
      AND customer_name = 'Arjun'
);

INSERT INTO ratings
(cake_id, rating, review, customer_name)
SELECT
    5,
    5,
    'Classic and delicious. Would order again.',
    'Meera'
    WHERE NOT EXISTS (
    SELECT 1 FROM ratings
    WHERE cake_id = 5
      AND customer_name = 'Meera'
);