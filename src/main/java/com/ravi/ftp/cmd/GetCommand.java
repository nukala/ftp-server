package com.ravi.ftp.cmd;

import com.ravi.ftp.cmd.Commands.CommandException;
import com.ravi.ftp.fs.DirLister;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * "get file_name" behavior
 */
public class GetCommand extends AbstractCommand {
    private final Path folder;
    private final String fileName;

    protected GetCommand(BufferedWriter writer, Path folder, String cmdStr) {
        super(writer, cmdStr);
        this.folder = folder;
        String[] vals = cmdStr.split(" ");
        if (vals.length != 2) {
            writeError();
            throw new CommandException("handles only 2 parameters");
        }

        this.fileName = vals[1];
    }

    @Override
    public void doWork() {
        if (!DirLister.isAllowed(fileName)) {
            writeError();
            throw new CommandException(fileName + ": not allowed");
        }
        Path pathToGet = Paths.get(folder.toString(), fileName);
        try {
            String entire = new String(Files.readAllBytes(pathToGet), StandardCharsets.UTF_8);

            writeOk();
            write(entire);
        } catch (IOException ioe) {
            writeError();
            System.err.printf("IOException while reading entire file(%s)%n", pathToGet);
        }
    }

    private void writeLine(String str) {
        write(str + System.lineSeparator());
    }

    private void writeOk() {
        writeLine("ok");
    }

    private void writeError() {
        writeLine("error");
    }
}
