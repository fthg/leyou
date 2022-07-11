package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Hwj
 * @Date 2019/4/19 13:41
 * @Version 1.0.0
 * 这个微服务用于用户页面      不是管理页哦
 **/
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class LyPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class);
    }
}
