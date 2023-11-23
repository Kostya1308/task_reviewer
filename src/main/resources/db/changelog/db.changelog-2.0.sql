--liquibase formatted sql

--changeset knpiskunov:fill_correct_result_table
INSERT INTO correct_result(result_path)
VALUES ('classpath:correct.results/1.csv');

INSERT INTO correct_result(result_path)
VALUES ('classpath:correct.results/2.csv');

--changeset knpiskunov:fill_command_line_table
INSERT INTO command_line(command_line_args, correct_result_id)
VALUES ('3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100', 1);

INSERT INTO command_line(command_line_args, correct_result_id)
VALUES ('3-1 2-5 5-1 balanceDebitCard=100', 2);

INSERT INTO command_line(command_line_args, correct_result_id)
VALUES ('3-1 2-5 5-1 discountCard=1111 balanceDebitCard=0', null);

