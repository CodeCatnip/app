CREATE TABLE IF NOT EXISTS application_user
(
    id uuid UNIQUE,
    username varchar(255) UNIQUE,
    password varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    enabled boolean,
    primary key (id)
);