package com.ravi.ftp.fs;

import java.io.IOException;
import java.util.List;

/**
 * To list the files.
 * <br/>
 * For the first version, it will not be cached. Later on we can layer-in caching and recursive lookup and other such features
 */
public interface Lister {
    /**
     * List files in a folder. Folder will specified somewhere else.
     * <br/>
     * This is pure behavior
     *
     * @return empty list if there is nothing or exceptions.
     */
    List<String> list();

    String getFolderName();
}
