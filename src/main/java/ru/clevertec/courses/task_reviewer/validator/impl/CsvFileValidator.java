package ru.clevertec.courses.task_reviewer.validator.impl;

import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;
import ru.clevertec.courses.task_reviewer.validator.Validator;

import static ru.clevertec.courses.task_reviewer.constant.Constant.INCORRECT_STRUCTURE_MESSAGE;

import java.util.Arrays;

@Component
public class CsvFileValidator implements Validator {

    public void checkNextLineIsNotHeader(String[] strings, String... headers) {
        boolean nextLineIsNotHeader = Arrays.stream(headers)
                .anyMatch(header -> Arrays.toString(strings).contains(header));

        if (nextLineIsNotHeader) {
            throw new FailedReviewException(INCORRECT_STRUCTURE_MESSAGE);
        }
    }

    public void checkFirstLineIsHeader(String[] strings, String... headers) {
        boolean firstLineIsHeader = Arrays.stream(headers)
                .allMatch(header -> Arrays.toString(strings).contains(header));

        if (!firstLineIsHeader) {
            throw new FailedReviewException(INCORRECT_STRUCTURE_MESSAGE);
        }
    }


}
