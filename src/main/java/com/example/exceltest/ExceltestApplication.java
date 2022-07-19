package com.example.exceltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.exceltest.mapper"})
public class ExceltestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExceltestApplication.class, args);
    }

}
