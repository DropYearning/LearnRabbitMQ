package org.example.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 通配符Topic模式发送消息。
 * Topic 类型的Exchange 可以让队列在绑定 RoutingKey 的时候使用通配符。
 * 通配符规则：
 *     #：匹配一个或多个词
 *     *：匹配一个词
 */
public class ProducerTopic {
    static final String QUEUE_1 = "test_topic_queue1";
    static final String QUEUE_2 = "test_topic_queue2";
    static final String EXCHANGE = "test_topic";

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
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true, false, false, null);

        // 声明（创建）队列
        channel.queueDeclare(QUEUE_1, true, false, false, null);
        channel.queueDeclare(QUEUE_2, true, false, false, null);

        // 队列绑定交换机：所有error级别的日志存数据库，所有order系统的日志存数据库, 其他所有信息都打印到控制台
        channel.queueBind(QUEUE_1, EXCHANGE, "#.error");
        channel.queueBind(QUEUE_1, EXCHANGE, "order.*");
        channel.queueBind(QUEUE_2, EXCHANGE, "*.*");

        //// 发送信息
        //String message = "新增了商品。Topic模式；routing key 为 order.info " ;
        //channel.basicPublish(EXCHANGE, "order.info", null, message.getBytes());
        //System.out.println("已发送消息：" + message);

        // 发送信息
        String message = "新增了商品。Topic模式；routing key 为 goods.info " ;
        channel.basicPublish(EXCHANGE, "goods.info", null, message.getBytes());
        System.out.println("已发送消息：" + message);


        // 关闭资源
        channel.close();
        connection.close();
    }
}
