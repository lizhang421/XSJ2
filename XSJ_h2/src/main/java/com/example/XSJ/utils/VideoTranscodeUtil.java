package com.example.XSJ.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.XSJ.utils.RegexPatterns.*;



@Slf4j
@Component
public class VideoTranscodeUtil {

    // 初始化临时文件的目录不存在就创建
    static {
        File tempDir = new File(FILE_ADDRESS);
        if (!tempDir.exists()) {
            boolean tempCreated = tempDir.mkdirs();
            log.info("临时目录创建{}：{}", tempCreated ? "成功" : "失败", tempDir.getAbsolutePath());
        }

        File endDir = new File(END_ADDRESS);
        if (!endDir.exists()) {
            boolean endCreated = endDir.mkdirs();
            log.info("转码目录创建{}：{}", endCreated ? "成功" : "失败", endDir.getAbsolutePath());
        }
    }

    /**
     * 接收视频流文件，转码为多分辨率并存储
     * @param multipartFile 接口接收的视频文件（MultipartFile）
     * @param videoId 视频唯一ID
     * @return 所有分辨率转码是否都成功
     */
    public static boolean transcodevideo(MultipartFile multipartFile, String videoId) {
        // 生成临时文件名称
        String tempFileName = UUID.randomUUID().toString() + "_temp.mp4";
        File tempFile = new File(FILE_ADDRESS + tempFileName);
        try {
            // 接口接收的视频流文件写入临时文件
            multipartFile.transferTo(tempFile);
            log.info("视频资源落地临时文件完成：{}", tempFile.getAbsolutePath());

            // 定义需要转码的分辨率
            String[][] resolutions = {
                    {"480p", "854x480"},
                    {"720p", "1280x720"},
                    {"1080p", "1920x1080"}
            };

            // 开始转码
            boolean allSuccess = true;
            for (String[] res : resolutions) {
                String resName = res[0];
                String resSize = res[1];

                String outputPath = END_ADDRESS + videoId + "_" + resName + ".mp4";
                log.info("开始转码{}分辨率，输出路径：{}", resName, outputPath);

                // 调用ffmpeg转码
                boolean transcodeSuccess = executeFFmpegTranscode(tempFile.getAbsolutePath(), outputPath, resSize);
                if (!transcodeSuccess) {
                    allSuccess = false;
                    log.error("{}分辨率转码失败", resName);
                    break;
                }
                log.info("{}分辨率转码完成，存储路径：{}", resName, outputPath);
            }
            return allSuccess;

        } catch (IOException e) {
            log.error("视频流落地临时文件失败：{}", e.getMessage(), e);
            return false;
        } finally {
            // 修复：延迟删除临时文件（避免转码未完成就删除），或根据转码结果决定是否删除
            if (tempFile.exists()) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                boolean deleted = FileUtils.deleteQuietly(tempFile);
                log.info("临时文件删除{}：{}", deleted ? "成功" : "失败", tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * 执行ffmpeg转码 拼接转码指令 本地要部署软件
     * @param inputPath 输入临时文件目录
     * @param outputPath 输出文件路径
     * @param resolution 分辨率
     * @return 转码是否成功
     */

    private static Boolean executeFFmpegTranscode(String inputPath, String outputPath, String resolution) {
        List<String> command = new ArrayList<>();
        command.add(FFMPEG_ADDRESS);          // ffmpeg可执行文件路径
        command.add("-i");                    // 输入文件标识
        command.add(inputPath);               // 输入文件路径
        command.add("-s");                    // 分辨率标识
        command.add(resolution);              // 目标分辨率
        command.add("-c:v");
        command.add("h264");                  // 视频编码器
        command.add("-crf");
        command.add("23");                    // 画质参数
        command.add("-c:a");                  // 音频编码标识
        command.add("aac");                   // 音频编码器
        command.add("-y");                    // 覆盖已存在的输出文件
        command.add(outputPath);              // 输出文件路径

        log.info("执行FFmpeg命令：{}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
            readProcessOutput(process.getInputStream());
            boolean finished = process.waitFor(300, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                log.error("FFmpeg转码超时（300秒），已终止进程");
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("FFmpeg转码失败，退出码：{}", exitCode);
                return false;
            }
            File outputFile = new File(outputPath);
            if (!outputFile.exists() || outputFile.length() == 0) {
                log.error("转码输出文件不存在或为空：{}", outputPath);
                return false;
            }

            return true;
        } catch (IOException e) {
            log.error("FFmpeg进程启动失败：{}", e.getMessage(), e);
            return false;
        } catch (InterruptedException e) {
            log.error("FFmpeg转码被中断：{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (process != null) {
                process.destroy(); // 确保进程终止
            }
        }
    }

    /**
     * 异步读取FFmpeg输出流（日志），避免缓冲区堵塞，同时捕获详细报错
     */
    private static void readProcessOutput(java.io.InputStream inputStream) {
        new Thread(() -> {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(inputStream, "GBK"))) { // 适配Windows下FFmpeg的中文输出
                String line;
                while ((line = reader.readLine()) != null) {
                    // 区分日志级别：包含error/warning的标红，方便排查
                    if (line.contains("error") || line.contains("Error") || line.contains("ERROR")) {
                        log.error("FFmpeg错误输出：{}", line);
                    } else if (line.contains("warning") || line.contains("Warning")) {
                        log.warn("FFmpeg警告输出：{}", line);
                    } else {
                        log.debug("FFmpeg正常输出：{}", line); // 调试级别，可根据需要改为info
                    }
                }
            } catch (IOException e) {
                log.warn("读取FFmpeg输出失败：{}", e.getMessage());
            }
        }, "FFmpeg-Output-Reader").start();
    }
}