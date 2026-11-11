package com.dn.dnaicodeuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Lin_jd
 * @version V1.0.0
 * @since 2026/11/11 18:17
 **/
@SpringBootApplication
@MapperScan("com.dn.dnaicodeuser.mapper")
@ComponentScan("com.dn")
public class DnAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(DnAiCodeUserApplication.class, args);
    }
}
