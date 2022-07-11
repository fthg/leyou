package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author: Lucifer
 * @create: 2018-10-12 01:32
 * @description:
 **/
@SpringBootApplication(exclude ={DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableZuulProxy /*启用zuul组件*/
public class LyGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class, args);
    }
}
