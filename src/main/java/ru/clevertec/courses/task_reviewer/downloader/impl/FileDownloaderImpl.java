package ru.clevertec.courses.task_reviewer.downloader.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import ru.clevertec.courses.task_reviewer.downloader.FileDownloader;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileDownloaderImpl implements FileDownloader {

    private final ResourceLoader resourceLoader;

    public File downloadFromPath(String path) {
        File file = null;

        try {
            file = resourceLoader.getResource(path).getFile();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return file;
    }

}
