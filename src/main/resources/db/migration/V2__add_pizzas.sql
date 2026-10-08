-- Insert sample pizzas
INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Margherita', 'La classica con pomodoro, mozzarella e basilico','8.50');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Marinara', 'Pomodoro, aglio e origano','7.00');

INSERT INTO pizza (pizza_id, name, description,price)
VALUES ((SELECT nextval('pizza_id_seq')), 'Capricciosa', 'Pomodoro, mozzarella, carciofi, prosciutto cotto e olive','10.50');



