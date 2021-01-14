package com.itactic.core.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;
import java.util.UUID;


/**
 * @author 1Zx.
 * @date 2020/5/16 14:54
 * @see AliYunOSSConfig 通过 aliyun.oss.enable=true 启用上传工具
 * @description 阿里云OSS上传工具
 */
@DependsOn("aliOSSConfig")
@Scope("singleton")
@Component
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enable", havingValue = "true")
public class AliYunOSS {

    private final Logger logger = LoggerFactory.getLogger(AliYunOSS.class);

    public AliYunOSS () {
        logger.info("Ali-OSS-EndPoint：【{}】", AliYunOSSConfig.getEndpoint());
        logger.info("Ali-OSS-BucketName：【{}】", AliYunOSSConfig.getBucketName());
        logger.info("Ali-OSS-AccessKeyId：【{}】", AliYunOSSConfig.getAccessKeyId());
        logger.info("Ali-OSS-AccessKeySecret：【{}】", AliYunOSSConfig.getAccessKeySecret());
        logger.info("Ali-OSS-MainPath：【{}】", AliYunOSSConfig.getMainPath());
    }

    private OSS OSS() {
        return new OSSClient(AliYunOSSConfig.getEndpoint(), AliYunOSSConfig.getAccessKeyId(), AliYunOSSConfig.getAccessKeySecret());
    }

    /**
     * @return fileUrl
     * */
    public String save (MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Calendar calendar = Calendar.getInstance();
        String mainPath = AliYunOSSConfig.getMainPath();
        mainPath = mainPath.endsWith("/") ? mainPath : mainPath + "/";
        String path =  mainPath + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/";
        String result = path +  fileName;
        OSS oss = null;
        try {
            oss = OSS();
            if (oss.doesBucketExist(AliYunOSSConfig.getBucketName())) {
                logger.info("--------Bucket：【{}】已存在--------", AliYunOSSConfig.getBucketName());
            } else {
                logger.info("--------创建Bucket：【{}】--------", AliYunOSSConfig.getBucketName());
                oss.createBucket(AliYunOSSConfig.getBucketName());
            }
            oss.putObject(AliYunOSSConfig.getBucketName(), result, file.getInputStream());
        } catch (Exception e) {
            logger.error("--------上传文件发生错误：【{}】--------",e.getMessage());
            return null;
        } finally {
            if (null != oss) {
                oss.shutdown();
            }
        }
        return result;
    }

    public String save(File file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getName();
        Calendar calendar = Calendar.getInstance();
        String mainPath = AliYunOSSConfig.getMainPath();
        String path =  mainPath + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/";
        String result = path +  fileName;
        OSS oss = null;
        try {
            oss = OSS();
            if (oss.doesBucketExist(AliYunOSSConfig.getBucketName())) {
                logger.info("--------Bucket：【{}】已存在--------", AliYunOSSConfig.getBucketName());
            } else {
                logger.info("--------创建Bucket：【{}】--------", AliYunOSSConfig.getBucketName());
                oss.createBucket(AliYunOSSConfig.getBucketName());
            }
            oss.putObject(AliYunOSSConfig.getBucketName(), result, new FileInputStream(file));
        } catch (Exception e) {
            logger.error("----上传文件发生错误：【{}】----",e.getMessage());
            return null;
        } finally {
            if (null != oss) {
                oss.shutdown();
            }
        }
        return result;
    }

    public String getInputStream (String fileUrl) {
        OSS oss = null;
        OSSObject ossObject = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            oss = OSS();
            String fileName = UUID.randomUUID().toString().replace("-","_");
            String result = System.getProperty("java.io.tmpdir") + File.separator;
            File file = new File(result);
            if (!file.exists()) {
                file.mkdirs();
            }
            os = new FileOutputStream(result + fileName);
            ossObject = oss.getObject(AliYunOSSConfig.getBucketName(), fileUrl);
            byte[] bytes = new byte[2048];
            int len;
            is = ossObject.getObjectContent();
            while (-1 != (len = is.read(bytes))) {
                os.write(bytes, 0, len);
            }
            return result + fileName;
        } catch (Exception e) {
            logger.error("---------获取Ali-OSS文件流错误：【{}】--------", e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(ossObject);
            if (null != oss) {
                oss.shutdown();
            }
        }
        return null;
    }

    /** 检查文件是否存在 */
    public boolean checkFile (String fileUrl) {
        OSS oss = null;
        try {
            oss = OSS();
            return oss.doesObjectExist(AliYunOSSConfig.getBucketName(), fileUrl, true);
        } catch (Exception e) {
            logger.error("--------检查Ali-OSS文件是否存在错误：【{}】--------", e.getMessage());
        } finally {
            if (null != oss) {
                oss.shutdown();
            }
        }
        return false;
    }

    public ObjectMetadata getFile(String fileUrl){
        OSS oss = null;
        try {
            oss = OSS();
            return oss.getObjectMetadata(AliYunOSSConfig.getBucketName(), fileUrl);
        } catch (Exception e){
            logger.error("--------获取Ali-OSS文件错误：【{}】--------", e.getMessage());
        } finally {
            if (null != oss) {
                oss.shutdown();
            }
        }
        return null;
    }

}
