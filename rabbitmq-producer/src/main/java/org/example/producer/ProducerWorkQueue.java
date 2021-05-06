package org.example.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * WorkQueue模式发送消息：单个生产者，多个消费者竞争消费消息。
 */
public class ProducerWorkQueue {
    static final String QUEUE_NAME = "work_queue";

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
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for (int i = 0; i < 10; i++) {
            String message = i + ": 你好，work_queue！";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("已发送消息：" + message);
        }

        // 关闭资源。若不关闭channel和connection则可以在RabbitMQ的管理后台看到channel和connection信息
        channel.close();
        connection.close();
    }
}
