package com.example.project01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.example.project01")
public class Project01Application {
    public static void main(String[] args) {
        SpringApplication.run(Project01Application.class, args);
    }
}
