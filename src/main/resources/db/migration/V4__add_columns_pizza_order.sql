ALTER TABLE pizza_order
    add "payment_method" varchar(255),
    add "payment_date"   TIMESTAMP(6) null;