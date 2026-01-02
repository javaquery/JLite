package com.javaquery.ftp;

/**
 * @author javaquery
 * @since 1.0.0
 */
public interface FileFilter<RemoteFile> {
    boolean accept(RemoteFile file);
}
