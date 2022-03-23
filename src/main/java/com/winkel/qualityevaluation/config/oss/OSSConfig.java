package com.winkel.qualityevaluation.config.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "oss")
@Data
public class OSSConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String urlPrefix;


    @Bean
    public OSSClient oSSClient() {

        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);

        CredentialsProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        return new OSSClient(endpoint, provider, config);
    }

}
