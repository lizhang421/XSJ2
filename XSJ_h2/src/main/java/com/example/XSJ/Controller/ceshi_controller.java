package com.example.XSJ.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/ceshi")
@RestController

public class ceshi_controller {

    @GetMapping("/ceshi1")
    public String ceshi1(){
        System.out.println("这是测试的内容测试内容");
        return "这是测试的内容测试内容";
    }



}
