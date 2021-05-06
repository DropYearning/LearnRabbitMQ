package org.example.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * HelloWorld模式发送消息:  单个生产者，单个消费者。
 */
public class ProducerHelloWorld {
    static final String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 主机地址; 默认为 localhost
        connectionFactory.setHost("localhost");
        // 连接端口; 默认为 5672
        connectionFactory.setPort(5672);
        // 虚拟主机名称; 默认为 /
        connectionFactory.setVirtualHost("/");
        // 连接用户名；默认为guest
        connectionFactory.setUsername("guest");
        // 连接密码；默认为guest
        connectionFactory.setPassword("guest");

        // 创建连接
        Connection connection = connectionFactory.newConnection();
        // 创建频道
        Channel channel = connection.createChannel();
        // 声明（创建）队列
        /**
         * 参数1 queue: 队列名称。如果不存在叫该名字的队列，则会创建队列；若有则不会再创建
         * 参数2 durable: 是否定义持久化队列，当MQ重启之后还存在
         * 参数3 exclusive: 是否独占本次连接，只能有一个消费者来监听这个队列。当connection关闭时是否删除队列
         * 参数4 autoDelete: 是否在不使用的时候自动删除队列
         * 参数5 map: 队列其它参数
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 要发送的信息
        String message = "你好，小兔子！";
        /**
         * 参数1 exchange：交换机名称，如果没有指定则使用默认 Default Exchange ("")
         * 参数2 routingKey：路由key,简单模式可以传递队列名称
         * 参数3 props: 消息其它属性
         * 参数4 byte[] body : 消息内容
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("已发送消息：" + message);

        // 关闭资源。若不关闭channel和connection则可以在RabbitMQ的管理后台看到channel和connection信息
        channel.close();
        connection.close();
    }
}
