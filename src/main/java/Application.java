import javax.jms.JMSException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class Application {
    public static void main(String[] args) {
        Application app = new Application();
        try {
            /*
            Get data from properties file
             */
            Properties props = app.getProperties("app.properties");
            String connectionurl = props.getProperty("connectionurl");
            String username = props.getProperty("user");
            String password = props.getProperty("password");
            String queueName = props.getProperty("queue");
            Long interval = Long.parseLong(props.getProperty("interval"));
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
        try {
            is = getClass().getResourceAsStream(fileName);
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
