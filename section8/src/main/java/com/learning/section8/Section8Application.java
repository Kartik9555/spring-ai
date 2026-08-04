package com.learning.section8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class Section8Application {
    static void main(String[] args) {
        SpringApplication.run(Section8Application.class, args);
    }
}
