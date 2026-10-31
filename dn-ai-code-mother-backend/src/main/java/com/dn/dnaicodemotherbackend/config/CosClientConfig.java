package com.dn.dnaicodemotherbackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云 COS 配置类
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/10/31 02:20
 **/
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    private String host;

    private String secretId;

    private String secretKey;

    private String region;

    private String bucket;

    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(this.secretId, this.secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(this.region));
        return new COSClient(cred, clientConfig);
    }
}
