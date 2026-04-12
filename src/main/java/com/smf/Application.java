package com.smf;

import jakarta.annotation.PostConstruct; 
import java.util.TimeZone;         
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {


    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}