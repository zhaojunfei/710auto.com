package com.auto710;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 710号汽车之家 数字化多端平台 后端启动类
 */
@SpringBootApplication
@MapperScan("com.auto710.mapper")
@EnableScheduling
@EnableAsync
public class AutoRepairApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoRepairApplication.class, args);
        System.out.println("""
                ==========================================================
                  710号汽车之家 数字化多端平台 后端启动成功
                  API 文档: http://localhost:8080/swagger-ui/index.html
                ==========================================================
                """);
    }
}
