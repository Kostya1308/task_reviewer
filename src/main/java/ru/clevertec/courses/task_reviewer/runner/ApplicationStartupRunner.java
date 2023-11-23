package ru.clevertec.courses.task_reviewer.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.service.TaskReviewerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupRunner{

    private final TaskReviewerService taskReviewerService;

    @EventListener
    public void run(ContextRefreshedEvent contextRefreshedEvent) {
        log.info(contextRefreshedEvent.toString());
        taskReviewerService.reviewTasks();
    }

}
