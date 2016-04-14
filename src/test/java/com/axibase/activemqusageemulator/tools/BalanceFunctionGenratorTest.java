package com.axibase.activemqusageemulator.tools;

import com.axibase.activemqusageemulator.WorkDay;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shmgrinsky on 14.04.16.
 */
public class BalanceFunctionGenratorTest {
    private WorkDay workDay;
    private PolynomialSplineFunction function;

    private int roundValue(int time) {
        return (int) Math.round(function.value(time));
    }


    public BalanceFunctionGenratorTest() {
        workDay = new WorkDay();
        function = BalanceFunctionGenrator.generateInterpolator(workDay);
    }

    @Test
    public void generateInterpolator() throws Exception {

    }


    /**
     * Тестируем пограничные значения для рабочего дня
     *
     * @throws Exception
     */
    @Test
    public void extremeValueus() throws Exception {
        double startDayValue = roundValue(workDay.getStartDay());
        double endDayValue = roundValue(workDay.getEndDay());
        boolean testResult = (startDayValue >= 0 && startDayValue < 25) && (endDayValue >= 0 && endDayValue < 25);
        assertEquals(testResult, true);
    }

    /**
     * Тестируем ночные значения
     */
    @Test
    public void testMidnightValues() throws Exception {
        int startTime = workDay.getStartDay();
        int workBeginTime = workDay.getWorkBeginTime();
        boolean testResult = true;
        for (int i = startTime; i < workBeginTime; i++) {
            long value = Math.round(function.value(i));
            if (value < 0 || value > 25) {
                testResult = false;
            }
        }
        assertEquals(testResult, true);
    }

    @Test
    public void testLunchBeginTime() throws Exception {
        int lunchBeginTime = workDay.getLunchBeginTime();
        int value = roundValue(lunchBeginTime);
        assertEquals(100, roundValue(lunchBeginTime), 10);
    }
}