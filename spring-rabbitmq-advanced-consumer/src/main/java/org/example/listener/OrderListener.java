package org.example.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 测试延迟队列用的监听器
 */
@Component
public class OrderListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1.接收转换消息
            System.out.println(new String(message.getBody()));
            //2. 处理业务逻辑
            System.out.println("处理业务逻辑...");
            System.out.println("根据订单id查询状态...");
            System.out.println("判断订单是否支付成功...");
            System.out.println("取消订单回滚库存...");
            //int i = 3/0;// 模拟接受消息进行处理时出现错误
            //3. 手动签收
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            //e.printStackTrace();
            //4.拒绝签收
            /*
            第三个参数：requeue：重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            System.out.println("出现异常，拒绝签收");
            //channel.basicNack(deliveryTag,true,true);
            channel.basicNack(deliveryTag,true,false); // 不重回队列，而是将其加入到死信队列
            //channel.basicReject(deliveryTag,true); // basicReject只能处理单条消息
        }
    }
}
