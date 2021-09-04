package com.ravi.ftp.srvr;

import com.ravi.ftp.cmd.Command;
import com.ravi.ftp.cmd.Commands;
import com.ravi.ftp.cmd.Commands.CommandException;
import com.ravi.ftp.utils.FtpServerUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class Request implements Callable<Void> {
    private static final int TOO_SLOW_THRESHOLD = 12_000; // arbitrarily chose - 12 seconds
    private FtpServer ftpServer;
    private Socket clientSocket;

    public Request(FtpServer ftpServer, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.ftpServer = ftpServer;
    }

    @Override
    public Void call() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        Command command = null;
        long timer = -System.currentTimeMillis();

        try {
            reader = getReader();
            writer = getWriter();

            command = readAndLocateCommand(ftpServer, reader, writer);
            //System.err.printf("performing [%s] %n", command.getCommandStr());
            command.doWork();
        } catch (IOException | CommandException e) {
            throw new RequestFailedException(e);
        } finally {
            FtpServerUtils.close(writer);
            FtpServerUtils.close(reader);
            timer += System.currentTimeMillis();
            if (timer > TOO_SLOW_THRESHOLD) {
                // usually logger.warn
                System.err.printf("TOO-SLOW: processing[%s] consumed %d ms %n", command == null ? "no-command" : command.getCommandStr(), timer);
            }
            //System.out.printf("%s: consumed %d ms %n", command.getCommandStr(), timer);
        }
        // dont care about return value.
        return null;
    }

    private BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
    }

    private BufferedWriter getWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
    }

    private Command readAndLocateCommand(FtpServer ftpServer, BufferedReader reader, BufferedWriter writer) throws IOException {
        String cmdStr = reader.readLine();

        return Commands.makeCommand(ftpServer, writer, cmdStr);
    }

    public static class RequestFailedException extends FtpServer.FtpServerException {
        public RequestFailedException(Throwable cause) {
            super(cause);
        }

        public RequestFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
