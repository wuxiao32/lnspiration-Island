package com.inspiration.island;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.inspiration.island.mapper")
public class InspirationIslandApplication {
    public static void main(String[] args) {
        SpringApplication.run(InspirationIslandApplication.class, args);
    }
}