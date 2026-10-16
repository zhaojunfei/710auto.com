package com.auto710.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 敏感词检测工具（DFA 简化实现，词典来自 classpath:sensitive-words.txt）
 * <p>用于服务项目描述、文章、留言等内容的合规校验。</p>
 */
@Slf4j
public final class SensitiveWordUtil {

    private static final Set<String> WORDS = new HashSet<>();
    private static volatile boolean loaded = false;

    private SensitiveWordUtil() {
    }

    /**
     * 加载词库
     */
    public static synchronized void load() {
        if (loaded) {
            return;
        }
        try {
            ClassPathResource resource = new ClassPathResource("sensitive-words.txt");
            if (resource.exists()) {
                List<String> lines = IoUtil.readLines(resource.getInputStream(), StandardCharsets.UTF_8, new java.util.ArrayList<>());
                lines.stream()
                        .map(String::trim)
                        .filter(StrUtil::isNotBlank)
                        .filter(l -> !l.startsWith("#"))
                        .forEach(WORDS::add);
            }
        } catch (Exception e) {
            log.warn("敏感词库加载失败: {}", e.getMessage());
        }
        loaded = true;
    }

    /**
     * 是否包含敏感词
     */
    public static boolean contains(String text) {
        return firstMatch(text) != null;
    }

    /**
     * 返回首个命中敏感词
     */
    public static String firstMatch(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        load();
        for (String word : WORDS) {
            if (text.contains(word)) {
                return word;
            }
        }
        return null;
    }

    /**
     * 敏感词替换为 *
     */
    public static String replace(String text) {
        if (StrUtil.isBlank(text)) {
            return text;
        }
        load();
        String result = text;
        for (String word : WORDS) {
            if (result.contains(word)) {
                result = result.replace(word, "*".repeat(word.length()));
            }
        }
        return result;
    }

    public static void addWord(String word) {
        if (StrUtil.isNotBlank(word)) {
            WORDS.add(word.trim());
        }
    }
}
