package com.example.XSJ.utils;

import java.util.UUID;

/**
 * ID生成工具类：生成VI开头的32位唯一ID
 */
public class IdGeneratorUtil {

    /**
     * 生成VI开头的32位唯一ID
     * 规则：VI + 30位随机字符（UUID去横线后截取30位）
     * 总长度：2(VI) + 30 = 32位
     * @return 32位唯一ID（示例：VI5f8d7e9c8b7a6s5d4f3g2h1j9k8l7m6n5）
     */
    public static String generateVI32Id() {
        // 1. 生成UUID并去掉横线（32位）
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 2. 截取前30位，拼接VI前缀，总长度32位
        String viId = "VI" + uuid.substring(0, 30);
        // 3. 确保长度严格32位（兜底校验）
        if (viId.length() != 32) {
            throw new RuntimeException("生成VI32位ID失败，长度异常：" + viId.length());
        }
        return viId;
    }
}