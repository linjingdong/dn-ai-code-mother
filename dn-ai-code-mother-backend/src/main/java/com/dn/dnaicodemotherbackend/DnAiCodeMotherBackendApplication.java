package com.dn.dnaicodemotherbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class DnAiCodeMotherBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DnAiCodeMotherBackendApplication.class, args);
    }

}
