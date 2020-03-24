package com.kanomiya.hinaoutlineprocessor.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Kanomiya in 2017/02.
 */
public class FileUtils {

    public static String extension(String filename) {
        int lastIndexOf = filename.lastIndexOf('.');
        return lastIndexOf == filename.length() -1 || lastIndexOf == -1 ? null : filename.substring(lastIndexOf +1);
    }

    public static String readAll(Path path) throws IOException {
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(path).forEach(line -> builder.append(line).append(System.lineSeparator()));
        if (builder.length() > 0) builder.deleteCharAt(builder.length() -1);

        return builder.toString();
    }

}
