import javax.jms.JMSException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class Application {
    public static void main(String[] args) {
        try {
            /*
            Get data from properties file
             */

            String connectionurl = System.getProperty("connectionurl");
            String username = System.getProperty("user");
            String password = System.getProperty("password");
            String queueName = System.getProperty("queue");
            System.out.println(connectionurl);
            Long interval = Long.parseLong(System.getProperty("interval"));
            new QueueUsageEmulator(
                    new WorkDay(),
                    queueName,
                    connectionurl,
                    username,
                    password,
                    interval)
                    .start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties(String fileName) {
        Properties props = new Properties();
        InputStream is = null;
        try{
            is = getClass().getResourceAsStream(fileName);
            props.load(is);
        } catch (IOException e) {
            s
        }

    }
}
