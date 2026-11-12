CREATE SEQUENCE IF NOT EXISTS ingredient_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE ingredients
(
    ingredient_id int8         NOT NULL,
    "name"        varchar(255) NULL,
    "version"     int4 NOT NULL,
    "created_at"  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "modified_at" TIMESTAMP(6) WITH TIME ZONE NULL,
    CONSTRAINT ingredients_pkey PRIMARY KEY (ingredient_id)
);

CREATE TABLE pizza_ingredients
(
    pizza_id      int8 NOT NULL,
    ingredient_id int8 NOT NULL,
    PRIMARY KEY (pizza_id, ingredient_id),
    CONSTRAINT fk_pizza_ingredients_pizza
        FOREIGN KEY (pizza_id) REFERENCES pizza (pizza_id),
    CONSTRAINT fk_pizza_ingredients_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id)
);
