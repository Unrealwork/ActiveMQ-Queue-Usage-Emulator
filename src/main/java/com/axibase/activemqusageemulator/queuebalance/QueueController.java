package com.axibase.activemqusageemulator.queuebalance;

import javax.jms.*;
import java.util.Enumeration;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public abstract class QueueController {
    Session session;
    Queue destination;
    Connection JMSConnection;

    QueueController(Connection connection, String queueName) throws JMSException {
        setJMSConnection(connection);
        session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
    }

    void setJMSConnection(Connection JMSConnection) {
        this.JMSConnection = JMSConnection;
    }

    public abstract void balanceAction();

    public Long getQueueSize() throws JMSException {
        QueueBrowser browser = session.createBrowser(destination);
        Long l = 0L;
        for (Enumeration e = browser.getEnumeration(); e.hasMoreElements(); ) {
            e.nextElement();
            l++;
        }
        return l;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        session.close();
    }
}
