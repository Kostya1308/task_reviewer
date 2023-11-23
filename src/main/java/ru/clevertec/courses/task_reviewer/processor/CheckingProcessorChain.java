package ru.clevertec.courses.task_reviewer.processor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckingProcessorChain {

    private final List<AbstractCheckingProcessor> abstractCheckingProcessors;

    @PostConstruct
    public void sortCheckingProcessors() {
        abstractCheckingProcessors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    public void runChain(TaskDto taskDto) {
        abstractCheckingProcessors.forEach(checkingProcessor -> checkingProcessor.check(taskDto));
    }

}
