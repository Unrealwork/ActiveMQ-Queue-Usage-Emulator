import org.apache.log4j.Logger;

import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class Consumer {
    private Session session;
    private Queue destination;
    private MessageConsumer consumer;

    public Long getQueueSize() throws Exception{
        QueueBrowser browser = session.createBrowser(destination);
        ArrayList list = new ArrayList();
        Long l = 0L;
        for(Enumeration e = browser.getEnumeration(); e.hasMoreElements(); )
        {
            e.nextElement();
            l++;
        }
        return l;
    }


    public Consumer(Connection connection) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue("queue-1");
        consumer = session.createConsumer(destination);
    }




    public void receiveMessage() throws JMSException {
        Message message = consumer.receive(100);
    }
}
