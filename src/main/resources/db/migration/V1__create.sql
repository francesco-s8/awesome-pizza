CREATE SEQUENCE IF NOT EXISTS pizza_order_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS pizza_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE pizza_order
(
    "pizza_order_id" int8         NOT NULL,
    "order_status"   varchar(255) NULL,
    "username"       varchar(255) NOT NULL,
    "version"        int4         NULL,
    "created_at"     TIMESTAMP(6) NULL,
    "modified_at"    TIMESTAMP(6) NULL,
    CONSTRAINT pizza_order_pkey PRIMARY KEY (pizza_order_id)
);

CREATE TABLE pizza
(
    pizza_id       int8         NOT NULL,
    pizza_order_id int8         NULL,
    description    varchar(255) NULL,
    "name"         varchar(255) NULL,
    "version"      INTEGER      DEFAULT 0,
    "created_at"   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    "modified_at"  TIMESTAMP(6) NULL,

    CONSTRAINT pizza_pkey PRIMARY KEY (pizza_id),
    CONSTRAINT pizza_order_fk FOREIGN KEY (pizza_order_id) REFERENCES pizza_order (pizza_order_id)
);

CREATE TABLE pizza_order_items
(
    pizza_order_id INT8 NOT NULL,
    pizza_id       INT8 NOT NULL,
    PRIMARY KEY (pizza_order_id, pizza_id),
    CONSTRAINT fk_pizza_order_items_order FOREIGN KEY (pizza_order_id) REFERENCES pizza_order (pizza_order_id),
    CONSTRAINT fk_pizza_order_items_pizza FOREIGN KEY (pizza_id) REFERENCES pizza (pizza_id)
);
