package com.ravi.ftp.cmd;

import com.ravi.ftp.fs.DirLister;
import com.ravi.ftp.fs.Lister;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.util.List;

/**
 * "index" command
 */
public class IndexCommand extends AbstractCommand {
    private final Path folder;
    private Lister lister;

    protected IndexCommand(BufferedWriter writer, Path folder, String cmdStr) {
        super(writer, cmdStr);
        this.folder = folder;
        this.lister = new DirLister(this.folder);
    }

    @Override
    public void doWork() {
        List<String> fileNames = lister.list();

        StringBuilder sb = new StringBuilder(256 * fileNames.size());
        fileNames.stream()
                .forEach(fn -> sb.append(fn).append(System.lineSeparator()));
        write(sb.toString());
    }

    //@VisibleForTesting
    protected void setLister(Lister lister) {
        this.lister = lister;
    }
}
