package com.ravi.ftp.utils;

import com.ravi.ftp.srvr.FtpServer;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

// needed coz - not using any libraries
public class FtpServerUtilsTest {
    @Test
    public void nullArrayIsEmpty() {
        Integer[] ary = null;
        assertThat(FtpServerUtils.isEmpty(ary))
                .as("null array should be empty")
                .isTrue();
    }

    @Test
    public void emptyArrayIsEmpty() {
        String[] ary = new String[0];

        assertThat(FtpServerUtils.isEmpty(ary))
                .as("empty array should be empty")
                .isTrue();
    }

    @Test
    public void uninitedArrayIsEmpty() {
        assertThat(FtpServerUtils.isEmpty(new String[1]))
                .as("uninited array")
                .isTrue();
    }

    @Test
    public void nullStringIsEmpty() {
        String str = null;
        assertThat(FtpServerUtils.isEmpty(str))
                .as("null string")
                .isTrue();
    }

    @Test
    public void openCloseStringIsEmpty() {
        assertThat(FtpServerUtils.isEmpty(""))
                .as("open close quotes string")
                .isTrue();
    }

    @Test
    public void stringFooIsNotEmpty() {
        assertThat(FtpServerUtils.isEmpty("foo"))
                .as("open close quotes string")
                .isFalse();
    }

    @Test
    public void withValuesIsNotEmpty() {
        String[] ary = new String[]{"this", "is", "a", "test"};

        assertThat(FtpServerUtils.isEmpty(ary))
                .as("with values is not empty")
                .isFalse();
    }

    @Test
    public void closeSuccess() throws IOException {
        OutputStream os = mock(OutputStream.class);
        doNothing().when(os).close();

        FtpServerUtils.close(os);
        Mockito.verify(os, times(1)).close();
    }

    @Test
    public void closeNullObject() {
        FtpServerUtils.close(null);
    }

    @Test
    public void exceptionSwallowed() throws IOException {
        InputStreamReader isr = mock(InputStreamReader.class);
        doThrow(new IOException("mock-close-exception")).when(isr).close();

        FtpServerUtils.close(isr);
        Mockito.verify(isr, times(1)).close();
    }

    @Test
    public void nullFoderIsNotValid() {
        assertThatExceptionOfType(FtpServer.InvalidFolderException.class)
                .as("null folder is not valid")
                .isThrownBy(() -> FtpServerUtils.validateFolder(null))
                .withMessageContaining("null folder");
    }

    @Test
    public void nonExistantFolder() {
        Path path = Paths.get(File.separator + "xxxyyzz");
        assertThatExceptionOfType(FtpServer.InvalidFolderException.class)
                .isThrownBy(() -> FtpServerUtils.validateFolder(path))
                .withMessageContaining("does not exist");
    }
    
    @Test
    public void tempFileIsNotFolder() throws IOException {
        Path path = Files.createTempFile("rn", ".tmp");
        path.toFile().deleteOnExit();

        assertThatExceptionOfType(FtpServer.InvalidFolderException.class)
                .isThrownBy(() -> FtpServerUtils.validateFolder(path))
                .withMessageContaining("is not a directory");
    }
}
