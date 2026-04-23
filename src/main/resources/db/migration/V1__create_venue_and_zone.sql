CREATE TABLE venues (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE zones (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    capacity       INTEGER        NOT NULL,
    price_per_seat NUMERIC(10, 2) NOT NULL,
    venue_id       BIGINT         NOT NULL REFERENCES venues (id)
);
