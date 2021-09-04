package com.ravi.ftp.srvr;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FtpServerImplTest {
    @Test(expected = FtpServerImpl.FtpServerException.class)
    public void nullArgsInit() {
        new FtpServerImpl().initialize(null);
    }

    @Test
    public void invalidFolderInit() {
        String[] args = new String[]{"/invalid/doesnot/exist"};

        // since it is the same exception-type
        assertThatExceptionOfType(FtpServer.InvalidFolderException.class)
                .isThrownBy(() -> new FtpServerImpl().initialize(args))
                .withMessageContaining("does not exist");
    }

    @Test
    public void initWithTemporaryFile() throws IOException {
        Path path = Files.createTempFile("rn", ".tmp");
        path.toFile().deleteOnExit();

        String[] args = new String[]{path.toAbsolutePath().toString()};
        // since it is the same exception-type
        assertThatExceptionOfType(FtpServer.InvalidFolderException.class)
                .isThrownBy(() -> new FtpServerImpl().initialize(args))
                .withMessageContaining("is not a directory");
    }
}
