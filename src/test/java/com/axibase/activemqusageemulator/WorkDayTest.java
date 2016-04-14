package com.axibase.activemqusageemulator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shmgrinsky on 14.04.16.
 */
public class WorkDayTest {
    @Test
    public void getLunchMinTime() throws Exception {
        int lunchMinTime = workDay.getLunchMinTime();
        boolean resultTest = lunchMinTime >= workDay.getLunchBeginTime() && lunchMinTime < workDay.getLunchEndTime();
        assertEquals(resultTest, true);
    }

    private WorkDay workDay;
    private static final int DEFAULT_START_TIME = 0;
    private static final int DEFAULT_END_TIME = 86399;
    private static final int DEFAULT_WORK_BEGIN_TIME = 28800;
    private static final int DEFAULT_LUNCH_BEGIN_TIME = 43200;
    private static final int DEFAULT_LUNCH_END_TIME = 46800;
    private static final int DEFAULT_WORK_END_TIME = 64800;


    public WorkDayTest() {
        workDay = new WorkDay();
    }

    @Test
    public void getStartDay() throws Exception {
        int value = workDay.getStartDay();
        assertEquals(DEFAULT_START_TIME, value);
    }

    @Test
    public void getEndDay() throws Exception {
        int value = workDay.getEndDay();
        assertEquals(DEFAULT_END_TIME, value);
    }

    @Test
    public void getWorkBeginTime() throws Exception {
        int value = workDay.getWorkBeginTime();
        assertEquals(DEFAULT_WORK_BEGIN_TIME, value);
    }

    @Test
    public void getLunchBeginTime() throws Exception {
        int value = workDay.getLunchBeginTime();
        assertEquals(DEFAULT_LUNCH_BEGIN_TIME, value);
    }

    @Test
    public void getLunchEndTime() throws Exception {
        int value = workDay.getLunchEndTime();
        assertEquals(DEFAULT_LUNCH_END_TIME, value);
    }

    @Test
    public void getWorkEndTime() throws Exception {
        int value = workDay.getWorkEndTime();
        assertEquals(DEFAULT_WORK_END_TIME, value);
    }

}