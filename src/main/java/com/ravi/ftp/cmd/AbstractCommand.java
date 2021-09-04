package com.ravi.ftp.cmd;

import com.ravi.ftp.srvr.Request.RequestFailedException;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Abstract parent class for common-behavior exposed by almsot all commands.
 */
public class AbstractCommand implements Command {
    // arbitrarily chose - 5 seconds
    private static final int WRITE_THRESHOLD_MILLIS = 5_000;

    private final String cmdStr;
    private final BufferedWriter writer;

    protected AbstractCommand(BufferedWriter writer, String cmdStr) {
        this.writer = writer;
        this.cmdStr = cmdStr;
    }

    // to test the exception and finally portion of this code,
    //  I need to write another meaningless-helper and mock.
    // skipping that for now.
    protected void write(String str) {
        long timer = -System.currentTimeMillis();
        try {
            writer.write(str);
            writer.flush();
            //System.err.printf("%s: Wrote %d bytes.%n", getCommandStr(), str.length());
        } catch (IOException ioe) {
            throw new RequestFailedException("cmd=" + getCommandStr() + " failed.", ioe);
        } finally {
            timer += System.currentTimeMillis();
            if (timer > WRITE_THRESHOLD_MILLIS) {
                // usually logger.warn
                System.err.printf("TOO-SLOW: write consumed %d ms %n", timer);
            }
        }
    }

    @Override
    public String getCommandStr() {
        return cmdStr;
    }
}
