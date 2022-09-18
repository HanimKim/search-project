package com.example.frontoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}, scanBasePackages = { "com.example" })
public class ApplicationFrontOffice {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationFrontOffice.class, args);
    }

}
