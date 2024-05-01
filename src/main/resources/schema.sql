DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    username                    VARCHAR(15)             NOT NULL,
    email                       VARCHAR(100),
    image_url                   VARCHAR(500)            NOT NULL,
    visit_cnt                   INT                     NOT NULL,
    birthdate                   DATE,
    create_date_time            DATETIME(6)             NOT NULL,
    update_date_time            DATETIME(6)             NOT NULL,
    CONSTRAINT uq_user_username UNIQUE (username)
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