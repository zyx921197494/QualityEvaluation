package com.winkel.qualityevaluation.config.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "sts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class STSConfig {

    private String endpoint;

    private String endpointName;

    private String regionId;

    private String ramAccessKeyId;

    private String ramAcessKeySecret;

    private String roleArn;

    @Bean
    public STSConfig StsConfig() {
        return new STSConfig(endpoint, endpointName, regionId, ramAccessKeyId, ramAcessKeySecret, roleArn);
    }


}
