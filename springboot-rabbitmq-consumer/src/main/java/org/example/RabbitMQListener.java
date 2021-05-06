package org.example;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 创建消息监听器类，使用@RabbitListener 注解完成队列监听。
 */
@Component
public class RabbitMQListener {

    @RabbitListener(queues = "item_queue")
    public void ListenerQueue(Message message){
        System.out.println(message);
        System.out.println(new String(message.getBody()));
    }
}
