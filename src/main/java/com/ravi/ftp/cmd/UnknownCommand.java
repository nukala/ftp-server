package com.ravi.ftp.cmd;

import java.io.BufferedWriter;

// responds with -- "unknown command"
public class UnknownCommand extends AbstractCommand {
    protected UnknownCommand(BufferedWriter writer, String cmdStr) {
        super(writer, cmdStr);
    }

    @Override
    public void doWork() {
        write("unknown command");
    }
}
