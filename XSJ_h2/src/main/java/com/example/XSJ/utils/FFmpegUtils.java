package com.example.XSJ.utils;

import lombok.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import static com.example.XSJ.utils.RegexPatterns.FFMPEG_ADDRESS;

public class FFmpegUtils {

    @Data
    public static class VideoInfo {
        private long duration;  // 时长(秒)
        private long bitrate;   // 码率
        private int width;      // 宽
        private int height;     // 高
    }


    private static final String FFMPEG = FFMPEG_ADDRESS;

    public static VideoInfo getFullVideoInfo(String videoPath) {
        VideoInfo info = new VideoInfo();
        try {
            String command = FFMPEG + " -i " + videoPath;
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), "GBK")
            );

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.contains("Duration:")) {
                    String durationStr = line.split("Duration: ")[1].split(",")[0].trim();
                    String[] time = durationStr.split(":");
                    long seconds = (long) (
                            Double.parseDouble(time[0]) * 3600
                                    + Double.parseDouble(time[1]) * 60
                                    + Double.parseDouble(time[2])
                    );
                    info.setDuration(seconds);
                }


                if (line.contains("bitrate:")) {
                    String[] arr = line.split("bitrate: ")[1].split(" ");
                    if (arr[0].matches("\\d+")) {
                        info.setBitrate(Long.parseLong(arr[0]) * 1000);
                    }
                }

                // 解析分辨率
                if (line.startsWith("Stream #0:0") && line.contains("Video:")) {
                    int start = line.indexOf(", ") + 2;
                    int end = line.indexOf(" fps", start);
                    String wh = line.substring(start, end).trim();
                    String[] whArr = wh.split("x");
                    info.setWidth(Integer.parseInt(whArr[0]));
                    info.setHeight(Integer.parseInt(whArr[1]));
                    break;
                }
            }
            br.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    // 截取封面（使用你自己的FFmpeg路径）
    public static void extractFirstFrame(String videoPath, String outputImagePath) {
        try {
            String command = FFMPEG + " -i " + videoPath + " -vframes 1 -y " + outputImagePath;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}