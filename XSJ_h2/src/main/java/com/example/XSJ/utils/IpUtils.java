package com.example.XSJ.utils;

/**
 * IP解析工具类
 */
public class IpUtils {

    private static final String[] TRUSTED_PROXY_PREFIXES = {"192.168.", "172.16.", "10.", "127.", "0:0:0:0:0:0:0:1"};

    // 方法参数类型同步改为jakarta.servlet.http.HttpServletRequest
    public static String getRealIp(jakarta.servlet.http.HttpServletRequest request) {
        String ip = null;

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            String[] ipSegments = xForwardedFor.split(",");
            for (String segment : ipSegments) {
                String trimIp = segment.trim();
                if (isValidIp(trimIp) && !isTrustedProxyIp(trimIp)) {
                    ip = trimIp;
                    break;
                }
            }
        }

        // 2. 读取X-Real-IP
        if (!isValidIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        // 3. 读取Proxy-Client-IP
        if (!isValidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        // 4. 读取RemoteAddr
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 5. 处理IPv6
        ip = convertIpv6ToIpv4(ip);

        // 6. 兜底
        if (!isValidIp(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    // 其他辅助方法（isValidIp/isTrustedProxyIp/convertIpv6ToIpv4）保持不变
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip) && !ip.trim().isEmpty();
    }

    private static boolean isTrustedProxyIp(String ip) {
        for (String prefix : TRUSTED_PROXY_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static String convertIpv6ToIpv4(String ip) {
        if (ip == null) return null;
        if ("::1".equals(ip)) {
            return "127.0.0.1";
        }
        if (ip.startsWith("::ffff:")) {
            return ip.substring("::ffff:".length());
        }
        try {
            java.net.InetAddress.getByName(ip);
        } catch (java.net.UnknownHostException e) {
            return "127.0.0.1";
        }
        return ip;
    }
}