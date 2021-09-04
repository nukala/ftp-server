package com.ravi.ftp.cmd;

import com.ravi.ftp.fs.DirLister;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import static com.ravi.ftp.fs.DirListerTest.mkTmpDir;
import static com.ravi.ftp.fs.DirListerTest.mkTmpFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class IndexCommandTest {
    @Test
    public void listerThrowsException() throws IOException {
        StringWriter sw = new StringWriter();
        Path tmpDir = mkTmpDir("ict1");
        DirLister dirLister = new DirLister(tmpDir);
        IndexCommand indexCommand = new IndexCommand(new BufferedWriter(sw), tmpDir, "listerThrowsException");
        DirLister.DirHelperForTesting mockHelper = mock(DirLister.DirHelperForTesting.class);
        doThrow(new IOException("mock-ioexception")).when(mockHelper).listFiles();
        dirLister.setHelper(mockHelper);
        indexCommand.setLister(dirLister);

        indexCommand.doWork();

        String cmdAnswer = sw.toString();
        assertThat(cmdAnswer).isEmpty();
    }

    @Test
    public void txtFilesAreReturned() throws IOException {
        StringWriter sw = new StringWriter();
        Path tmpDir = mkTmpDir("ict2");
        mkTmpFile(tmpDir, "aaa.txt");
        mkTmpFile(tmpDir, "b.txt");
        mkTmpFile(tmpDir, "ccc.txt");

        new IndexCommand(new BufferedWriter(sw), tmpDir, "index")
                .doWork();

        String answer = sw.toString();
        assertThat(answer).isNotEmpty();

        String[] lines = answer.split(System.getProperty("line.separator"));
        assertThat(lines).hasSize(3);
        assertThat(lines)
                .as("check filenames returned")
                .containsExactlyInAnyOrder("aaa.txt", "b.txt", "ccc.txt");
    }
}
