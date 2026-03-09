CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
