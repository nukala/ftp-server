package com.ravi.ftp.fs;

import com.ravi.ftp.utils.FtpServerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A non-recursive, un-cached directory lister !
 */
public class DirLister implements Lister {
    private final Path folder;
    // for testing purposes only. this wrapper serves no code-value
    private DirHelperForTesting helper;

    public DirLister(Path folder) {
        this.folder = folder;
        this.helper = new DirHelperForTesting();

        FtpServerUtils.validateFolder(this.folder);
    }

    /**
     * Check if the specified path is allowed to be listed.
     * @param p
     * @return
     */
    public static boolean isAllowed(Path p) {
        return isAllowed(p.toString());
    }

    /**
     * Is the specified fileName allowed to be listed.
     * 
     * @param fileName
     * @return
     */
    public static boolean isAllowed(String fileName) {
        return fileName.endsWith(".txt");
    }

    @Override
    public List<String> list() {
        List<String> fileNames = new ArrayList<>();
        // better style is to use PathMatcher with glob .. for next version.
        try {
            helper.listFiles()
                    .filter(p -> isAllowed(p))
                    .forEach(p -> fileNames.add(p.getFileName().toString()));
            //System.out.printf("FileNames = [%s]%n", String.join(", ", fileNames));
        } catch (IOException ioe) {
            System.err.printf("List files in [%s] failed(%s).%n", getFolderName(), ioe.getMessage());
        }
        return Collections.unmodifiableList(fileNames);
    }

    @Override
    public String getFolderName() {
        return folder.toString();
    }

    // @VisibleForTesting
    public void setHelper(DirHelperForTesting helper) {
        this.helper = helper;
    }

    // static methods cannot be mocked with simple-Mockito. Need another library
    // purely for exception testing, serves no purpose (OO or otherwise)
    // @VisibleForTesting
    public class DirHelperForTesting {
        public Stream<Path> listFiles() throws IOException {
            return Files.list(folder);
        }
    }
}
