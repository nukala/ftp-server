package com.ravi.ftp.srvr;

import com.ravi.ftp.utils.FtpServerUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Blocking IO ftp-server.
 */
public class FtpServerImpl implements FtpServer {
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private int port;
    private Path folderPath;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public FtpServerImpl() {
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public int getPort() {
        return port;
    }

    protected void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean isStopped() {
        return stopped.get();
    }

    @Override
    public void setStopped(boolean stopped) {
        this.stopped.compareAndSet(false, stopped);
    }

    @Override
    public Path getFolderPath() {
        return folderPath;
    }

    @Override
    public void initialize(String[] args) {
        if (FtpServerUtils.isEmpty(args)) {
            throw new FtpServerException("Folder-name argument is required");
        }

        String folder = args[0];
        Path folderPath = Paths.get(folder);
        FtpServerUtils.validateFolder(folderPath);
        this.folderPath = folderPath;
    }

    @Override
    public void open(int port) {
        if ((port < 0) || (port > 65535)) {
            throw new InvalidPortException(String.format("port=%d is not allowed", port));
        }

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioe) {
            throw new FtpServerException(ioe);
        }

        setPort(serverSocket.getLocalPort());
        System.err.printf("Opened port=%d %n", getPort());
    }

    @Override
    public void start() {
        if (isStopped()) {
            return;
        }

        while (!isStopped()) {
            try {
                Socket clientSocket = serverSocket.accept();

                //System.err.printf("Processing [%s]%n", clientSocket.getRemoteSocketAddress());
                process(clientSocket);
            } catch (SocketException se) {
                if (!isStopped()) {
                    System.out.printf("Socket exception while stopped=%b ?", isStopped());
                    setStopped(true);
                }
            } catch (IOException ioe) {
                if (!isStopped()) {
                    setStopped(true);
                }
                System.err.printf("processing failed [%s]%n", ioe.getMessage());
                break;
            }
        }

        System.out.printf("Shutting down entire server%n");
        this.pool.shutdown();
    }

    @Override
    public void forceStop() {
        // easy way to forcibly stop the serve, when thread is in a blocking-accept
        FtpServerUtils.close(serverSocket);
    }

    private void process(Socket clientSocket) {
        Request req = new Request(this, clientSocket);

        // exception in single thread/client should not stop the system !
        try {
            this.pool.submit(req);
        } catch (Request.RequestFailedException e) { // ignored
        }
    }
}
