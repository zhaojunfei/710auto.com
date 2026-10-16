package com.auto710.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 文档配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI auto710OpenAPI() {
        return new OpenAPI().info(new Info()
                .title("710号汽车之家 数字化多端平台 API")
                .description("公众号 + 小程序 + PC官网 + H5 + 后台管理 + 接车技师端 统一接口文档")
                .version("1.0.0")
                .contact(new Contact().name("710号汽车之家").url("https://github.com/zhaojunfei/710auto.com")));
    }
}
