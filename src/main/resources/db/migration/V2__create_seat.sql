CREATE TABLE seats (
    id      BIGSERIAL PRIMARY KEY,
    number  INTEGER      NOT NULL,
    status  VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    zone_id BIGINT       NOT NULL REFERENCES zones (id)
);
