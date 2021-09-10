package com.ravi.ftp.srvr;

import java.nio.file.Path;

/**
 * Behavior exposed by our "ftp-server"
 */
public interface FtpServer {
    int getPort();

    boolean isStopped();

    void setStopped(boolean stopped);

    Path getFolderPath();

    /**
     * Initialize the server object.
     * <br/>
     * Ideally, we should populate an Errors object with individual error and return a boolean. But this is a simple-app (hence the weird name).
     *
     * @param args
     * @return empty if there is no error
     * else error string.
     */
    void initialize(String[] args);

    /**
     * Creates a server socket and sets up some state
     */
    void open(int port);

    /**
     * Starts the server, begins accepting connections on the main-thread.
     * <br/>
     * Each request after acceptance will be handled in a thread-pool.
     */
    void start();

    /**
     * Forcibly try to stop the server
     */
    void forceStop();

    /**
     * We have a series of highly specialized un-caught exceptions here.
     * <br/>
     * Given the nature of a simple-app, this is easier. In a commercial app, these will be error codes and Errors class and such.
     */
    class FtpServerException extends RuntimeException {
        public FtpServerException(String message) {
            super(message);
        }

        public FtpServerException(Throwable cause) {
            super(cause);
        }

        public FtpServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class InvalidFolderException extends FtpServerException {
        public InvalidFolderException(String message) {
            super(message);
        }
    }

    class InvalidPortException extends FtpServerException {
        public InvalidPortException(String message) {
            super(message);
        }
    }
}
