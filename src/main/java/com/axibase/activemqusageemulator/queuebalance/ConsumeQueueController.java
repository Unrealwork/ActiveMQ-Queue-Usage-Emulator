package com.axibase.activemqusageemulator.queuebalance;

import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class ConsumeQueueController extends QueueController {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConsumeQueueController.class);
    private MessageConsumer consumer;


    public ConsumeQueueController(Connection connection, String queueName) throws JMSException {
        super(connection, queueName);
        LOG.info("Trying to create consumer", queueName);
        consumer = session.createConsumer(destination);
    }

    @Override
    public void balanceAction() {
        try {
            receiveMessage();
        } catch (JMSException e) {
            LOG.error("ConsumeQueueController can't recive a message with error: \n {}", e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        consumer.close();
    }


    private void receiveMessage() throws JMSException {
        Message message = consumer.receive(100);
    }
}
