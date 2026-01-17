package com.churchsoft.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3Properties {

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

}
