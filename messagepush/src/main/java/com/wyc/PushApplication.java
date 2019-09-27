package com.wyc;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.wyc.messagepush.service")
public class PushApplication {
    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class,args);
    }
}
