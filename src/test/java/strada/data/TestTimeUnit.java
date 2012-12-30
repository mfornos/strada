package strada.data;

import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTimeUnit
{
   @Test
   public void units()
   {
      Calendar calendar = Calendar.getInstance(TimeUnit.UTC);
      calendar.set(2012, 11, 5, 4, 5);
      Date d = calendar.getTime();
      Date w = TimeUnit.WEEK.prepareDate(d);
      Calendar t = Calendar.getInstance(TimeUnit.UTC);
      t.setTime(w);
      Assert.assertEquals(t.get(Calendar.DAY_OF_WEEK), 1);

      w = TimeUnit.DAY.prepareDate(d);
      t.setTime(w);
      Assert.assertEquals(t.get(Calendar.HOUR), 0);

      w = TimeUnit.HOUR.prepareDate(d);
      t.setTime(w);
      Assert.assertEquals(t.get(Calendar.HOUR), 4);
      Assert.assertEquals(t.get(Calendar.MINUTE), 0);
      Assert.assertEquals(t.get(Calendar.SECOND), 0);
      Assert.assertEquals(t.get(Calendar.MILLISECOND), 0);
   }
}
