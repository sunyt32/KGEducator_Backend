package com.tsinghua.kgeducator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication()
@MapperScan("com.tsinghua.kgeducator.mapper")
public class KgEducatorApplication
{
    public static void main(String[] args) {
        SpringApplication.run(KgEducatorApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
