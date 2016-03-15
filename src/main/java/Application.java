import javax.jms.JMSException;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class Application {
    public static void main(String[] args) {
        try {
            new QueueUsageEmulator(
                    new WorkDay(),
                    "queue-2", "tcp://hbs.axibase.com:5022",
                    "admin",
                    null)
                    .start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
