DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS question_answer;
DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS question_category;
DROP TABLE IF EXISTS category;
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
    nickname                    VARCHAR(10)                 NOT NULL,
    email                       VARCHAR(100)                NOT NULL,
    image_url                   VARCHAR(500)                NOT NULL,
    introduction                VARCHAR(60),
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    CONSTRAINT uq_user_nickname UNIQUE (nickname),
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
    status                      VARCHAR(10)                 NOT NULL,
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
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    CONSTRAINT fk_user_provider_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE user_visit
(
    user_visit_id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    visitor_user_id             BIGINT                      NOT NULL,
    CONSTRAINT fk_user_visit_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_user_visit_visitor_user FOREIGN KEY (visitor_user_id) REFERENCES users(user_id)
);

CREATE TABLE follow
(
    follow_id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    follower_user_id            BIGINT                      NOT NULL,
    CONSTRAINT fk_follow_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_follow_follower_user FOREIGN KEY (follower_user_id) REFERENCES users(user_id)
);

CREATE TABLE question
(
    question_id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    text                        VARCHAR(60)                 NOT NULL,
    background_color            VARCHAR(6)                  NOT NULL,
    start_date_time             DATETIME(6),
    end_date_time               DATETIME(6),
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    user_id                     BIGINT                      NOT NULL,
    CONSTRAINT fk_question_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE category
(
    category_id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                        VARCHAR(10)                 NOT NULL,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    admin_id                    BIGINT                      NOT NULL,
    CONSTRAINT fk_question_admin FOREIGN KEY (admin_id) REFERENCES users(user_id)
);

CREATE TABLE question_category
(
    question_category_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    question_id                 BIGINT                      NOT NULL,
    category_id                 BIGINT                      NOT NULL,
    CONSTRAINT fk_question_category_question FOREIGN KEY (question_id) REFERENCES question(question_id),
    CONSTRAINT fk_question_category_category FOREIGN KEY (category_id) REFERENCES category(category_id)
);

CREATE TABLE answer
(
    answer_id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    text                        VARCHAR(280),
    image_url                   VARCHAR(500),
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL
);

CREATE TABLE question_answer
(
    question_answer_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    sender_id                   BIGINT                      NOT NULL,
    receiver_id                 BIGINT                      NOT NULL,
    question_id                 BIGINT                      NOT NULL,
    answer_id                   BIGINT,
    CONSTRAINT fk_question_answer_sender FOREIGN KEY (sender_id) REFERENCES users(user_id),
    CONSTRAINT fk_question_answer_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id),
    CONSTRAINT fk_question_answer_question FOREIGN KEY (question_id) REFERENCES question(question_id),
    CONSTRAINT fk_question_answer_answer FOREIGN KEY (answer_id) REFERENCES answer(answer_id)
);

CREATE TABLE notification
(
    notification_id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    type                        VARCHAR(30)                 NOT NULL,
    title                       VARCHAR(50)                 NOT NULL,
    context                     VARCHAR(100)                NOT NULL,
    link                        VARCHAR(500)                NOT NULL,
    status                      VARCHAR(10)                 NOT NULL,
    create_date_time            DATETIME(6)                 NOT NULL,
    update_date_time            DATETIME(6)                 NOT NULL,
    receiver_id                 BIGINT                      NOT NULL,
    sender_user_id              BIGINT,
    sender_admin_id             BIGINT,
    CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id),
    CONSTRAINT fk_notification_sender_user_id FOREIGN KEY (sender_user_id) REFERENCES users(user_id),
    CONSTRAINT fk_notification_sender_admin_id FOREIGN KEY (sender_admin_id) REFERENCES users(user_id)
)