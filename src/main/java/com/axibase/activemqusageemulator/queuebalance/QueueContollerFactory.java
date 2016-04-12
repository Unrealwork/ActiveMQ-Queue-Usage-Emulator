package com.axibase.activemqusageemulator.queuebalance;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public class QueueContollerFactory {
    private Connection JMSConnection;
    private String queueName;

    public QueueContollerFactory(Connection connection, String queueName) {
        this.JMSConnection = connection;
        this.queueName = queueName;
    }

    public QueueController getQueueController(QueueContollerType type) throws JMSException {
        switch (type) {
            case CONSUMER:
                return new ConsumeQueueController(this.JMSConnection, this.queueName);
            case PRODUCER:
                return new ProduceQueueController(this.JMSConnection, this.queueName);
            default:
                return null;
        }
    }
}
