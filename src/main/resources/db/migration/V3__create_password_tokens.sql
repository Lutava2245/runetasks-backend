CREATE TABLE password_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_users_token FOREIGN KEY (user_id) REFERENCES users(id)
)