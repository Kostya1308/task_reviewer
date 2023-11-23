package ru.clevertec.courses.task_reviewer.processor;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.dto.TaskDto;

@Component
public abstract class AbstractCheckingProcessor implements Ordered {

    public abstract void check(TaskDto taskDto);

}
