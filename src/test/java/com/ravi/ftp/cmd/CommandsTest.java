package com.ravi.ftp.cmd;

import com.ravi.ftp.srvr.FtpServer;
import com.ravi.ftp.srvr.FtpServerImpl;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CommandsTest {
    @Test
    public void emptyStringHasUnknownCommand() {
        assertThat(Commands.makeCommand(null, null, ""))
                .as("command of empty string")
                .isInstanceOf(UnknownCommand.class);
    }

    @Test
    public void unknownCommandTypeCheck() {
        Command cmd = Commands.makeCommand(null, null, "blah");

        assertThat(cmd).isNotNull();
        assertThat(cmd).isInstanceOf(UnknownCommand.class);
    }

    @Test
    public void doWorkOfUnknownCommand() throws IOException {
        BufferedWriter writer = mock(BufferedWriter.class);

        Commands.makeCommand(null, writer, "unknown").doWork();
        verify(writer, times(1)).write("unknown command");
    }

    @Test
    public void quitCommandTypeCheck() {
        Command cmd = Commands.makeCommand(null, null, "quit");

        assertThat(cmd).isNotNull();
        assertThat(cmd).isInstanceOf(QuitCommand.class);
    }

    @Test
    public void letterQCommandTypeCheck() {
        Command cmd = Commands.makeCommand(null, null, "q");

        assertThat(cmd).isNotNull();
        assertThat(cmd).isInstanceOf(QuitCommand.class);
    }

    @Test
    public void doWorkOfQuitCommand() throws IOException {
        FtpServer ftpServer = mock(FtpServer.class);
        BufferedWriter writer = mock(BufferedWriter.class);
        Command quitCommand = Commands.makeCommand(ftpServer, writer, "quit");

        quitCommand.doWork();

        verify(ftpServer).setStopped(true);
        verify(ftpServer).forceStop();
        verify(writer, never()).write(anyString());
    }

    @Test
    public void indexCommandTypeCheck() {
        FtpServer ftpServer = new FtpServerImpl();
        ftpServer.initialize(new String[]{File.listRoots()[0].getName()});
        Command indexCommand = Commands.makeCommand(ftpServer, null, "index");

        assertThat(indexCommand).isNotNull();
        assertThat(indexCommand).isInstanceOf(IndexCommand.class);
    }

    @Test
    public void getCommandTypeCheck() {
        FtpServer ftpServer = new FtpServerImpl();
        ftpServer.initialize(new String[]{File.listRoots()[0].getName()});
        Command getCommand = Commands.makeCommand(ftpServer, null, "get fn");

        assertThat(getCommand).isNotNull();
        assertThat(getCommand).isInstanceOf(GetCommand.class);
    }

}
