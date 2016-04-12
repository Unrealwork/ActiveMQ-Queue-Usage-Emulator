package com.axibase.activemqusageemulator.queuebalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.TimerTask;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public abstract class QueueBalancerTask extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(QueueBalancerTask.class);
    protected QueueController randomContoller;

    public QueueBalancerTask(QueueContollerFactory queueContollerFactory) {
        QueueContollerFactory queueContollerFactory1 = queueContollerFactory;
        try {
            this.randomContoller = queueContollerFactory.getQueueController(QueueContollerType.PRODUCER);
        } catch (JMSException e) {
            LOG.error("Failed to get QueueController");
        }
    }

    protected abstract void makeBalanced () throws JMSException;

    protected abstract boolean isBalanced() throws JMSException;

    @Override
    public void run() {
        try {
            if (!isBalanced()) {
                LOG.info("Queue size is not balnced");
                makeBalanced();
            } else {
                LOG.info("Queue size is balanced");
            }
        } catch (JMSException e) {
            LOG.error("Failed to make queue size balanced!");
            e.printStackTrace();
        }
    }

    protected Long getCurrentQueueSize() throws JMSException {
        return randomContoller.getQueueSize();
    }

    protected abstract Long getPreferredQueueSize();


}
