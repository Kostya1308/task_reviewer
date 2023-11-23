package ru.clevertec.courses.task_reviewer.exception;

public class FailedReviewException extends RuntimeException{

    public FailedReviewException(String message) {
        super(message);
    }
}
