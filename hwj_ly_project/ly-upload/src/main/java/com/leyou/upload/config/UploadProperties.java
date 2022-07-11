package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author Hwj
 * @Date 2019/4/1 18:23
 * @Version 1.0.0
 **/
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getAllowTypes() {
        return allowTypes;
    }

    public void setAllowTypes(List<String> allowTypes) {
        this.allowTypes = allowTypes;
    }
}
