package org.example.comsumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 路由Routing模式接受消息。队列与交换机的绑定需要指定一个 RoutingKey。
 * 消息的发送方在向 Exchange 发送消息时，也必须指定消息的 RoutingKey。
 * Exchange 不再把消息交给每一个绑定的队列，而是根据消息的 RoutingKey 进行判断，
 * 只有队列的 RoutingKey 与消息的 RoutingKey 完全一致，才会接收到消息。Routing 模式会将消息转发到routingKey匹配的队列。
 * 一个交换机"test_direct"，两个队列"test_direct_queue1"、"test_direct_queue2"
 * 前者接受error消息，后者接受info/error/warning消息
 */
public class ConsumerRouting1 {
    static final String QUEUE = "test_direct_queue1";
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
                System.out.println("保存到数据库，接收到的消息为：" + new String(body, "utf-8"));
            }
        };
        //监听消息
        channel.basicConsume(QUEUE, true, consumer);

        // 消费者不应该关闭资源，应该一直监听消息
    }
}
