package ru.clevertec.courses.task_reviewer.processor;

import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ListGoodsProcessor extends AbstractCheckingProcessor {

    private static final String GOODS_LISTS_DO_NOT_MATCH_MESSAGE = "Список товаров в чеке некорректный";

    @Override
    public void check(TaskDto taskDto) {
        Map<Integer, ReceiptDto> receiptDtoToReviewMap = taskDto.getReceiptDtoToReviewMap();
        Map<Integer, ReceiptDto> correctReceiptDtoMap = taskDto.getCorrectReceiptDtoMap();

        correctReceiptDtoMap.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue().getDateTimeInfo()))
                .forEach(correctEntry -> {
                    List<String> correctDescriptions = getDescriptions(correctEntry.getValue());
                    List<String> descriptionsToReview = getDescriptions(receiptDtoToReviewMap.get(correctEntry.getKey()));
                    if (!correctDescriptions.equals(descriptionsToReview)) {
                        throw new FailedReviewException(GOODS_LISTS_DO_NOT_MATCH_MESSAGE);
                    }
                });
    }

    private static List<String> getDescriptions(ReceiptDto receiptDto) {
        return receiptDto.getGoodsInfoList().stream()
                .map(ReceiptDto.GoodsInfo::getDescription)
                .toList();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
