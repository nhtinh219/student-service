package com.kienlong.api.studentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudentManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManageApplication.class, args);
    }

}
