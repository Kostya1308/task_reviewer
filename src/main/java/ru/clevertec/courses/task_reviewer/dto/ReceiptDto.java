package ru.clevertec.courses.task_reviewer.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static ru.clevertec.courses.task_reviewer.constant.Constant.DATE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DESCRIPTION_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DISCOUNT_CARD_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DISCOUNT_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DISCOUNT_PERCENTAGE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.PRICE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.QTY_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TIME_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_DISCOUNT_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_PRICE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_WITH_DISCOUNT_HEADER;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReceiptDto {

    private DateTimeInfo dateTimeInfo;
    private List<GoodsInfo> goodsInfoList;
    private DiscountInfo discountInfo;
    private TotalInfo totalInfo;

    public static class Body {
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DateTimeInfo extends Body {

        @CsvBindByName(column = DATE_HEADER)
        private String date;

        @CsvBindByName(column = TIME_HEADER)
        private String time;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class GoodsInfo extends Body {

        @CsvBindByName(column = QTY_HEADER)
        private String quantity;

        @CsvBindByName(column = PRICE_HEADER)
        private String price;

        @CsvBindByName(column = TOTAL_HEADER)
        private String total;

        @CsvBindByName(column = DISCOUNT_HEADER)
        private String discount;

        @CsvBindByName(column = DESCRIPTION_HEADER)
        private String description;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DiscountInfo extends Body {

        @CsvBindByName(column = DISCOUNT_CARD_HEADER)
        private String discountCard;

        @CsvBindByName(column = DISCOUNT_PERCENTAGE_HEADER)
        private String discountPercentage;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TotalInfo extends Body {

        @CsvBindByName(column = TOTAL_PRICE_HEADER)
        private String totalPrice;

        @CsvBindByName(column = TOTAL_DISCOUNT_HEADER)
        private String totalDiscount;

        @CsvBindByName(column = TOTAL_WITH_DISCOUNT_HEADER)
        private String totalWithDiscount;
    }

}
