package com.winkel.qualityevaluation.config.mq;
/*
  @ClassName Producer
  @Description
  @Author winkel
  @Date 2022-04-05 14:03
  */

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchangeName, String routingKey, Object obj) {
        String msgId = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(obj.toString().getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setCorrelationId(msgId)
                .setHeader("x-delay",1000*3600*24*15)  // ms 延迟15天发送
                .build();
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, new CorrelationData(msgId));
    }

}
