package com.itactic.core.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 1Zx.
 * @date 2021/1/13 15:24
 */
@Configuration("aliOSSConfig")
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enable", havingValue = "true")
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliYunOSSConfig {

    private static String endpoint;
    private static String bucketName;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String mainPath = "";

    public void setEndpoint(String endpoint) {
        AliYunOSSConfig.endpoint = endpoint;
    }

    public void setBucketName(String bucketName) {
        AliYunOSSConfig.bucketName = bucketName;
    }

    public void setAccessKeyId(String accessKeyId) {
        AliYunOSSConfig.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AliYunOSSConfig.accessKeySecret = accessKeySecret;
    }

    public void setMainPath(String mainPath) {
        if (StringUtils.isNotBlank(mainPath) && mainPath.startsWith("/")) {
            mainPath = mainPath.substring(1);
        }
        AliYunOSSConfig.mainPath = mainPath;
    }

    public static String getEndpoint() {
        return endpoint;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    public static String getAccessKeySecret() {
        return accessKeySecret;
    }

    public static String getMainPath() {
        return mainPath;
    }
}
