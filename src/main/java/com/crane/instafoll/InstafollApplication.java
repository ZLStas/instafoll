package com.crane.instafoll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class InstafollApplication {


    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(InstafollApplication.class, args);
    }

}
