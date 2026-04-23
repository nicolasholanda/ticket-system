CREATE TABLE tickets (
    id           BIGSERIAL PRIMARY KEY,
    show_id      BIGINT         NOT NULL REFERENCES shows (id),
    seat_id      BIGINT         NOT NULL REFERENCES seats (id),
    price        NUMERIC(10, 2) NOT NULL,
    buyer_email  VARCHAR(255)   NOT NULL,
    purchased_at TIMESTAMP      NOT NULL
);
