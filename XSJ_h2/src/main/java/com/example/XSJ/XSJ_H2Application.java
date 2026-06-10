package com.example.XSJ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XSJ_H2Application {

    public static void main(String[] args) {
        SpringApplication.run(XSJ_H2Application.class, args);
    }

}
