CREATE TABLE todos
(
    id        BIGSERIAL    NOT NULL,
    user_id   VARCHAR(255) NOT NULL,
    title     VARCHAR(255) NOT NULL,
    done      BOOLEAN      NOT NULL,
    PRIMARY KEY (id)
);
