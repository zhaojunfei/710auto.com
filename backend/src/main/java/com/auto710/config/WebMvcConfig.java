package com.auto710.config;

import com.auto710.security.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Web 配置：跨域、登录拦截器、本地存储静态资源、国际化 Locale 解析
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 框架级公开接口：随代码发布，不依赖各环境 yml 白名单配置。
     * （yml 中的 white-list 用于环境相关的额外放行项）
     */
    private static final List<String> BUILT_IN_WHITE_LIST = List.of(
            "/api/supplier/login"
    );

    @Value("${auto710.security.white-list}")
    private List<String> whiteList;

    @Value("${auto710.storage.local.base-path:./data/upload}")
    private String localBasePath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> merged = new java.util.ArrayList<>(whiteList == null ? List.of() : whiteList);
        BUILT_IN_WHITE_LIST.forEach(p -> {
            if (!merged.contains(p)) {
                merged.add(p);
            }
        });
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(merged);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = new File(localBasePath).getAbsolutePath();
        registry.addResourceHandler("/upload/local/**")
                .addResourceLocations("file:" + path + File.separator);
    }

    /**
     * 前台语言切换：优先 lang 请求头 / 参数
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return resolver;
    }
}
