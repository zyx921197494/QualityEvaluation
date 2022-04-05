package com.winkel.qualityevaluation.config.mq;
/*
  @ClassName MQConfig
  @Description
  @Author winkel
  @Date 2022-04-05 13:34
  */

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


@Slf4j
@Configuration
public class MQConfig {

    @Value("${spring.rabbitmq.host}")
    String address;

    @Value("${spring.rabbitmq.port}")
    int port;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    String password;

    @Value("${spring.rabbitmq.virtual-host}")
    String virtualHost;

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        // 消息到达Exchange后回调
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            if (ack) {
                log.info(data + " 消息发送成功");
            } else {
                log.error("消息发送失败：" + cause);
            }
        });
        rabbitTemplate.setMandatory(true);
        // 消息从Exchange发送到Queue失败时回调
        rabbitTemplate.setReturnCallback((msg, replyCode, reply, exchange, toutingKey) -> {
            log.error("消费者接收消息失败：" + reply);
        });
        return rabbitTemplate;
    }

    @Bean
    public CustomExchange exchange() {
        HashMap<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delayexchange", "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue queue() {
        return new Queue("delayqueue");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("delaykey").noargs();
    }


}
