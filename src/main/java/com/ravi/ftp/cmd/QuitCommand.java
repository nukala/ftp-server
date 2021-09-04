package com.ravi.ftp.cmd;

import com.ravi.ftp.srvr.FtpServer;

import java.io.BufferedWriter;

/**
 * "quit" command
 */
public class QuitCommand extends AbstractCommand {
    private final FtpServer ftpServer;

    protected QuitCommand(FtpServer ftpServer, BufferedWriter writer, String cmdStr) {
        super(writer, cmdStr);
        this.ftpServer = ftpServer;
    }

    @Override
    public void doWork() {
        ftpServer.setStopped(true);

        // easy way to interrupt accept method is by closing the serversocket forcibly.
        ftpServer.forceStop();
    }
}
