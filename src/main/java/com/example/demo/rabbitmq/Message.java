package com.example.demo.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class Message {
    private String message;
}
