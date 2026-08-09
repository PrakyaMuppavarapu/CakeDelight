INSERT INTO cakes
(name, description, category, price, available, image_url)
SELECT
    'Chocolate Truffle',
    'Rich chocolate cake',
    'Chocolate',
    799.99,
    true,
    'chocolate.jpg'
    WHERE NOT EXISTS (
    SELECT 1 FROM cakes WHERE name = 'Chocolate Truffle'
);

INSERT INTO cakes
(name, description, category, price, available, image_url)
SELECT
    'Vanilla Dream',
    'Soft and creamy vanilla cake',
    'Vanilla',
    699.99,
    true,
    'vanilla.jpg'
    WHERE NOT EXISTS (
    SELECT 1 FROM cakes WHERE name = 'Vanilla Dream'
);

INSERT INTO cakes
(name, description, category, price, available, image_url)
SELECT
    'Red Velvet',
    'Classic red velvet cake with cream cheese frosting',
    'Red Velvet',
    849.99,
    true,
    'red-velvet.jpg'
    WHERE NOT EXISTS (
    SELECT 1 FROM cakes WHERE name = 'Red Velvet'
);

INSERT INTO cakes
(name, description, category, price, available, image_url)
SELECT
    'Strawberry Delight',
    'Fresh strawberry cake with creamy frosting',
    'Fruit',
    899.99,
    true,
    'strawberry.jpg'
    WHERE NOT EXISTS (
    SELECT 1 FROM cakes WHERE name = 'Strawberry Delight'
);

INSERT INTO cakes
(name, description, category, price, available, image_url)
SELECT
    'Black Forest',
    'Classic black forest cake with chocolate and cherries',
    'Chocolate',
    749.99,
    true,
    'black-forest.jpg'
    WHERE NOT EXISTS (
    SELECT 1 FROM cakes WHERE name = 'Black Forest'
);