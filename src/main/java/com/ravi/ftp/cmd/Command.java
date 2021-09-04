package com.ravi.ftp.cmd;

/**
 * Behavior exposed by the implementation.
 */
public interface Command {
    default void doWork() {
        throw new IllegalStateException("PROGRAMMING-ERROR: needs to implemented");
    }

    String getCommandStr();
}
