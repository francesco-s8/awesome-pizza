ALTER TABLE pizza_order
    ADD "paid"  BOOLEAN DEFAULT FALSE,
    add "payment_method"   varchar(255),
    add "payment_date"     TIMESTAMP(6)   null,
    add "total"            numeric(10, 2) null;

