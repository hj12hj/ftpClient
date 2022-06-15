package com.hj.core;

import com.jcraft.jsch.SftpException;

import java.io.FileNotFoundException;

/**
 * Ftp  Sftp 操作客户端
 */
public interface FtpClient {


    /**
     * @param remotePath
     * @param localPath
     * @return whether upload success
     */
    boolean upload(String remotePath,String fileName ,String localPath) throws SftpException, FileNotFoundException;

    /**
     * @param remotePath
     * @param localPath
     * @return whether upload success
     */
    boolean download(String remotePath,String fileName ,String localPath);


    /**
     * @param oldPath
     * @param newPath
     * @return whether move success
     */
    boolean move(String oldPath,String newPath);


    /**
     * @param remotePath
     * @return whether delete success
     */
    boolean delete(String remotePath);


}
