package com.example.demo.rabbitmq;

import org.springframework.stereotype.Service;

@Service
public class MqService {

    public void send(Message message) throws Exception {
        Send send = new Send();
        send.setMessage(message.getMessage());
        send.run();
        System.out.println("Sending message...");
    }
}
