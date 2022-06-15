package com.hj.impl;

import com.hj.core.FtpClient;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

public class Sftp implements FtpClient {

    private Logger logger = LoggerFactory.getLogger(Sftp.class);

    private ChannelSftp sftp;
    private Session sshSession;

    private String userName;
    private String passWord;
    private String host;
    private int port;

    public Sftp(String  userName, String passWord, String host, int port) {
        this.userName = userName;
        this.passWord = passWord;
        this.host = host;
        this.port = port;
    }

    /**
     * @return
     */
    private boolean getClient() {
        boolean isLoginSuccess = false;
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(userName, host, port);
            sshSession.setPassword(passWord);//密码
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(properties);
            sshSession.setTimeout(30000); // 设置timeout时间
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            logger.info("sftp connected success");
            isLoginSuccess = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            isLoginSuccess = false;
        }
        return isLoginSuccess;
    }

    @Override
    public boolean upload(String remotePath, String fileName, String localPath) throws SftpException, FileNotFoundException {
        boolean isUploadSuccess = false;
        if (sftp == null) {
            getClient();
        }
        try {
            //目录不存在则创建
            if (!isDirExist(remotePath)) {
                sftp.mkdir(remotePath);
            }
            sftp.cd(remotePath);
            File file = new File(localPath);
            sftp.put(new FileInputStream(file), fileName);
            isUploadSuccess = true;
            logger.info("上传文件 {} 到 {} 成功", fileName, remotePath);
        } catch (Exception e) {
            logger.info("上传文件 {} 到 {} 失败,原因 {} ", fileName, remotePath, e.getMessage());
            isUploadSuccess = false;
        } finally {
            sshSession.disconnect();
            sftp.disconnect();
            sshSession=null;
            sftp=null;
        }
        return isUploadSuccess;
    }





    @Override
    public boolean download(String remotePath, String fileName, String localPath) {
        boolean isDownloadSuccess = false;
        if (sftp == null) {
            getClient();
        }
        try {
            sftp.cd(remotePath);
            File file = new File(localPath);
            sftp.get(fileName, new FileOutputStream(file));
            isDownloadSuccess = true;
            logger.info("从 {} 下载文件 {} 成功", remotePath, fileName);
        } catch (Exception e) {
            logger.info("从 {} 下载文件 {} 失败,原因 {}", remotePath, fileName, e.getMessage());
            isDownloadSuccess = false;
        }finally {
            sshSession.disconnect();
            sftp.disconnect();
            sshSession=null;
            sftp=null;
        }
        return isDownloadSuccess;
    }

    @Override
    public boolean move(String oldPath, String newPath) {

        return false;
    }

    @Override
    public boolean delete(String directory) {
        if (sftp == null) {
            getClient();
        }
        try {
            if (isDirExist(directory)) {
                Vector<ChannelSftp.LsEntry> vector = sftp.ls(directory);
                if (vector.size() == 1) { // ⽂件，直接删除
                    sftp.rm(directory);
                } else if (vector.size() == 2) { // 空⽂件夹，直接删除
                    sftp.rmdir(directory);
                } else {
                    String fileName = "";
                    // 删除⽂件夹下所有⽂件
                    for (ChannelSftp.LsEntry en : vector) {
                        fileName = en.getFilename();
                        if (".".equals(fileName) || "..".equals(fileName)) {
                            continue;
                        } else {
                            delete(directory + "/" + fileName);
                        }
                    }
                    // 删除⽂件夹
                    sftp.rmdir(directory);
                }
                logger.info("删除文件 {} 成功", directory);
            } else {
                logger.info("文件 {} 不存在", directory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("删除文件 {} 失败", directory);
        }finally {
            sshSession.disconnect();
            sftp.disconnect();
            sshSession=null;
            sftp=null;
        }
        return false;
    }


    private boolean isDirExist(String directory) {
        try {
            Vector<?> vector = sftp.ls(directory);
            if (null == vector) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
