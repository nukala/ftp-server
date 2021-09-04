package com.ravi.ftp.cmd;

import com.ravi.ftp.srvr.FtpServer;
import com.ravi.ftp.srvr.FtpServer.FtpServerException;
import com.ravi.ftp.utils.FtpServerUtils;

import java.io.BufferedWriter;

public class Commands {
    /**
     * Build a Command object from the command-string
     * <br/>
     *
     * @param ftpServer
     * @param writer
     * @param str
     * @return Always a command object.
     */
    public static Command makeCommand(FtpServer ftpServer, BufferedWriter writer, String str) {
        if (FtpServerUtils.isEmpty(str)) {
            System.err.printf("commandString is empty [%s]%n", str);
            return new UnknownCommand(writer, "empty-string");
        }
        String cmdStr = str.trim();
        if (cmdStr.equals("index")) {
            return new IndexCommand(writer, ftpServer.getFolderPath(), cmdStr);
        } else if (cmdStr.startsWith("get ")) {
            return new GetCommand(writer, ftpServer.getFolderPath(), cmdStr);
        } else if (cmdStr.equals("quit") || cmdStr.equals("q")) {
            return new QuitCommand(ftpServer, writer, cmdStr);
        }
        return new UnknownCommand(writer, cmdStr);
    }

    public static class CommandException extends FtpServerException {
        public CommandException(String message) {
            super(message);
        }
    }
}
