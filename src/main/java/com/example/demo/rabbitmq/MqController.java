package com.example.demo.rabbitmq;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/mq")
@RestController
public class MqController {
    private final MqService mqService;

    public MqController(MqService mqService) {
        this.mqService = mqService;
    }

    @PostMapping
    public void send(@RequestBody Message message) throws Exception {
        mqService.send(message);
    }
}
