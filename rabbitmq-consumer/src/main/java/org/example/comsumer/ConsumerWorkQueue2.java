package org.example.comsumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * WorkQueue模式接受消息：单个生产者，多个消费者竞争消费消息。
 */
public class ConsumerWorkQueue2 {
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

        // 创建连接（Consumer创建的连接和channel与Producer中的连接和channel不是同一个）
        Connection connection = connectionFactory.newConnection();
        // 创建频道
        Channel channel = connection.createChannel();

        // 声明（创建）队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 创建消费者；并设置消息处理
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            /**
             * 回调方法，在收到消息时和会自动执行该方法
             */
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //收到的消息
                System.out.println("接收到的消息为：" + new String(body, "utf-8"));
            }
        };
        //监听消息
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // 消费者不应该关闭资源，应该一直监听消息
    }
}
