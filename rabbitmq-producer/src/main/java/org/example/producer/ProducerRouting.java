package org.example.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 路由Routing模式发送消息。队列与交换机的绑定需要指定一个 RoutingKey。
 * 消息的发送方在向 Exchange 发送消息时，也必须指定消息的 RoutingKey。
 * Exchange 不再把消息交给每一个绑定的队列，而是根据消息的 RoutingKey 进行判断，
 * 只有队列的 RoutingKey 与消息的 RoutingKey 完全一致，才会接收到消息。Routing 模式会将消息转发到routingKey匹配的队列。
 * 一个交换机"test_direct"，两个队列"test_direct_queue1"、"test_direct_queue2"
 * 前者接受error消息，后者接受info/error/warning消息
 */
public class ProducerRouting {
    static final String DIRECT_QUEUE_1 = "test_direct_queue1";
    static final String DIRECT_QUEUE_2 = "test_direct_queue2";
    static final String DIRECT_EXCHAGE = "test_direct";

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

         // 声明交换机
        channel.exchangeDeclare(DIRECT_EXCHAGE, BuiltinExchangeType.DIRECT, true, false, false, null);

        // 声明（创建）队列
        channel.queueDeclare(DIRECT_QUEUE_1, true, false, false, null);
        channel.queueDeclare(DIRECT_QUEUE_2, true, false, false, null);

        // 队列绑定交换机
        // 队列1绑定
        channel.queueBind(DIRECT_QUEUE_1, DIRECT_EXCHAGE, "error"); // 当交换机为direct时，需要指定具体的路由键
        // 队列2绑定
        channel.queueBind(DIRECT_QUEUE_2, DIRECT_EXCHAGE, "info");
        channel.queueBind(DIRECT_QUEUE_2, DIRECT_EXCHAGE, "error");
        channel.queueBind(DIRECT_QUEUE_2, DIRECT_EXCHAGE, "warning");

        // 发送信息
        String message1 = "[info]你好；小兔子！Routing模式" ;
        String message2 = "[error]你好；小兔子！Routing模式" ;
        String message3 = "[warning]你好；小兔子！Routing模式" ;

        /**
         * 参数1：交换机名称，如果没有指定则使用默认 Default Exchange
         * 参数2：路由key,简单模式可以传递队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
        channel.basicPublish(DIRECT_EXCHAGE, "error", null, message2.getBytes());
        System.out.println("已发送消息：" + message2);

        // 关闭资源
        channel.close();
        connection.close();
    }
}
