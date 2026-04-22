package com.starter.lovable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LovableApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(LovableApplication.class, args);
    }

}
