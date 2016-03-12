import javax.jms.JMSException;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class Application {
    public static void main(String[] args) {
        try {
            new QueueUsageEmulator().start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
