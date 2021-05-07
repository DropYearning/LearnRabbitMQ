package org.example.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * Consumer ACK机制：
 *  1. 设置手动签收。在XML的listener-container中设置acknowledge="manual"
 *  2. 让监听器类实现ChannelAwareMessageListener接口，实现onMessage(Message message, Channel channel)方法
 *  3. 如果消息成功处理，则调用channel的 basicAck() 手动签收
 *  4. 如果消息处理失败，则调用channel的basicNack() 拒绝签收，broker重新发送给consumer
 */
@Component
public class AckListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1.接收转换消息
            System.out.println(new String(message.getBody()));
            //2. 处理业务逻辑
            System.out.println("处理业务逻辑...");
            //int i = 3/0;// 模拟接受消息进行处理时出现错误
            //3. 手动签收
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            //e.printStackTrace();

            //4.拒绝签收
            /*
            第三个参数：requeue：重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            channel.basicNack(deliveryTag,true,true);
            //channel.basicReject(deliveryTag,true); // basicReject只能处理单条消息
        }
    }
}