package ru.clevertec.courses.task_reviewer.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.clevertec.courses.task_reviewer.entity.CommandLine;

import java.util.List;

public interface CommandLineRepository extends JpaRepository<CommandLine, Integer> {

    @NonNull
    @EntityGraph(attributePaths = "correctResult")
    List<CommandLine> findAll();

}
