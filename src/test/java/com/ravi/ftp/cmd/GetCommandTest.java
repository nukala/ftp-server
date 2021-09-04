package com.ravi.ftp.cmd;

import com.ravi.ftp.srvr.Request;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.ravi.ftp.fs.DirListerTest.mkTmpDir;
import static com.ravi.ftp.fs.DirListerTest.mkTmpFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class GetCommandTest {
    @Test
    public void missingFileName() {
        StringWriter sw = new StringWriter();
        assertThatExceptionOfType(Commands.CommandException.class)
                .as("instantiate get -- missing filename")
                .isThrownBy(() -> new GetCommand(new BufferedWriter(sw), null,
                        "get"))
                .withMessageContaining("handles only 2 parameters");
        assertThat(sw.toString())
                .contains("error");
    }

    @Test
    public void tooManyFiles() {
        StringWriter sw = new StringWriter();
        assertThatExceptionOfType(Commands.CommandException.class)
                .as("instantiate get -- too many files")
                .isThrownBy(() -> new GetCommand(new BufferedWriter(sw), null,
                        "get a.txt b.txt"))
                .withMessageContaining("handles only 2 parameters");
        assertThat(sw.toString())
                .contains("error");
    }

    @Test
    public void getNonExistantFile() throws IOException {
        Path tmpDir = mkTmpDir("gct1-");
        StringWriter sw = new StringWriter();
        new GetCommand(new BufferedWriter(sw), tmpDir, "get a.txt")
                .doWork();

        String str = sw.toString();
        assertThat(str)
                .isNotEmpty();

        String[] lines = str.split(System.lineSeparator());
        assertThat(lines).hasSize(1);
        assertThat(lines[0])
                .isNotEmpty()
                .contains("error");
    }

    @Test
    public void getExistingFileName() throws IOException {
        Path tmpDir = mkTmpDir("gct2-");
        Path atxt = mkTmpFile(tmpDir, "a.txt");
        Files.write(atxt, "atxt".getBytes(StandardCharsets.UTF_8));
        StringWriter sw = new StringWriter();
        new GetCommand(new BufferedWriter(sw), tmpDir, "get a.txt")
                .doWork();

        String str = sw.toString();
        assertThat(str)
                .isNotEmpty();

        String[] lines = str.split(System.lineSeparator());
        assertThat(lines).hasSize(2);
        assertThat(lines[0])
                .isNotEmpty()
                .contains("ok");
        assertThat(lines[1])
                .isNotEmpty()
                .isEqualTo("atxt");
    }

    @Test
    public void writeFailureTest() throws IOException {
        Path tmpDir = mkTmpDir("gct3-");
        Path atxt = mkTmpFile(tmpDir, "a.txt");
        //Files.write(atxt, "atxt".getBytes(StandardCharsets.UTF_8));

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        doThrow(new IOException("mock-write-exception"))
                .when(mockWriter)
                .write(anyString());
        assertThatExceptionOfType(Request.RequestFailedException.class)
                .as("upon exception in write")
                .isThrownBy(() ->
                        new GetCommand(mockWriter, tmpDir, "get a.txt")
                                .doWork())
                .withMessageContaining("cmd=get a.txt failed.");

        verify(mockWriter).write(anyString());
    }

    @Test
    public void disallowedFile() throws IOException {
        Path tmpDir = mkTmpDir("gct4-");
        StringWriter sw = new StringWriter();
        GetCommand getCommand = new GetCommand(new BufferedWriter(sw), tmpDir, "get aa.htm");

        assertThatExceptionOfType(Commands.CommandException.class)
                .as("get filename that is not allowed")
                .isThrownBy(() -> getCommand.doWork())
                .withMessageContaining("aa.htm: not allowed");
    }
}
