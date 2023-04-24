/*
 * $Id: DateUtils.java,v 1.2 2006/04/05 15:16:33 boyan Exp $
 */
package at.fhj.utils.misc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Ilya Boyandin
 *
 * $Revision: 1.2 $
 */
public class DateUtils {

  private static DateFormat parseableDateFormat =
    new SimpleDateFormat("EEE MMM dd hh:mm:ss aa zzz yyyy", Locale.US);

  public static String formatDate(Date date) {
    return parseableDateFormat.format(date);
  }

  public static Date parseDate(String date) throws ParseException {
    return parseableDateFormat.parse(date);
  }
  
  public static String timeIntervalToString(long millis) {
    String s;
    long secs = millis / 1000;
    if (secs >= 60) {
      long mins = secs / 60;
      if (mins >= 60) {
        long hours = mins / 60;
        if (hours >= 24) {
          long days = hours / 24;
          hours -= days * 24;
          s =  days + " day" + (days == 1 ? "" : "s") +
            " " + hours + " hour" + (hours == 1 ? "" : "s");
        } else {
          mins -= hours * 60;
          s = itoa(hours, 2) + ":" + itoa(mins, 2) + " hours";
        }
      } else {
        secs -= mins * 60;
        s = itoa(mins, 2) + ":" + itoa(secs, 2) + " min";
      }
    } else {
      s = secs + " sec";
    }
    return s;
  }
  
  private static String itoa(long val, int width) {
    StringBuffer s = new StringBuffer(width);
    for (int i = 0, w = 1; i < width; i++, w *= 10) {
      if (val < w) {
        s.append('0');
      }
    }
    if (val > 0) {
      s.append(val);
    } else if (val < 0) {
      s.append(-val);
      s.insert(0, '-');
    }
    return s.toString();
  }
}
