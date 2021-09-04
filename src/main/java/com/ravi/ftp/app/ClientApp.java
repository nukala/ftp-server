package com.ravi.ftp.app;


import com.ravi.ftp.utils.FtpServerUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.CharBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ravi.ftp.app.FtpServerApp.PORT;

public class ClientApp {
    private final Socket socket;
    private final String command;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String content = "";
    private AtomicBoolean doneWorking = new AtomicBoolean(false);

    public ClientApp(String[] args) {
        switch (args.length) {
            case 1:
                command = args[0];
                break;
            case 2:
                command = args[0] + " " + args[1];
                break;
            default:
                throw new ClientAppException("Too many arguments [" + String.join(", ", args));
        }

        this.socket = new Socket();
    }

    public static void main(String[] args) {
        ClientApp app = new ClientApp(args);
        app.doWork();

        System.out.printf("%s", app.getContent());
    }

    /**
     * Perform: connect, write command and read response
     */
    public void doWork() {
        try {
            performConnect();

            prepareForIO();

            writeCommand();
            content = readEntire(reader);
            doneWorking.compareAndSet(false, true);
        } finally {
            FtpServerUtils.close(socket);
        }
    }

    /** if available -- return content, else exception */
    public String getContent() {
        if (!doneWorking.get()) {
            throw new ClientAppException("Content not read yet, call doWork first");
        }

        return content;
    }

    private void prepareForIO() {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ioe) {
            throw new ClientAppException("preparing writer", ioe);
        }
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioe) {
            throw new ClientAppException("preparing reader", ioe);
        }
    }

    private void performConnect() {
        try {
            SocketAddress there = new InetSocketAddress(InetAddress.getLocalHost(), PORT);

            socket.connect(there);
            //System.err.printf("Connected using [%s] %n", socket.getLocalSocketAddress());
        } catch (IOException ioe) {
            throw new ClientAppException("while connecting", ioe);
        }
    }

    public static String readEntire(BufferedReader bufferedReader) {
        String entireString = "";
        try {
            CharBuffer buffer = CharBuffer.allocate(1024);
            while (bufferedReader.read(buffer) != -1) {
                buffer.flip();
                String got = buffer.toString();
                buffer.compact();
                entireString += got;
            }

            return entireString;
        } catch (IOException ioe) {
            throw new ClientAppException("while reading content", ioe);
        }
    }

    private void writeCommand() {
        try {
            writer.write(command);
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException ioe) {
            throw new ClientAppException("while writing command", ioe);
        }
    }

    public static class ClientAppException extends RuntimeException {
        public ClientAppException(String message) {
            super(message);
        }

        public ClientAppException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
