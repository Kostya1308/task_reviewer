package ru.clevertec.courses.task_reviewer.service.impl;

import static ru.clevertec.courses.task_reviewer.constant.Constant.DOT;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.courses.task_reviewer.downloader.FileDownloader;
import ru.clevertec.courses.task_reviewer.dto.ReceiptDto;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;
import ru.clevertec.courses.task_reviewer.entity.CommandLine;
import ru.clevertec.courses.task_reviewer.parser.FileParser;
import ru.clevertec.courses.task_reviewer.processor.CheckingProcessorChain;
import ru.clevertec.courses.task_reviewer.repository.CommandLineRepository;
import ru.clevertec.courses.task_reviewer.service.TaskReviewerService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskReviewerServiceImpl implements TaskReviewerService {

    @Value("${app.tasks-path}")
    private String tasksPath;

    private final FileParser fileParser;
    private final FileDownloader fileDownloader;
    private final ResourceLoader resourceLoader;
    private final CommandLineRepository commandLineRepository;
    private final CheckingProcessorChain checkingProcessorChain;

    @Override
    @SneakyThrows
    public void reviewTasks() {
        List<CommandLine> commandLines = commandLineRepository.findAll();

        Map<Integer, ReceiptDto> correctReceiptDtoMap = commandLines.stream()
                .collect(Collectors.toMap(CommandLine::getCommandLineId, commandLine ->
                        Optional.ofNullable(commandLine.getCorrectResult()).isPresent() ?
                                fileParser.getReceiptDto(fileDownloader
                                        .downloadFromPath(commandLine.getCorrectResult().getResultPath())) :
                                new ReceiptDto()));

        File[] filesToReview = resourceLoader.getResource(tasksPath).getFile().listFiles();
        Map<Integer, ReceiptDto> receiptDtoToReviewMap = Arrays.stream(Objects.requireNonNull(filesToReview))
                .collect(Collectors.toMap(file -> Integer.valueOf(substringToDot(file)), fileParser::getReceiptDto));

        TaskDto taskDto = TaskDto.builder()
                .correctReceiptDtoMap(correctReceiptDtoMap)
                .receiptDtoToReviewMap(receiptDtoToReviewMap)
                .build();

        checkingProcessorChain.runChain(taskDto);

       //TODO разбить exceptions на более специфичекие
    }

    private static String substringToDot(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf(DOT));
    }
}
