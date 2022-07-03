package com.eterlar.seckillsystem.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息发送者
 * @author eterlar
 */
@Service
@Slf4j
public class MQSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param msg
     */
    public void sendSeckillMessage(String msg) {
        log.info("发送消息: " + msg);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", msg);
    }
}
