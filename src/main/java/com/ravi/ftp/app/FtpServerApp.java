package com.ravi.ftp.app;

import com.ravi.ftp.srvr.FtpServer;
import com.ravi.ftp.srvr.FtpServerImpl;

//Task: Write a simple network file server and its client.
//
//        Requirements for the server:
//        -The server can serve a list of available text files from a specified directory. The files can be
//        downloaded by the client.
//        -Acceptable commands from the client are
//        + "index" to list out all the available files
//        + "get <file-name>". Response with either "ok" or "error"
//        The "ok" message will be followed with the content of the file
//        The "error" message indicates that the specified file does not exist on the server.
//        + “quit” or “q” to exit the program
//        + For other commands, the server gives "unknown command" response.
//        - It should accept a command-line parameter at start up specifying the directory that contains the file that the server can serve.
//        - Listening PORT can be a constant within the code base.
//        - Use of sockets over TCP/IP
//
//        Things we are looking for :
//        - Server should be able to handle multiple incoming connections.
//        - A client program is included to run application from command line and in integration tests.
//        - Proper error handling.
//        - Full test coverage.
public class FtpServerApp {
    public static final int PORT = 6103;
    private FtpServer server;

    public FtpServerApp() {
        this.server = new FtpServerImpl();
    }

    /**
     * Takes one command-line-argument, the folder to serve.
     *
     * @param args
     */
    public static void main(String[] args) {
        FtpServerApp app = new FtpServerApp();

        // start/run are also used with Threads
        app.begin(args);
    }

    public void begin(String[] args) {
        try {
            server.initialize(args);

            server.open(PORT);
        } catch (FtpServerImpl.FtpServerException fse) {
            System.err.printf("Could not start [%s]%n", fse.getMessage());
            System.exit(1);
        }

        server.start();
    }
}
