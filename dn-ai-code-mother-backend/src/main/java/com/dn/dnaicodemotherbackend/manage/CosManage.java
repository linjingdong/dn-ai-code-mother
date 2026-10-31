package com.dn.dnaicodemotherbackend.manage;

import com.dn.dnaicodemotherbackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/31 02:24
 **/
@Component
@Slf4j
public class CosManage {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     * @param key 唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    public String uploadFile(String key, File file) {
        // 上传文件
        PutObjectResult result = putObject(key, file);
        if (null != result) {
            // 构建访问URL
            String url = String.format("%s%s", cosClientConfig.getHost(), key);
            log.info("文件上传 COS 成功：{} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传 COS 失败");
            return null;
        }
    }
}
