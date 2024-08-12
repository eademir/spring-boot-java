package com.example.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Recieve {
    private final static String QUEUE_NAME = "hello";

    public static List<Message> run() throws Exception {
        List<Message> messages = new ArrayList<>();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageContent = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Message message = new Message();
            message.setMessage(messageContent);
            messages.add(message);
            System.out.println(" [x] Received '" + messageContent + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });

        // Wait for a short period to ensure messages are consumed
        Thread.sleep(1000);

        return messages;
    }
}
