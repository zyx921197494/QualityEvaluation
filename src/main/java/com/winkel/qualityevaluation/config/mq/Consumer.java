package com.winkel.qualityevaluation.config.mq;
/*
  @ClassName Consumer
  @Description
  @Author winkel
  @Date 2022-04-05 14:21
  */

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rabbitmq.client.Channel;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.exception.ConsumerException;
import com.winkel.qualityevaluation.service.api.TaskService;
import com.winkel.qualityevaluation.service.api.UserService;
import com.winkel.qualityevaluation.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class Consumer {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delayqueue", durable = "true"),
            exchange = @Exchange(value = "delayexchange")))
    public void receive(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        String str = new String(message.getBody(), StandardCharsets.UTF_8);

        Pattern idPattern = Pattern.compile("id=(\\w+),");
        Matcher idMatcher = idPattern.matcher(str);
        while (idMatcher.find()) {
            userService.update(new UpdateWrapper<User>().eq("id", idMatcher.group(1)).set("is_locked", Const.LOCKED));
            log.info("锁定用户：id={}", idMatcher.group(1));
        }

        Pattern taskIdPattern = Pattern.compile("taskId=(\\w+)\\)");
        Matcher taskIdMatcher = taskIdPattern.matcher(str);
        while (taskIdMatcher.find()) {
            taskService.update(new UpdateWrapper<EvaluateTask>().eq("evaluate_task_id", taskIdMatcher.group(1)).set("task_is_locked", Const.LOCKED));
            log.info("任务到期，taskId={}", taskIdMatcher.group(1));
        }

        try {
            channel.basicAck(deliveryTag, false);
            log.info("确认消息：deliveryTag={}", deliveryTag);
        } catch (IOException e) {
            throw new ConsumerException("确认消息时异常：deliveryTag = " + deliveryTag);
        }
    }

}
