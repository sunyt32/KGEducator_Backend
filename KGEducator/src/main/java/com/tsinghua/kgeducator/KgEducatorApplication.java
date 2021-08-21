package com.tsinghua.kgeducator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication()
@MapperScan("com.tsinghua.kgeducator.mapper")
public class KgEducatorApplication
{
    public static void main(String[] args) {
        SpringApplication.run(KgEducatorApplication.class, args);
    }
}
