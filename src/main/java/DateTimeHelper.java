import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class DateTimeHelper {
    /**
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


    public static Date getNextMidnight() {
        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);
        return date.getTime();
    }

    public static Integer hourToSeconds(Integer hour) {
        return hour * 60 * 60;
    }
}
