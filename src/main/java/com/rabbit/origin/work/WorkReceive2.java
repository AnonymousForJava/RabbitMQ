package com.rabbit.origin.work;

import com.rabbit.origin.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @ClassName WorkReceive2
 * @Author ZhangY
 * @Date 2020/02/21 13:08
 * @Version 1.0.0
 * @Description
 */
public class WorkReceive2 {

    public static void main(String[] argv) throws Exception {

        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare(WorkReceive1.QUEUE_NAME, false, false, false, null);

        // 同一时刻服务器只会发一条消息给消费者
        channel.basicQos(1);  //TODO Work模式的“能者多劳”打开此代码 ①

        // 定义队列的消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 监听队列，false表示手动返回完成状态，true表示自动
        //参数说明
        // * @param queue the name of the queue
        // * @param autoAck true if the server should consider messages  true为自动确认  TODO Work模式的“能者多劳”改为false(手动确认)
        // * acknowledged once delivered; false if the server should expect
        // * explicit acknowledgements
        // * @param callback an interface to the consumer object
        // * @return the consumerTag generated by the server
        channel.basicConsume(WorkReceive1.QUEUE_NAME, false, consumer);

        // 获取消息
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            // 休眠1秒
            Thread.sleep(1000);
            //下面这行注释掉表示使用自动确认模式
            //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

}
