package com.axibase.activemqusageemulator;

import com.axibase.activemqusageemulator.queuebalance.ProduceQueueController;
import com.axibase.activemqusageemulator.queuebalance.QueueContollerFactory;
import com.axibase.activemqusageemulator.queuebalance.SimpleQueueBalncerTask;
import com.axibase.activemqusageemulator.tools.BalanceFunctionGenrator;
import com.axibase.activemqusageemulator.tools.DateTimeHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.util.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class ActiveMQueueBalancer extends Thread {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ActiveMQueueBalancer.class);
    private final SimpleQueueBalncerTask queueBalancerTask;
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private String queueName;
    private WorkDay workDay;
    private int interval;


    public ActiveMQueueBalancer(Properties properties) throws Exception {
        String host = properties.getProperty("host");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        Integer port = Integer.parseInt(properties.getProperty("port"));
        String serverURI = new StringBuilder()
                .append("tcp://")
                .append(host)
                .append(':')
                .append(port)
                .toString();

        this.interval = Integer.parseInt(properties.getProperty("interval"));
        this.queueName = properties.getProperty("queue");
        this.workDay = new WorkDay();
        this.connectionFactory = new ActiveMQConnectionFactory(user, password, serverURI);
        establishConnection();
        LOG.info("Conncetion is established");
        this.queueBalancerTask =
                new SimpleQueueBalncerTask(new QueueContollerFactory(this.connection, this.queueName),
                        BalanceFunctionGenrator.generateInterpolator(workDay));
    }


    private class InterpolateTask extends TimerTask {
        @Override
        public void run() {
            queueBalancerTask.setFunction(BalanceFunctionGenrator.generateInterpolator(workDay));
        }
    }

    private void establishConnection() {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        new Timer().schedule(new InterpolateTask(), DateTimeHelper.getNextMidnight(), 24 * 60 * 60 * 100L);
        new Timer().schedule(queueBalancerTask, new Date(), interval);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (connection != null) {
            connection.close();
        }
    }
}
