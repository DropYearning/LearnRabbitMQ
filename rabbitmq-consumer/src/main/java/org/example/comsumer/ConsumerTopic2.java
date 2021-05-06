package org.example.comsumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 通配符Topic模式接受消息。
 * Topic 类型的Exchange 可以让队列在绑定 RoutingKey 的时候使用通配符。
 * 通配符规则：
 *     #：匹配一个或多个词
 *     *：匹配一个词
 */
public class ConsumerTopic2 {
    static final String QUEUE = "test_topic_queue2";
    static final String EXCHANGE = "test_direct";

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
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 创建消费者；并设置消息处理
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            /**
             * 回调方法，在收到消息时和会自动执行该方法
             */
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //收到的消息
                System.out.println("打印到控制台：" + new String(body, "utf-8"));
            }
        };
        //监听消息
        channel.basicConsume(QUEUE, true, consumer);

        // 消费者不应该关闭资源，应该一直监听消息
    }
}
