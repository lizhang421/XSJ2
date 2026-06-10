package com.example.XSJ.Config;

import com.example.XSJ.utils.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    //登录和注册接口不拦截
    //把拦截器注册进去，登录接口和注册接口不拦截
    public  void addInterceptors(InterceptorRegistry  registry){
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/user/login","/user/register","/ceshi/ceshi1","/user/captcha","/email/send",
                "/email/register", "/email/login","/user/home","/admin/login","/email/forget","/email/updatePasswordbyem"
        );

    }


}

