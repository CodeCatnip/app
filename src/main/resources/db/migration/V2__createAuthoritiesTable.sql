CREATE TABLE IF NOT EXISTS authority
(
    id uuid UNIQUE,
    application_user_id varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    role varchar(255),
    primary key (id),
    FOREIGN KEY (application_user_id) REFERENCES application_user (id)
);