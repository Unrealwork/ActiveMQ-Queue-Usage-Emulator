package com.axibase.activemqusageemulator.queuebalance;

import com.axibase.activemqusageemulator.tools.DateTimeHelper;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public class SimpleQueueBalncerTask extends QueueBalancerTask {
    private static Logger LOG = LoggerFactory.getLogger(SimpleQueueBalncerTask.class);

    public void setFunction(PolynomialSplineFunction function) {
        this.function = function;
    }

    private PolynomialSplineFunction function;
    private ConsumeQueueController consumeQueueContoller;
    private ProduceQueueController produceQueueContoller;

    public SimpleQueueBalncerTask(QueueContollerFactory queueContollerFactory, PolynomialSplineFunction function) {
        super(queueContollerFactory);
        this.function = function;
        try {
            this.produceQueueContoller = (ProduceQueueController) queueContollerFactory.getQueueController
                    (QueueContollerType.PRODUCER);
            LOG.info("ProduceQueueController successfully created!");
        } catch (JMSException e) {
            LOG.error("Failed to get ProduceQueueController");
            e.printStackTrace();

        }
        try {
            this.consumeQueueContoller = (ConsumeQueueController) queueContollerFactory.getQueueController
                    (QueueContollerType.CONSUMER);
            LOG.info("Consume QueueController successfully created!");
        } catch (JMSException e) {
            LOG.error("Failed to get ConsumeQueueController");
            e.printStackTrace();
        }
    }


    @Override
    public void makeBalanced() throws JMSException {
        Long currentQueueSize = getCurrentQueueSize();
        Long preferredQueueSize = getPreferredQueueSize();
        LOG.info("\tCurrent: {}; Prefferd {}", currentQueueSize, preferredQueueSize);
        if (currentQueueSize < preferredQueueSize) {
            LOG.info("Producer balance queue size.");
            while (getCurrentQueueSize() < preferredQueueSize)
                produceQueueContoller.balanceAction();
            LOG.info("Producer balanced queue size.");
        }
        if (currentQueueSize > preferredQueueSize) {
            LOG.info("Consume balance queue size.");
            while (getCurrentQueueSize() > preferredQueueSize)
                consumeQueueContoller.balanceAction();
            LOG.info("Consumer balanced queue size.");
        }
    }

    public boolean isBalanced() throws JMSException {
        return getPreferredQueueSize().equals(getCurrentQueueSize());
    }

    @Override
    protected Long getPreferredQueueSize() {
        Integer currentTimeInSeconds = DateTimeHelper.getSecondsOfToday();
        return Math.round(function.value(currentTimeInSeconds));
    }
}
