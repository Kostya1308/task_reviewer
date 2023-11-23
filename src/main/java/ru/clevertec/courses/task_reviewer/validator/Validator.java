package ru.clevertec.courses.task_reviewer.validator;

public interface Validator {

    void checkNextLineIsNotHeader(String[] strings, String... headers);

    void checkFirstLineIsHeader(String[] strings, String... headers);
}
