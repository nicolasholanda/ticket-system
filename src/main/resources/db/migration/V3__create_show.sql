CREATE TABLE shows (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    date     TIMESTAMP    NOT NULL,
    venue_id BIGINT       NOT NULL REFERENCES venues (id)
);
