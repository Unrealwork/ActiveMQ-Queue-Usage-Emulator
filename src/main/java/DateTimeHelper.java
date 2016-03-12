import java.util.Calendar;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class DateTimeHelper {
    /**
     *
     * @return how many seconds passed
     * from today's start
     */
    public static Integer getSecondsOfToday() {
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


    public static Integer hourToSeconds(Integer hour) {
        return hour * 60 * 60;
    }
}
