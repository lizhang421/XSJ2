package com.example.XSJ.utils;

import org.springframework.stereotype.Component;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

@Component
public class AliVideoUtil {
    public File downloadVideo(String videoUrl) throws Exception {
        URL url = new URL(videoUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        InputStream in = conn.getInputStream();

        File tempFile = File.createTempFile("ali_video_", ".mp4");
        FileOutputStream out = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        out.close();
        in.close();
        conn.disconnect();
        return tempFile;
    }


}