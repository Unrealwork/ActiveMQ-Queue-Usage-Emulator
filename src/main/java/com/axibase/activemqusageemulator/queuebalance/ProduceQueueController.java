package com.axibase.activemqusageemulator.queuebalance;

import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class ProduceQueueController extends QueueController {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ProduceQueueController.class);
    private MessageProducer producer;

    public Session getSession(){
        return session;
    }

    public ProduceQueueController(Connection connection, String queueName) throws JMSException {
        super(connection, queueName);
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void balanceAction() {
        try {
            sendMessage();
        } catch (JMSException e) {
            LOG.error("ProduceQueueController couldn't send message cause: \n", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        producer.close();
    }

    public void sendMessage() throws JMSException {
        String message = "message";
        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }


}
