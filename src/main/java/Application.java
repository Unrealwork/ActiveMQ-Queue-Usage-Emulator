import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.log4j.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.management.MBeanServerConnection;
import java.util.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class Application extends Thread {

    private ActiveMQConnectionFactory connectionFactory;
    private Consumer consumer;
    private Producer producer;
    private PolynomialSplineFunction function;
    private MBeanServerConnection mBeanServerConnection;
    private Logger logger = Logger.getLogger(Application.class);
    private Timer producedTimer, consumerTimer;

    public static void main(String[] args) {
        try {
            new Application().start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Application() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory("admin", null, "tcp://hbs.axibase.com:5022");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        consumer = new Consumer(connection);
        producer = new Producer(connection);
        function = generateInterpolator();
        double randomTime = new Random().nextInt(hourToSeconds(24));
        producer.sendMessage("a");
        producedTimer = new Timer();
        consumerTimer = new Timer();
    }

    private class ProducerTask extends TimerTask {

        @Override
        public void run() {
            try {
                Long currentQueueSize = consumer.getQueueSize();
                Date date = new Date();
                Long preferedQueueSize = Math.round(function.value(getCurrentSecondsOfDay()));
                System.out.println("Producer: preferredQueueSize: " + preferedQueueSize + "; currentQueueSize: " + currentQueueSize + ";");
                while (currentQueueSize < preferedQueueSize) {
                    producer.sendMessage("message");
                    currentQueueSize++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Integer getCurrentSecondsOfDay() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long passed = now - c.getTimeInMillis();
        long secondsPassed = passed / 1000;
        return (int) secondsPassed;
    }

    private class ConsumerTask extends TimerTask {

        @Override
        public void run() {
            try {
                Long currentQueueSize = consumer.getQueueSize();
                Date date = new Date();
                Long preferedQueueSize = Math.round(function.value(getCurrentSecondsOfDay()));
                System.out.println("Consumer: preferredQueueSize: " + preferedQueueSize + "; currentQueueSize: " + currentQueueSize + ";");
                while (currentQueueSize > preferedQueueSize) {
                    consumer.receiveMessage();
                    currentQueueSize--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Integer hourToSeconds(Integer hour) {
        return hour * 60 * 60;
    }

    private PolynomialSplineFunction generateInterpolator() {
        HashMap<Integer, Integer> interpolatedNodes = generateInterpolatedNodes();
        double[] x = new double[interpolatedNodes.size()];
        double[] y = new double[interpolatedNodes.size()];
        Integer[] times = new Integer[interpolatedNodes.size()];
        interpolatedNodes.keySet().toArray(times);
        Arrays.sort(times);
        int i = 0;
        for (Integer time : times) {
            x[i] = time;
            y[i] = interpolatedNodes.get(time);
            i++;
        }
        return new LinearInterpolator().interpolate(x, y);
    }

    private HashMap<Integer, Integer> generateInterpolatedNodes() {
        Integer startDay = hourToSeconds(0);
        Integer endDay = hourToSeconds(24);
        Integer workBeginTime = hourToSeconds(8);
        Integer lunchBeginTime = hourToSeconds(12);
        Integer lunchEndTime = hourToSeconds(13);
        Integer workEndTime = hourToSeconds(18);
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
        Integer delta = 600;
        for (Integer time = startDay; time < workBeginTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (randomTime < 0 || randomTime > workBeginTime) {
                continue;
            } else {
                result.put(randomTime, new Random().nextInt(20));
            }
        }
        for (Integer time = workBeginTime; time < lunchBeginTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (randomTime < workBeginTime || randomTime > workBeginTime) {
                continue;
            } else {
                Integer limit = 20 + (randomTime - workBeginTime) / (lunchBeginTime - workBeginTime) * 90;
                result.put(randomTime, new Random().nextInt(limit) + new Random().nextInt(20));
            }
        }
        Integer randomLunchMinTime = new Random().nextInt(hourToSeconds(1)) + lunchBeginTime;
        result.put(randomLunchMinTime, 30);
        for (Integer time = lunchEndTime; time < workEndTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (randomTime < lunchEndTime || randomTime > workEndTime) {
                continue;
            } else {
                Integer limit = 20 + (1 - (randomTime - lunchEndTime) / (workEndTime - lunchBeginTime)) * 90;
                result.put(randomTime, new Random().nextInt(limit) + new Random().nextInt(20));
            }
        }
        for (Integer time = workEndTime; time < endDay; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (randomTime < workEndTime || randomTime > endDay) {
                continue;
            } else {
                result.put(randomTime, new Random().nextInt(20));
            }
        }
        return result;
    }


    public void run() {
        Integer delay = 10 * 1000;
        producedTimer.schedule(new ProducerTask(), new Date(), delay);
        consumerTimer.schedule(new ConsumerTask(), new Date(System.currentTimeMillis() + delay / 2), delay);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }
}
