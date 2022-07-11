package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: Lucifer
 * @create: 2018-10-12 01:14
 * @description:
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})// 排除数据源的注入
@EnableEurekaServer  //启动Eureka服务
public class LyRegistry {
    public static void main(String[] args) {
        SpringApplication.run(LyRegistry.class, args);
    }
}
