package org.example.comsumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * HelloWorld入门模式 ——  单个生产者，单个消费者。
 */
public class ConsumerHelloWorld {
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

        // 创建连接（Consumer创建的连接和channel与Producer中的连接和channel不是同一个）
        Connection connection = connectionFactory.newConnection();
        // 创建频道
        Channel channel = connection.createChannel();

        // 声明（创建）队列（Producer中已经创建过）

        // 创建消费者；并设置消息处理
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            /**
             * 回调方法，在收到消息时和会自动执行该方法
             * consumerTag:  消息者标签，在channel.basicConsume时候可以指定
             * envelope: 消息包的内容，可从中获取消息id，消息routingKey，交换机，消息和重传标志(收到消息失败后是否需要重新发送)
             * properties: 属性信息
             * body: 消息
             */
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //路由key
                System.out.println("路由key为：" + envelope.getRoutingKey());
                //交换机
                System.out.println("交换机为：" + envelope.getExchange());
                //消息id
                System.out.println("消息id为：" + envelope.getDeliveryTag());
                //收到的消息
                System.out.println("接收到的消息为：" + new String(body, "utf-8"));
            }
        };
        //监听消息
        /**
         * 参数1 queue：队列名称
         * 参数2 autoAck：是否自动确认，设置为true为表示消息接收到自动向mq回复接收到了，MQ 接收到回复会删除消息，设置为false则需要手动确认
         * 参数3 callback：消息接收到后回调
         */
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // 消费者不应该关闭资源，应该一直监听消息
    }
}
