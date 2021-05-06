package org.example.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ的配置类
 */
@Configuration
public class RabbitMQConfig {
    // 交换机名称
    public static final String EXCHANGE_NAME = "item_topic_exchange";
    // 队列名称
    public static final String QUEUE_NAME = "item_queue";

    // 声明交换机，@Bean交给Spring管理, 需要被用来注入，因此在括号中声明名字
    @Bean("itemTopicExchange")
    public Exchange topicExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    // 声明队列，交给Spring管理, 需要被用来注入，因此在括号中声明名字
    @Bean("itemQueue")
    public Queue itemQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    // 绑定队列和交换机，交给Spring管理
    @Bean
    public Binding itemQueueExchange(@Qualifier("itemQueue") Queue queue,
                                     @Qualifier("itemTopicExchange") Exchange exchange){
        // with中声明routingKey
        return BindingBuilder.bind(queue).to(exchange).with("item.#").noargs();
    }
}
