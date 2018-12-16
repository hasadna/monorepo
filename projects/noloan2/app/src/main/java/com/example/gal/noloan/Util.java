package projects.noloan.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

  public static String formatTime(long time) {
    Date date = new Date(time);
    Locale locale = new Locale("he", "IL");
    SimpleDateFormat formatter = new SimpleDateFormat("d לLLLL yyyy", locale);
    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
    return formatter.format(date);
  }
}

