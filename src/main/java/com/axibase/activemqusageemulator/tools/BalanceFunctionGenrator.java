package com.axibase.activemqusageemulator.tools;

import com.axibase.activemqusageemulator.WorkDay;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by shmgrinsky on 12.04.16.
 */
public class BalanceFunctionGenrator {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PolynomialSplineFunction.class);
    private static final Random rand = new Random();
    private static final int EPS = 10;

    public static PolynomialSplineFunction generateInterpolator(WorkDay workDay) {
        LOG.info("Generating interpolated function");
        Map<Integer, Integer> interpolatedNodes = generateInterpolatedNodes(workDay, 600);
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


    private static Map<Integer, Integer> genrateIntervalValueWithBounds(int startInterval, int endInterval, int startBound,
                                                                        int endBound, int delta) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        int dBound = endBound - startBound;
        int dTime = endInterval - startInterval;
        result.put(startInterval, valueWithDelta(startBound, EPS));
        for (int i = startInterval + delta; i < endInterval; i += delta) {
            int time = valueWithDelta(i, delta);
            double coef = (double) (time - startInterval) / (double) dTime;
            double approxValue = startBound + dBound * coef;

            int resultValue = valueWithDelta(approxValue, EPS);
            result.put(time, resultValue);
        }
        result.put(endInterval, valueWithDelta(endBound, EPS));
        return result;
    }


    private static int valueWithDelta(double value, double delta) {
        return (int) (value + rand.nextInt((int) Math.round(delta)) - delta / 2);
    }

    /**
     * @param workDay
     * @return
     */
    private static Map<Integer, Integer> generateInterpolatedNodes(WorkDay workDay, Integer delta) {
        Integer startDay = workDay.getStartDay();
        Integer endDay = workDay.getEndDay();
        Integer workBeginTime = workDay.getWorkBeginTime();
        Integer lunchBeginTime = workDay.getLunchBeginTime();
        Integer lunchEndTime = workDay.getLunchEndTime();
        Integer workEndTime = workDay.getWorkEndTime();
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

        result.put(startDay, valueWithDelta(10, EPS));

        result.putAll(genrateIntervalValueWithBounds(startDay, workBeginTime, 10, 10, delta));
        result.putAll(genrateIntervalValueWithBounds(workBeginTime, lunchBeginTime, 10, 100, delta));
        int lunchMinimumTime = workDay.getLunchMinTime();
        result.putAll(genrateIntervalValueWithBounds(lunchBeginTime, lunchMinimumTime, 100, 30, delta));
        result.putAll(genrateIntervalValueWithBounds(lunchMinimumTime, lunchEndTime, 30, 100, delta));
        result.putAll(genrateIntervalValueWithBounds(lunchEndTime, workEndTime, 100, 10, delta));
        result.putAll(genrateIntervalValueWithBounds(workEndTime, endDay, 10, 10, delta));
        result.put(endDay, valueWithDelta(10, EPS));
        return result;
    }

    public static void main(String[] args) {
        Map<Integer, Integer> interpolatedNodes = generateInterpolatedNodes(new WorkDay(), 100);
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
        PolynomialSplineFunction function = new LinearInterpolator().interpolate(x, y);
        for (int time = 0; time < 86399; time += 100)
            LOG.info("{} {}", time, function.value(time));
    }
}
