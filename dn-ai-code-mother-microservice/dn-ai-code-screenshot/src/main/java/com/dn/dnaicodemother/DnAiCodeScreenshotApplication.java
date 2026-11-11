package com.dn.dnaicodemother;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DnAiCodeScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DnAiCodeScreenshotApplication.class, args);
    }
}
