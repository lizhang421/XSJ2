package com.example.XSJ.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Random;

import static com.example.XSJ.utils.RegexPatterns.DEFAULT_POINTS;

public class CaptchaUtils {
    // 验证码字符集（排除易混淆字符：0/O、1/I 减少识别问题）
    private static final String CHARACTERS = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final Random random = new Random();

    // 默认配置（优化宽度/高度比例，更适配4位字符）
    private static final int DEFAULT_WIDTH = 120;  // 原200过宽，4位字符120更紧凑
    private static final int DEFAULT_HEIGHT = 40;
    private static final int DEFAULT_LENGTH = 4;
    private static final int DEFAULT_LINES = 10;


    /**
     * 生成图片返回stream流
     */
    public static String generateCaptcha(OutputStream outputStream) {
        return generateCaptcha(outputStream, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LENGTH);
    }

    /**
     * 验证码生成（修复字符越界问题）
     */
    public static String generateCaptcha(OutputStream outputStream, int width, int height, int length) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        int fontSize = height - 10;
        g2d.setFont(new Font("宋体", Font.BOLD, fontSize));
        StringBuilder captchaText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            captchaText.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        int margin = 10;
        int charWidth = (width - 2 * margin) / length;
        int baseY = height / 2 + fontSize / 2 - 2;

        for (int i = 0; i < length; i++) {
            g2d.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(200)));
            int charX = margin + i * charWidth + 2;
            int rotateAngle = random.nextInt(40) - 20; // -20 ~ +20
            double rotateX = charX + charWidth / 2.0;
            double rotateY = height / 2.0;
            g2d.rotate(Math.toRadians(rotateAngle), rotateX, rotateY);
            g2d.drawString(String.valueOf(captchaText.charAt(i)),
                    Math.max(2, charX),
                    Math.min(baseY, height - 2));
            g2d.rotate(Math.toRadians(-rotateAngle), rotateX, rotateY);
        }
        for (int i = 0; i < DEFAULT_LINES; i++) {
            g2d.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g2d.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }
        for (int i = 0; i < DEFAULT_POINTS; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.fillRect(random.nextInt(width), random.nextInt(height), 1, 1);
        }

        // 9. 释放资源
        g2d.dispose();

        // 10. 写入输出流
        try {
            ImageIO.write(image, "PNG", outputStream);
        } catch (Exception e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }

        return captchaText.toString();
    }
}