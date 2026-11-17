-- Insert sample pizzas.
INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Margherita', 'The classic with tomato, mozzarella, and basil','8.50');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Marinara', 'Tomato, garlic, and oregano','7.00');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Capricciosa', 'Tomato, mozzarella, artichokes, cooked ham, and olives','10.50');

INSERT INTO ingredients (ingredient_id, name, version)
SELECT nextval('ingredient_id_seq'), seed.ingredient_name, 0
FROM (VALUES
    ('Tomato'),
    ('Mozzarella'),
    ('Basil'),
    ('Garlic'),
    ('Oregano'),
    ('Artichokes'),
    ('Cooked Ham'),
    ('Olives')
) AS seed(ingredient_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM ingredients existing
    WHERE existing.name = seed.ingredient_name
);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.pizza_id, i.ingredient_id
FROM (VALUES
    ('Margherita', 'Tomato'),
    ('Margherita', 'Mozzarella'),
    ('Margherita', 'Basil'),
    ('Marinara', 'Tomato'),
    ('Marinara', 'Garlic'),
    ('Marinara', 'Oregano'),
    ('Capricciosa', 'Tomato'),
    ('Capricciosa', 'Mozzarella'),
    ('Capricciosa', 'Artichokes'),
    ('Capricciosa', 'Cooked Ham'),
    ('Capricciosa', 'Olives')
) AS seed(pizza_name, ingredient_name)
JOIN pizza p ON p.name = seed.pizza_name
JOIN ingredients i ON i.name = seed.ingredient_name
WHERE NOT EXISTS (
    SELECT 1
    FROM pizza_ingredients existing
    WHERE existing.pizza_id = p.pizza_id
      AND existing.ingredient_id = i.ingredient_id
);
