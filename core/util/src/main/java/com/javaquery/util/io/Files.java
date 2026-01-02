package com.javaquery.util.io;

import com.javaquery.util.Assert;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class Files {

    private static final Logger LOGGER = LoggerFactory.getLogger(Files.class);
    /**
     * returns the system temporary directory location
     */
    public static final String SYSTEM_TMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * returns the current working directory location
     */
    public static final String CURRENT_WORKING_DIR = System.getProperty("user.dir");

    /**
     * Create new, empty file at specified path in {@link File} object. This method will also creates
     * folder structure if not exists.
     *
     * <p>Note: Exception is logged not thrown.
     *
     * @param file - file to create
     * @param <T> the type of class that extends {@link File}
     * @return <code>true</code> if the named file does not exist and was successfully created; <code>
     *     false</code> if the named file already exists
     */
    public static <T extends File> boolean createNewFile(T file) {
        Assert.nonNull(file, NullPointerException::new);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                return file.createNewFile();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Delete file if exists.
     * @param file - file to delete
     * @return <code>true</code> if and only if the file or directory is successfully deleted; <code>false</code> otherwise
     */
    public static <T extends File> boolean deleteIfExists(T file) {
        return file.exists() && file.delete();
    }

    /**
     * Delete existing file and then Create new, empty file at specified path in {@link File} object.
     *
     * <p>Note: Exception is logged not thrown.
     *
     * @param file - file to delete and create
     * @param <T> the type of class that extends {@link File}
     * @return <code>true</code> if the named file deleted and was successfully created; <code>false
     *     </code> if the named file already exists
     */
    public static <T extends File> boolean deleteAndCreateNewFile(T file) {
        Assert.nonNull(file, NullPointerException::new);
        if (file.exists()) {
            file.delete();
        }
        return createNewFile(file);
    }

    /**
     * Write data to provided file.
     *
     * <p>Note: This method will also creates new <code>file</code> if not exist. Exception is logged
     * not thrown.
     *
     * @param file file to write
     * @param data data to write to file
     * @param <T> the type of class that extends {@link File}
     */
    public static <T extends File> void writeToFile(T file, String data) {
        Assert.nonNull(file, NullPointerException::new);
        Assert.nonNull(data, NullPointerException::new);
        if (!file.exists()) {
            createNewFile(file);
        }
        try {
            java.nio.file.Files.write(getPath(file), data.getBytes());
        } catch (IOException | InvalidPathException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Append data to provided <code>file</code> Note: This method will also creates new <code>file
     * </code> if not exist. Exception is logged not thrown.
     *
     * @param file file to write
     * @param data data to append to file
     * @param appendNewLine <code>true</code> to append new line at the end of data otherwise <code>
     *     false</code>.
     * @param <T> the type of class that extends {@link File}
     */
    public static <T extends File> void appendToFile(T file, String data, boolean appendNewLine) {
        Assert.nonNull(file, NullPointerException::new);
        Assert.nonNull(data, NullPointerException::new);
        if (!file.exists()) {
            createNewFile(file);
        }
        data = appendNewLine ? (data + "\n") : data;
        try {
            java.nio.file.Files.write(getPath(file), data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException | InvalidPathException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Read <code>String</code> content of <code>file</code>.
     *
     * <p>Note: Exception is logged not thrown.
     *
     * @param file file to read
     * @param <T> the type of class that extends {@link File}
     * @return String data of file if exists otherwise <code>null</code>
     */
    public static <T extends File> String readFromFile(T file) {
        Assert.nonNull(file, NullPointerException::new);
        if (file.exists()) {
            try {
                return new String(java.nio.file.Files.readAllBytes(getPath(file)));
            } catch (IOException | InvalidPathException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Path of file provided
     *
     * @param file file to get {@link Path}.
     * @param <T> the type of class that extends {@link File}
     * @return {@link Path} of file
     */
    private static <T extends File> Path getPath(T file) {
        return Paths.get(file.getAbsolutePath());
    }

    /**
     * Read any file from resources folder of project. For example, read demo.json from provided path
     * `resources folder` <code>/sample/demo.json</code>
     *
     * @param path path to resource
     * @return String data of file if exists otherwise <code>null</code>
     */
    public String loadResource(String path) {
        Assert.nonNull(path, NullPointerException::new);
        if (!path.trim().isEmpty()) {
            try (InputStream inputStream = getClass().getResourceAsStream(path);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String fileLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((fileLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(fileLine);
                }
                return stringBuilder.toString();
            } catch (IOException | NullPointerException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Determine content type of file
     *
     * @param file file to determine content type
     * @return content type of file
     */
    public static String determineContentType(File file) {
        try {
            String contentType = java.nio.file.Files.probeContentType(file.toPath());
            return contentType != null ? contentType : FileTypes.APPLICATION_OCTET_STREAM;
        } catch (IOException e) {
            return FileTypes.APPLICATION_OCTET_STREAM;
        }
    }
}
