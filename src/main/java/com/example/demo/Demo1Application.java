package com.example.demo;

import com.example.demo.rabbitmq.Recieve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo1Application {
    private static final Logger logger = LoggerFactory.getLogger(Demo1Application.class);


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Demo1Application.class, args);
        Recieve.run();
        logger.info("Application started successfully!");
    }
}
