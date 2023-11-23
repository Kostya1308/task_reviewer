--liquibase formatted sql

--changeset knpiskunov:create_correct_result_table
CREATE TABLE IF NOT EXISTS correct_result
(
    correct_result_id   serial PRIMARY KEY,
    result_path         varchar(100) NOT NULL
);

--changeset knpiskunov:create_correct_results_sequence
CREATE SEQUENCE correct_result_is_seq START WITH 1 INCREMENT BY 1;

--changeset knpiskunov:create_command_line_table
CREATE TABLE IF NOT EXISTS command_line
(
    command_line_id   serial PRIMARY KEY,
    command_line_args         varchar(100) NOT NULL,
    correct_result_id integer,
    CONSTRAINT fk_command_line_correct_result
        FOREIGN KEY (correct_result_id) REFERENCES correct_result (correct_result_id)
);

--changeset knpiskunov:create_command_line_sequence
CREATE SEQUENCE command_line_is_seq START WITH 1 INCREMENT BY 1;


