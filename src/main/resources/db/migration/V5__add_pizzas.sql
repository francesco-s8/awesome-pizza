-- Insert sample pizzas
INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Margherita', 'La classica con pomodoro, mozzarella e basilico','8.50');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Marinara', 'Pomodoro, aglio e origano','7.00');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Capricciosa', 'Pomodoro, mozzarella, carciofi, prosciutto cotto e olive','10.50');

INSERT INTO ingredients (ingredient_id, name, version)
SELECT nextval('ingredient_id_seq'), seed.ingredient_name, 0
FROM (VALUES
    ('Pomodoro'),
    ('Mozzarella'),
    ('Basilico'),
    ('Aglio'),
    ('Origano'),
    ('Carciofi'),
    ('Prosciutto cotto'),
    ('Olive')
) AS seed(ingredient_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM ingredients existing
    WHERE existing.name = seed.ingredient_name
);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.pizza_id, i.ingredient_id
FROM (VALUES
    ('Margherita', 'Pomodoro'),
    ('Margherita', 'Mozzarella'),
    ('Margherita', 'Basilico'),
    ('Marinara', 'Pomodoro'),
    ('Marinara', 'Aglio'),
    ('Marinara', 'Origano'),
    ('Capricciosa', 'Pomodoro'),
    ('Capricciosa', 'Mozzarella'),
    ('Capricciosa', 'Carciofi'),
    ('Capricciosa', 'Prosciutto cotto'),
    ('Capricciosa', 'Olive')
) AS seed(pizza_name, ingredient_name)
JOIN pizza p ON p.name = seed.pizza_name
JOIN ingredients i ON i.name = seed.ingredient_name
WHERE NOT EXISTS (
    SELECT 1
    FROM pizza_ingredients existing
    WHERE existing.pizza_id = p.pizza_id
      AND existing.ingredient_id = i.ingredient_id
);
