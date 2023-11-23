package ru.clevertec.courses.task_reviewer.parser;

import com.opencsv.exceptions.CsvValidationException;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;

import java.io.File;
import java.io.IOException;

public interface FileParser {

    ReceiptDto getReceiptDto(File file);

}
