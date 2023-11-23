package ru.clevertec.courses.task_reviewer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DataFormatProcessor extends AbstractCheckingProcessor {

    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String MONETARY_VALUE_PATTERN = "\\d+\\.\\d{2}\\$";

    private static final String INCORRECT_DATE_FORMAT_MESSAGE = "Формат даты некорректный";
    private static final String INCORRECT_TIME_FORMAT_MESSAGE = "Формат времени некорректный";
    private static final String INCORRECT_DATA_FORMAT_MESSAGE = "Формат денежных значений некорректный";

    @Override
    public void check(TaskDto taskDto) {
        Map<Integer, ReceiptDto> receiptDtoToReviewMap = taskDto.getReceiptDtoToReviewMap();

        checkMonetaryValuesFormat(receiptDtoToReviewMap);

        receiptDtoToReviewMap.values()
                .stream()
                .map(ReceiptDto::getDateTimeInfo)
                .forEach(dateTimeInfo -> {
                    checkDateFormat(dateTimeInfo.getDate());
                    checkTimeFormat(dateTimeInfo.getTime());
                });
    }

    private void checkMonetaryValuesFormat(Map<Integer, ReceiptDto> receiptDtoToReviewMap) {
        List<String> monetaryValues = new ArrayList<>();

        receiptDtoToReviewMap.values()
                .forEach(receiptDto -> {
                    receiptDto.getGoodsInfoList().forEach(goodsInfo -> monetaryValues.add(goodsInfo.getPrice()));
                    receiptDto.getGoodsInfoList().forEach(goodsInfo -> monetaryValues.add(goodsInfo.getTotal()));
                    receiptDto.getGoodsInfoList().forEach(goodsInfo -> monetaryValues.add(goodsInfo.getDiscount()));
                    monetaryValues.add(receiptDto.getTotalInfo().getTotalPrice());
                    monetaryValues.add(receiptDto.getTotalInfo().getTotalDiscount());
                    monetaryValues.add(receiptDto.getTotalInfo().getTotalWithDiscount());
                });

        monetaryValues.forEach(this::checkDoubleValueFormat);
    }

    private void checkDoubleValueFormat(String monetaryValue) {
        boolean isMatched = Pattern.matches(MONETARY_VALUE_PATTERN, monetaryValue);

        if (!isMatched) {
            throw new FailedReviewException(INCORRECT_DATA_FORMAT_MESSAGE);
        }
    }

    private void checkDateFormat(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
        } catch (DateTimeParseException e) {
            throw new FailedReviewException(INCORRECT_DATE_FORMAT_MESSAGE);
        }
    }

    private void checkTimeFormat(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_PATTERN));
        } catch (DateTimeParseException e) {
            throw new FailedReviewException(INCORRECT_TIME_FORMAT_MESSAGE);
        }
    }



    @Override
    public int getOrder() {
        return 2;
    }
}
