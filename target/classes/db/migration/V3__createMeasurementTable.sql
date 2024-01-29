CREATE TABLE IF NOT EXISTS measurement
(
    id uuid UNIQUE,
    application_user_id varchar(255) NOT NULL,
    time_in_seconds varchar(255) NOT NULL,
    type varchar(255) NOT NULL,
    measure_value_one varchar(255) NOT NULL,
    measure_value_two varchar(255) NOT NULL,
    primary key (id),
    FOREIGN KEY (application_user_id) REFERENCES application_user (id)
);
