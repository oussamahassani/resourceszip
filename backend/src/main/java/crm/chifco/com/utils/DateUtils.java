package crm.chifco.com.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static Date getStartOfMonth() {
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 1);
    return calendar.getTime();
  }

  public static Date getEndOfMonth() {
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.SECOND, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 1);
    return calendar.getTime();
  }

  public static Date getStartOfLastMonth() {
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today); // Reset to current date
    calendar.add(Calendar.MONTH, -1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 1);
    Date startOfLastMonth = calendar.getTime();
    return startOfLastMonth;
  }

  public static Date getEndOfLastMonth() {
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 1);
    calendar.add(Calendar.SECOND, -1);
    return calendar.getTime();
  }
}
