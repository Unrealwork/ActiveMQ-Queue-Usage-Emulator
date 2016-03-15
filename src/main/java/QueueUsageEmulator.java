import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import javax.jms.Connection;
import java.util.*;

/**
 * Created by shmgrinsky on 11.03.16.
 */
public class QueueUsageEmulator extends Thread {

    private Consumer consumer;
    private Producer producer;
    private PolynomialSplineFunction function;
    private Logger logger = Logger.getLogger(QueueUsageEmulator.class);
    private Timer producedTimer, consumerTimer, interpolateTimer;
    private Connection connection;
    private WorkDay workDay;


    public QueueUsageEmulator(WorkDay workDay, String queueName, String serverURI, String user, String password) throws Exception {
        this.workDay = workDay;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, serverURI/*"tcp://hbs.axibase.com:5022"*/);
        connection = connectionFactory.createConnection();
        connection.start();
        consumer = new Consumer(connection, queueName);
        producer = new Producer(connection, queueName);
        logger.l7dlog(Priority.INFO, "Creating of interpolate function", null);
        function = generateInterpolator();
        producedTimer = new Timer();
        consumerTimer = new Timer();
    }

    private class ProducerTask extends TimerTask {

        @Override
        public void run() {
            try {
                Long currentQueueSize = consumer.getQueueSize();
                Integer currentTime = DateTimeHelper.getSecondsOfToday();
                Long preferredQueueSize = Math.round(function.value(currentTime));
                logger.l7dlog(Priority.INFO, "Producer : preferredQueueSize: " + preferredQueueSize + "; currentQueueSize: " + currentQueueSize + ";", null);
                while (currentQueueSize < preferredQueueSize) {
                    producer.sendMessage("message");
                    currentQueueSize++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class ConsumerTask extends TimerTask {


        @Override
        public void run() {
            try {
                Long currentQueueSize = consumer.getQueueSize();
                Integer currentTime = DateTimeHelper.getSecondsOfToday();
                Long preferredQueueSize = Math.round(function.value(currentTime));
                logger.l7dlog(Priority.INFO, "Consumer : preferredQueueSize: " + preferredQueueSize + "; currentQueueSize: " + currentQueueSize + ";", null);
                while (currentQueueSize > preferredQueueSize) {
                    consumer.receiveMessage();
                    currentQueueSize--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private PolynomialSplineFunction generateInterpolator() {
        HashMap<Integer, Integer> interpolatedNodes = generateInterpolatedNodes(workDay, 600);
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
        return new LoessInterpolator().interpolate(x, y);
    }


    /**
     * @param workDay
     * @return
     */
    private HashMap<Integer, Integer> generateInterpolatedNodes(WorkDay workDay, Integer delta) {
        Integer startDay = workDay.getStartDay();
        Integer endDay = workDay.getEndDay();
        Integer workBeginTime = workDay.getWorkBeginTime();
        Integer lunchBeginTime = workDay.getLunchBeginTime();
        Integer lunchEndTime = workDay.getLunchEndTime();
        Integer workEndTime = workDay.getWorkEndTime();
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Integer time = startDay; time < workBeginTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (!(randomTime < 0 || randomTime > workBeginTime)) {
                result.put(randomTime, new Random().nextInt(20));
            }
        }
        for (Integer time = workBeginTime; time < lunchBeginTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (!(randomTime < workBeginTime || randomTime > lunchBeginTime)) {
                Double part = ((randomTime - workBeginTime) * 1.0) / (lunchBeginTime - workBeginTime);
                Integer limit = (int) Math.round(part * 90);
                Integer randomQueueSize = limit + new Random().nextInt(20);
                result.put(randomTime, randomQueueSize);
            }
        }
        Integer randomLunchMinTime = new Random().nextInt(DateTimeHelper.hourToSeconds(1)) + lunchBeginTime;
        result.put(randomLunchMinTime, 30);
        for (Integer time = lunchEndTime; time < workEndTime; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (!(randomTime < lunchEndTime || randomTime > workEndTime)) {
                Double part = 1 - ((randomTime - lunchEndTime) * 1.0) / (workEndTime - lunchEndTime);
                Integer limit = (int) Math.round(part * 90);
                Integer randomQueueSize = limit + new Random().nextInt(20);
                result.put(randomTime, randomQueueSize);
            }
        }
        for (Integer time = workEndTime; time < endDay; time += delta) {
            Integer randomTime = time += new Random().nextInt(delta) - delta / 2;
            if (!(randomTime < workEndTime || randomTime > endDay)) {
                result.put(randomTime, new Random().nextInt(20));
            }
        }
        result.put(endDay - 1, new Random().nextInt(20));
        return result;
    }

    @Override
    public void run() {
        Integer delay = 2 * 1000;
        producedTimer.schedule(new ProducerTask(), new Date(), delay);
        consumerTimer.schedule(new ConsumerTask(), new Date(System.currentTimeMillis() + delay / 2), delay);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
