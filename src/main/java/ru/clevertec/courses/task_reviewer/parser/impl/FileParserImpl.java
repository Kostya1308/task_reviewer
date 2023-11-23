package ru.clevertec.courses.task_reviewer.parser.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.exception.FailedReviewException;
import ru.clevertec.courses.task_reviewer.parser.FileParser;
import ru.clevertec.courses.task_reviewer.validator.Validator;

import static ru.clevertec.courses.task_reviewer.constant.Constant.APPENDER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DATE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DEFAULT_NUMBER_LINES_TO_SKIP;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DESCRIPTION_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DISCOUNT_CARD_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.DISCOUNT_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.EMPTY_STRING_ARRAY;
import static ru.clevertec.courses.task_reviewer.constant.Constant.INCORRECT_STRUCTURE_MESSAGE;
import static ru.clevertec.courses.task_reviewer.constant.Constant.PRICE_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.QTY_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.REQUIRED_NUMBER_ONE;
import static ru.clevertec.courses.task_reviewer.constant.Constant.SEPARATOR_CHAR;
import static ru.clevertec.courses.task_reviewer.constant.Constant.SEPARATOR_STRING;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TIME_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_HEADER;
import static ru.clevertec.courses.task_reviewer.constant.Constant.TOTAL_PRICE_HEADER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileParserImpl implements FileParser {

    private final Validator validator;

    public ReceiptDto getReceiptDto(File file) {

        ReceiptDto.DateTimeInfo dateTimeParse = getDateTimeParse(file);

        int skipLines = REQUIRED_NUMBER_ONE + DEFAULT_NUMBER_LINES_TO_SKIP;
        List<ReceiptDto.GoodsInfo> goodsParse = getGoodsParse(file, skipLines);

        skipLines += goodsParse.size() + DEFAULT_NUMBER_LINES_TO_SKIP;
        ReceiptDto.DiscountInfo discountParse = getDiscountParse(file, skipLines);

        skipLines = Optional.ofNullable(discountParse).isEmpty() ?
                skipLines : skipLines + REQUIRED_NUMBER_ONE + DEFAULT_NUMBER_LINES_TO_SKIP;
        ReceiptDto.TotalInfo totalParse = getTotalParse(file, skipLines);

        return ReceiptDto.builder()
                .dateTimeInfo(dateTimeParse)
                .goodsInfoList(goodsParse)
                .discountInfo(discountParse)
                .totalInfo(totalParse)
                .build();
    }

    private ReceiptDto.DateTimeInfo getDateTimeParse(File file) {
        List<ReceiptDto.DateTimeInfo> dateTimeParse = new ArrayList<>();

        try (InputStream commonFileInputStream = new FileInputStream(file);
             InputStreamReader commonFileInputStreamReader =
                     new InputStreamReader(Objects.requireNonNull(commonFileInputStream), StandardCharsets.UTF_8);
             CSVReader commonFileCsvReader = new CSVReader(commonFileInputStreamReader)) {

            validator.checkFirstLineIsHeader(commonFileCsvReader.peek(), DATE_HEADER, TIME_HEADER);

            List<String[]> dateTimeStringArrayList = getStringArrayList(commonFileCsvReader, QTY_HEADER);
            String dateTimeCsvData = getCsvData(dateTimeStringArrayList);

            InputStream datetimeInputStream = new ByteArrayInputStream(dateTimeCsvData.getBytes(StandardCharsets.UTF_8));
            InputStreamReader datetimeInputStreamReader =
                    new InputStreamReader(Objects.requireNonNull(datetimeInputStream), StandardCharsets.UTF_8);

            dateTimeParse = getParseList(datetimeInputStreamReader, ReceiptDto.DateTimeInfo.class);

            datetimeInputStream.close();
            datetimeInputStreamReader.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return getRequiredFirstItem(dateTimeParse);
    }

    private List<ReceiptDto.GoodsInfo> getGoodsParse(File file, int skipLines) {
        List<ReceiptDto.GoodsInfo> goodsParse = new ArrayList<>();

        try (InputStream commonFileInputStream = new FileInputStream(file);
             InputStreamReader commonFileInputStreamReader =
                     new InputStreamReader(Objects.requireNonNull(commonFileInputStream), StandardCharsets.UTF_8);
             CSVReader commonFileCsvReader = new CSVReaderBuilder(commonFileInputStreamReader)
                     .withSkipLines(skipLines)
                     .build()) {

            validator.checkFirstLineIsHeader(commonFileCsvReader.peek(), QTY_HEADER, DESCRIPTION_HEADER, PRICE_HEADER,
                    DISCOUNT_HEADER, TOTAL_HEADER);

            List<String[]> goodsStringArrayList = getStringArrayList(commonFileCsvReader, DISCOUNT_CARD_HEADER, TOTAL_PRICE_HEADER);
            String goodsCsvData = getCsvData(goodsStringArrayList);

            InputStream goodsInputStream = new ByteArrayInputStream(goodsCsvData.getBytes(StandardCharsets.UTF_8));
            InputStreamReader goodsInputStreamReader =
                    new InputStreamReader(Objects.requireNonNull(goodsInputStream), StandardCharsets.UTF_8);

            goodsParse = getParseList(goodsInputStreamReader, ReceiptDto.GoodsInfo.class);

            goodsInputStream.close();
            goodsInputStreamReader.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return goodsParse;
    }

    private ReceiptDto.DiscountInfo getDiscountParse(File file, int skipLines) {
        List<ReceiptDto.DiscountInfo> discountParse = new ArrayList<>();

        try (InputStream commonFileInputStream = new FileInputStream(file);
             InputStreamReader commonFileInputStreamReader =
                     new InputStreamReader(Objects.requireNonNull(commonFileInputStream), StandardCharsets.UTF_8);
             CSVReader commonFileCsvReader = new CSVReaderBuilder(commonFileInputStreamReader)
                     .withSkipLines(skipLines)
                     .build()) {

            if (Arrays.toString(commonFileCsvReader.peek()).contains(TOTAL_PRICE_HEADER)) {
                return null;
            }

            List<String[]> discountStringArrayList = getStringArrayList(commonFileCsvReader, TOTAL_HEADER);
            String discountCsvData = getCsvData(discountStringArrayList);

            InputStream discountInputStream = new ByteArrayInputStream(discountCsvData.getBytes(StandardCharsets.UTF_8));
            InputStreamReader discountInputStreamReader =
                    new InputStreamReader(Objects.requireNonNull(discountInputStream), StandardCharsets.UTF_8);

            discountParse = getParseList(discountInputStreamReader, ReceiptDto.DiscountInfo.class);

            discountInputStream.close();
            discountInputStreamReader.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return getRequiredFirstItem(discountParse);
    }


    private ReceiptDto.TotalInfo getTotalParse(File file, int skipLines) {
        List<ReceiptDto.TotalInfo> discountParse = new ArrayList<>();
        try (InputStream commonFileInputStream = new FileInputStream(file);
             InputStreamReader commonFileInputStreamReader =
                     new InputStreamReader(Objects.requireNonNull(commonFileInputStream), StandardCharsets.UTF_8);
             CSVReader commonFileCsvReader = new CSVReaderBuilder(commonFileInputStreamReader)
                     .withSkipLines(skipLines)
                     .build()) {

            List<String[]> totalStringArrayList = getTotalStringArrayList(commonFileCsvReader);
            String totalCsvData = getCsvData(totalStringArrayList);

            InputStream totalInputStream = new ByteArrayInputStream(totalCsvData.getBytes(StandardCharsets.UTF_8));
            InputStreamReader totalInputStreamReader =
                    new InputStreamReader(Objects.requireNonNull(totalInputStream), StandardCharsets.UTF_8);

            discountParse = getParseList(totalInputStreamReader, ReceiptDto.TotalInfo.class);

            totalInputStream.close();
            totalInputStreamReader.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return getRequiredFirstItem(discountParse);
    }

    private static <T extends ReceiptDto.Body> List<T> getParseList(InputStreamReader inputStreamReader,
                                                                    Class<T> clazz) {
        List<T> parse = new CsvToBeanBuilder<T>(inputStreamReader)
                .withType(clazz)
                .withSeparator(SEPARATOR_CHAR)
                .build()
                .parse();

        return getCastedList(parse);
    }

    @SneakyThrows
    private List<String[]> getStringArrayList(CSVReader commonFileCsvReader, String... headers) {
        List<String[]> dateTimeStringArrayList = new ArrayList<>();

        while (!Arrays.toString(commonFileCsvReader.peek()).equals(Arrays.toString(EMPTY_STRING_ARRAY))) {
            validator.checkNextLineIsNotHeader(commonFileCsvReader.peek(), headers);
            dateTimeStringArrayList.add(commonFileCsvReader.readNext());
        }

        return dateTimeStringArrayList;
    }

    @SneakyThrows
    private List<String[]> getTotalStringArrayList(CSVReader commonFileCsvReader) {
        List<String[]> totalStringArrayList = new ArrayList<>();

        while (!(Arrays.toString(commonFileCsvReader.peek()).equals(Arrays.toString(EMPTY_STRING_ARRAY)) ||
                commonFileCsvReader.peek() == null)) {
            totalStringArrayList.add(commonFileCsvReader.readNext());
        }

        return totalStringArrayList;

    }

    private static String getCsvData(List<String[]> stringArrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] array : stringArrayList) {
            stringBuilder.append(String.join(SEPARATOR_STRING, array));
            stringBuilder.append(APPENDER);
        }

        return stringBuilder.toString();
    }


    private static <T extends ReceiptDto.Body> List<T> getCastedList(List<T> parse) {
        List<T> castedList = new ArrayList<>();

        for (T t : parse) {
            try {
                castedList.add(t);
            } catch (ClassCastException e) {
                log.error(e.getMessage());
            }
        }

        return castedList;
    }

    private static <T extends ReceiptDto.Body> T getRequiredFirstItem(List<T> dateTimeParse) {
        if (dateTimeParse.size() == REQUIRED_NUMBER_ONE) {
            return dateTimeParse.get(0);
        } else {
            throw new FailedReviewException(INCORRECT_STRUCTURE_MESSAGE);
        }
    }

}
