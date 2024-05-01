DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_provider;

CREATE TABLE user_provider
(
    user_provider_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider                    VARCHAR(10)             NOT NULL,
    provider_id                 VARCHAR(100)            NOT NULL,
    create_date_time            DATETIME(6)             NOT NULL,
    update_date_time            DATETIME(6)             NOT NULL
);

CREATE TABLE users
(
    user_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    username                    VARCHAR(15)             NOT NULL,
    image_url                   VARCHAR(500)            NOT NULL,
    visit_cnt                   INT                     NOT NULL,
    create_date_time            DATETIME(6)             NOT NULL,
    update_date_time            DATETIME(6)             NOT NULL,
    user_provider_id            BIGINT                  NOT NULL,
    CONSTRAINT uq_user_username UNIQUE (username),
    CONSTRAINT fk_user_user_provider FOREIGN KEY (user_provider_id) REFERENCES user_provider (user_provider_id)
);

CREATE TABLE user_role
(
    user_role_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    role                        VARCHAR(6)              NOT NULL,
    create_date_time            DATETIME(6)             NOT NULL,
    update_date_time            DATETIME(6)             NOT NULL,
    user_id                     BIGINT                  NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);