package com.example.XSJ.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        try {

            Map<String, Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);


            String uri = request.getRequestURI();


            if (uri.startsWith("/admin/")) {

                Integer roleBase = (Integer) claims.get("RoleBase");


                if (roleBase == null || (roleBase != 1 && roleBase != 2)) {
                    response.setStatus(403);
                    response.getWriter().write("无管理员权限");
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}