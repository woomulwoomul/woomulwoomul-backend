DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS question_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS question_answer;
DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS user_visit;
DROP TABLE IF EXISTS user_provider;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS withdraw_user;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    username                    VARCHAR(30)                 NOT NULL,
    email                       VARCHAR(100)                NOT NULL,
    image_url                   VARCHAR(500)                NOT NULL,
    service_status              VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    CONSTRAINT uq_user_username UNIQUE (username),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE withdraw_user
(
    withdraw_user_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    CONSTRAINT fk_withdraw_user_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE user_role
(
    user_role_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    role                        VARCHAR(6)                  NOT NULL,
    service_status              VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE user_provider
(
    user_provider_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider                    VARCHAR(10)                 NOT NULL,
    provider_id                 VARCHAR(100)                NOT NULL,
    service_status              VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    CONSTRAINT fk_user_provider_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE user_visit
(
    user_visit                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    service_status              VARCHAR(10)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    visitor_user_id             BIGINT                      NOT NULL,
    CONSTRAINT fk_user_visit_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_user_visit_visitor_user FOREIGN KEY (visitor_user_id) REFERENCES users(user_id)
);

CREATE TABLE follow
(
    user_visit                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    service_status              VARCHAR(10)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    follower_user_id            BIGINT                      NOT NULL,
    CONSTRAINT fk_follow_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_follow_follower_user FOREIGN KEY (follower_user_id) REFERENCES users(user_id)
);
