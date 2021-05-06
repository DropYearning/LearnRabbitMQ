package org.example.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布/订阅PubSub模式发送消息。
 * 多了一个 Exchange 角色，生产者生产的消息不再发送到队列中，而是发给Exchange交换机，
 * 通过Exchange对消息进行分发（广播Fanout、定向Direct、通配符Topic）；
 * 每个消费者监听自己的队列。
 * 一个交换机"test_fanout"，两个队列"test_fanout_queue1"、"test_fanout_queue2"
 */
public class ProducerPubSub {
    static final String FANOUT_QUEUE_1 = "test_fanout_queue1";
    static final String FANOUT_QUEUE_2 = "test_fanout_queue2";
    static final String FANOUT_EXCHAGE = "test_fanout";

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

        /**
         * 声明交换机
         * DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type,
         *      boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments
         * 参数1：交换机名称
         * 参数2：交换机类型(枚举)，fanout广播、topic通配符、direct定向、headers参数匹配
         * 参数3 durable: 是否持久化
         * 参数4 autoDelete: 是否自动删除
         * 参数5 internal: 是否内部使用的
         * 参数6 arguments: 参数列表
         */
        channel.exchangeDeclare(FANOUT_EXCHAGE, BuiltinExchangeType.FANOUT, true, false, false, null);

        // 声明（创建）队列
        channel.queueDeclare(FANOUT_QUEUE_1, true, false, false, null);
        channel.queueDeclare(FANOUT_QUEUE_2, true, false, false, null);

        /**
         * 队列绑定交换机
         * 参数1 queue: 队列名称
         * 参数2 exchange: 交换机名称
         * 参数3 routingKey: 路由键
         */
        channel.queueBind(FANOUT_QUEUE_1, FANOUT_EXCHAGE, ""); // 当交换机为fanout时，路由键为""空字符串
        channel.queueBind(FANOUT_QUEUE_2, FANOUT_EXCHAGE, "");

        // 发送信息
        String message = "你好；小兔子！发布订阅模式" ;
        /**
         * 参数1：交换机名称，如果没有指定则使用默认Default Exchage
         * 参数2：路由key,简单模式可以传递队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
        channel.basicPublish(FANOUT_EXCHAGE, "", null, message.getBytes());
        System.out.println("已发送消息：" + message);

        // 关闭资源
        channel.close();
        connection.close();
    }
}
