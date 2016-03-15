/**
 * Created by shmagrinsky on 12.03.16.
 */
public class WorkDay {
    private Integer startDay;
    private Integer endDay;
    private Integer workBeginTime;
    private Integer lunchBeginTime;
    private Integer lunchEndTime;
    private Integer workEndTime;

    public WorkDay() {
        startDay = DateTimeHelper.hourToSeconds(0);
        endDay = DateTimeHelper.hourToSeconds(24);
        workBeginTime = DateTimeHelper.hourToSeconds(8);
        lunchBeginTime = DateTimeHelper.hourToSeconds(12);
        lunchEndTime = DateTimeHelper.hourToSeconds(13);
        workEndTime = DateTimeHelper.hourToSeconds(18);
    }


    public WorkDay(Integer workBeginTime, Integer lunchBeginTime, Integer lunchEndTime, Integer workEndTime) {
        startDay = DateTimeHelper.hourToSeconds(0);
        endDay = DateTimeHelper.hourToSeconds(24);
        this.workBeginTime = workBeginTime;
        this.lunchBeginTime = lunchBeginTime;
        this.lunchEndTime = lunchEndTime;
        this.workEndTime = workEndTime;
    }


    public Integer getStartDay() {
        return startDay;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public Integer getWorkBeginTime() {
        return workBeginTime;
    }

    public Integer getLunchBeginTime() {
        return lunchBeginTime;
    }

    public Integer getLunchEndTime() {
        return lunchEndTime;
    }

    public Integer getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(Integer workEndTime) {
        this.workEndTime = workEndTime;
    }
}
