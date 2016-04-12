package com.axibase.activemqusageemulator.tools;

import com.axibase.activemqusageemulator.ActiveMQueueBalancer;
import com.axibase.activemqusageemulator.WorkDay;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public class BalanceFunctionGenrator {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PolynomialSplineFunction.class);

    public static PolynomialSplineFunction generateInterpolator(WorkDay workDay) {
        LOG.info("Generating interpolated function");
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
    private static HashMap<Integer, Integer> generateInterpolatedNodes(WorkDay workDay, Integer delta) {
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
}
