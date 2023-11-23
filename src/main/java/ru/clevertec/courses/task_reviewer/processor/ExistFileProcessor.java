package ru.clevertec.courses.task_reviewer.processor;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ExistFileProcessor extends AbstractCheckingProcessor {

    private static final String EXIST_FILE_EXCEPTION_MESSAGE = "Сформированы лишние и/или отсутствуют необходимые чеки";

    @Override
    public void check(TaskDto taskDto) {
        Map<Integer, ReceiptDto> correctReceiptDtoMap = taskDto.getCorrectReceiptDtoMap();
        Map<Integer, ReceiptDto> receiptDtoToReviewMap = taskDto.getReceiptDtoToReviewMap();

        List<Integer> correctReceiptIdList = correctReceiptDtoMap.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue().getDateTimeInfo()))
                .map(Map.Entry::getKey)
                .toList();

        List<Integer> receiptToReviewIdList = receiptDtoToReviewMap.keySet().stream().toList();

        if (!correctReceiptIdList.equals(receiptToReviewIdList)) {
            throw new FailedReviewException(EXIST_FILE_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
