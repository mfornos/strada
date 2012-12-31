package strada.data;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public enum TimeUnit {
   HOUR {
      @Override
      public Date prepareDate(Date ts)
      {
         Calendar cal = Calendar.getInstance(UTC);
         cal.setTime(ts);
         cal.clear(Calendar.MINUTE);
         cal.clear(Calendar.SECOND);
         cal.clear(Calendar.MILLISECOND);
         return cal.getTime();
      }
   },
   DAY {
      @Override
      public Date prepareDate(Date ts)
      {
         Calendar cal = Calendar.getInstance(UTC);
         cal.setTime(ts);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.clear(Calendar.MINUTE);
         cal.clear(Calendar.SECOND);
         cal.clear(Calendar.MILLISECOND);
         return cal.getTime();
      }
   },
   WEEK {
      @Override
      public Date prepareDate(Date ts)
      {
         Calendar cal = Calendar.getInstance(UTC);
         cal.setTime(ts);
         cal.set(Calendar.DAY_OF_WEEK, 1);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.clear(Calendar.MINUTE);
         cal.clear(Calendar.SECOND);
         cal.clear(Calendar.MILLISECOND);
         return cal.getTime();
      }
   },
   MONTH {
      @Override
      public Date prepareDate(Date ts)
      {
         Calendar cal = Calendar.getInstance(UTC);
         cal.setTime(ts);
         cal.set(Calendar.DAY_OF_MONTH, 1);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.clear(Calendar.MINUTE);
         cal.clear(Calendar.SECOND);
         cal.clear(Calendar.MILLISECOND);
         return cal.getTime();
      }
   },
   YEAR {
      @Override
      public Date prepareDate(Date ts)
      {
         Calendar cal = Calendar.getInstance(UTC);
         cal.setTime(ts);
         cal.set(Calendar.MONTH, 1);
         cal.set(Calendar.DAY_OF_MONTH, 1);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.clear(Calendar.MINUTE);
         cal.clear(Calendar.SECOND);
         cal.clear(Calendar.MILLISECOND);
         return cal.getTime();
      }
   };

   public static final TimeZone UTC = TimeZone.getTimeZone("GMT+0:00");

   public abstract Date prepareDate(Date ts);

}
