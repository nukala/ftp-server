package com.ravi.ftp.utils;

import com.ravi.ftp.srvr.FtpServer.InvalidFolderException;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FtpServerUtils {
    /**
     * return true if array is empty
     */
    public static <T> boolean isEmpty(T[] ary) {
        return ary == null
                || ary.length == 0
                || ary[0] == null;
    }

    /**
     * Closes any IO object, while ignoring (swallowing) an exception
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                System.out.printf("Could not close %s due to [%s]%n",
                        closeable.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    public static boolean isEmpty(String str) {
        return str == null
                || str.length() == 0;
    }

    /**
     * Check if specified folderPath exists and a directory.
     *
     * @param folderPath
     */
    public static void validateFolder(Path folderPath) {
        if (folderPath == null) {
            throw new InvalidFolderException("null folder");
        }
        if (!Files.exists(folderPath)) {
            throw new InvalidFolderException("Folder [" + folderPath + "] does not exist");
        }
        if (!Files.isDirectory(folderPath)) {
            throw new InvalidFolderException("Folder [" + folderPath + "] is not a directory");
        }
    }
}
