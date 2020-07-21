package com.agree.tlqAgent.utils;

import com.hc.client.FtpReturn;
import com.hc.client.JavaClientUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public enum FileUtil {
    INSTANCE;

    private static final String AFA5_FILEFACTORY_USER = "AFA5_FILEFACTORY_USER";
    private static final String FILEFACTORY_IP="FILEFACTORY_IP";
    private static final String FILEFACTORY_PORT="FILEFACTORY_PORT";
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void main(String[] args) {
        String res =
                FileUtil.INSTANCE.
                        downloadFileByURL("F:/", "https://www.processon.com/diagraming/5e981502e401fd262e1e0f4a");
        System.out.println(res);
    }

    private JavaClientUtil connectServer() {
        String ip = ConfigUtils.INSTANCE.getConfig(FILEFACTORY_IP,"");
        String port = ConfigUtils.INSTANCE.getConfig(FILEFACTORY_PORT,"");
        return new JavaClientUtil(ip, port, ConfigUtils.INSTANCE.getConfig(AFA5_FILEFACTORY_USER, "AFA5"), 3);
    }

    public String downloadFileByURL(String localFileDir, String url) {
        String fileName = null;
        try {
            fileName = localFileDir + getFileNameFromUrl(url);
            FileUtils.copyURLToFile(new URL(url), new File(fileName));
        } catch (Exception e) {
            logger.error("downloadFileByURL error:" + e.getMessage(), e);
        }
        return fileName;
    }

    public String upload_file_to_fileFactory(String localFilePath) {
        try {
            FtpReturn freturn = new FtpReturn();
            JavaClientUtil jcu = connectServer();
            jcu.securityFileUpload(localFilePath, freturn, 0);
            if (freturn.getVerdict() == 0) {
                return freturn.getCheckCode();
            }
            logger.error("upload_file_to_fileFactory fail:" + freturn.getMsg());
        } catch (Exception e) {
            logger.error("upload_file_to_fileFactory error:" + e.getMessage(), e);
        }
        return "";
    }

    public String getFileNameFromUrl(String url) {
        String name = Long.toString(System.currentTimeMillis());
        int index = url.lastIndexOf("/");
        if (index > 0) {
            name = url.substring(index + 1);
            if (name.trim().length() > 0) {
                return name;
            }
        }
        return name;
    }
}
