package com.hj.config;

import com.hj.core.FtpClient;
import com.hj.impl.Sftp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FtpAutoConfig {
    @Value("${ftp.name:root}")
    private String userName;
    @Value("${ftp.passwd:root}")
    private String passWord;
    @Value("${ftp.host:localhost}")
    private String host;
    @Value("${ftp.port:22}")
    private int port;

    @Bean
    FtpClient getFtpClient(){
        return new Sftp(userName, passWord, host, port);
    }

}
