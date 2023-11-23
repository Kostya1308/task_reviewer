package ru.clevertec.courses.task_reviewer.downloader;

import java.io.File;

public interface FileDownloader {

    File downloadFromPath(String path);

}
