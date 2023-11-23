package ru.clevertec.courses.task_reviewer.processor;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class CalculationProcessor extends AbstractCheckingProcessor {

    private static final String INCORRECT_CALCULATION_RESULTS_MESSAGE = "Некорректные результаты расчётов";

    @Override
    public void check(TaskDto taskDto) {
        Map<Integer, ReceiptDto> receiptDtoToReviewMap = taskDto.getReceiptDtoToReviewMap();
        Map<Integer, ReceiptDto> correctReceiptDtoMap = taskDto.getCorrectReceiptDtoMap();

        receiptDtoToReviewMap.forEach((key, value) -> checkCalculating(value, correctReceiptDtoMap.get(key)));
    }

    private void checkCalculating(ReceiptDto reviewedReceiptDto, ReceiptDto correctReceiptDto) {
        List<ReceiptDto.GoodsInfo> reviewedGoodsInfoList = reviewedReceiptDto.getGoodsInfoList();
        List<ReceiptDto.GoodsInfo> correctGoodsInfoList = correctReceiptDto.getGoodsInfoList();
        ReceiptDto.TotalInfo reviewedTotalInfo = reviewedReceiptDto.getTotalInfo();
        ReceiptDto.TotalInfo correctTotalInfo = correctReceiptDto.getTotalInfo();

        reviewedGoodsInfoList.forEach(reviewedGoodsInfo -> correctGoodsInfoList.stream()
                .filter(getCorrectGoodsInfoPredicate(reviewedGoodsInfo))
                .findAny()
                .orElseThrow(() -> new FailedReviewException(INCORRECT_CALCULATION_RESULTS_MESSAGE)));

        boolean isValidTotalInfo = Optional.ofNullable(reviewedTotalInfo)
                .filter(getCorrectTotalInfoPredicate(correctTotalInfo))
                .isPresent();

        if (!isValidTotalInfo) {
            throw new FailedReviewException(INCORRECT_CALCULATION_RESULTS_MESSAGE);
        }
    }

    private static Predicate<ReceiptDto.GoodsInfo> getCorrectGoodsInfoPredicate(ReceiptDto.GoodsInfo reviewedGoodsInfo) {
        return correctGoodsInfo ->
                Objects.equals(correctGoodsInfo.getPrice(), reviewedGoodsInfo.getPrice()) &&
                        Objects.equals(correctGoodsInfo.getTotal(), reviewedGoodsInfo.getTotal()) &&
                        Objects.equals(correctGoodsInfo.getDiscount(), reviewedGoodsInfo.getDiscount()) &&
                        Objects.equals(correctGoodsInfo.getQuantity(), reviewedGoodsInfo.getQuantity()) &&
                        Objects.equals(correctGoodsInfo.getDescription(), reviewedGoodsInfo.getDescription());
    }

    private static Predicate<ReceiptDto.TotalInfo> getCorrectTotalInfoPredicate(ReceiptDto.TotalInfo correctTotalInfo) {
        return reviewedTotal -> Objects.equals(reviewedTotal.getTotalPrice(), correctTotalInfo.getTotalPrice()) &&
                Objects.equals(reviewedTotal.getTotalDiscount(), correctTotalInfo.getTotalDiscount()) &&
                Objects.equals(reviewedTotal.getTotalWithDiscount(), correctTotalInfo.getTotalWithDiscount());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
