package com.ravi.ftp.fs;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DirListerTest {
    public static Path mkTmpDir(String prefix) throws IOException {
        Path tmpDir = Files.createTempDirectory("dltst1");
        tmpDir.toFile().deleteOnExit();

        return tmpDir;
    }

    public static Path mkTmpFile(Path parent, String fileName) throws IOException {
        Path tmpFile = Files.createFile(Paths.get(parent.toString(), fileName));
        tmpFile.toFile().deleteOnExit();

        return tmpFile;
    }

    @Test
    public void existingFolderList() throws IOException {
        Path tmpDir = mkTmpDir("dltst1");
        mkTmpFile(tmpDir, "aaa.txt");
        mkTmpFile(tmpDir, "bbb.text");

        List<String> names = new DirLister(tmpDir).list();
        assertThat(names).isNotEmpty().hasSize(1);
        assertThat(names).contains("aaa.txt");
        assertThat(names).doesNotContain("bbb.text");
    }

    // TODO: mocking static methods requires yet-another-test-only-library
    //       we can use powermock, but that pulls down atleast 2 more libraries.
    // Given the minimize-library usage rule, that test will be deferred.

    @Test
    public void onlyHtmlFiles() throws IOException {
        Path tmpDir = mkTmpDir("dltst2");
        mkTmpFile(tmpDir, "aaa.html");
        mkTmpFile(tmpDir, "bbb.htm");

        List<String> names = new DirLister(tmpDir).list();
        assertThat(names).isNotNull();
        assertThat(names).isEmpty();
    }
}
