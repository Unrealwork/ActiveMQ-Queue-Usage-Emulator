
import javax.jms.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class Producer{

    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public Session getSession(){
        return session;
    }

    public Producer(Connection connection,String queueName) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void sendMessage(String message) throws JMSException {
        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }
}
