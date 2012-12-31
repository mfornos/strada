package strada.viz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import strada.data.TimeUnit;

public class BasicChartData implements ChartData
{

   private static final String YEAR_PATTERN = "yyyy";

   private static final String MONTH_PATTERN = "MMM";

   private static final String DAY_PATTERN = "MMM dd";

   private static final String HOUR_PATTERN = "HH:mm";

   private List<Object> data;

   public BasicChartData()
   {
      data = new ArrayList<Object>();
   }

   public BasicChartData(Object... objs)
   {
      this();
      for (Object obj : objs) {
         data.add(obj);
      }
   }

   public void add(int index, Object element)
   {
      data.add(index, element);
   }

   public void add(Object obj)
   {
      data.add(obj);
   }

   public boolean addAll(Collection<? extends Object> c)
   {
      return data.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends Object> c)
   {
      return data.addAll(index, c);
   }

   public void addAll(Object[] objs)
   {
      for (Object obj : objs) {
         data.add(obj);
      }
   }

   public boolean contains(Object o)
   {
      return data.contains(o);
   }

   public Object get(int index)
   {
      return data.get(index);
   }

   public Date getDate(int index)
   {
      return (Date) data.get(index);
   }

   @Override
   public String getFormattedDate(int index)
   {
      return getFormattedDate(index, TimeUnit.DAY, null);
   }

   public String getFormattedDate(int index, String pattern, Locale locale)
   {
      return '\'' + formatDate(getDate(index), pattern, locale) + '\'';
   }

   @Override
   public String getFormattedDate(int index, TimeUnit timeUnit, Locale locale)
   {
      String result = "";
      switch (timeUnit) {
      case HOUR:
         result = getFormattedDate(index, HOUR_PATTERN, locale);
         break;
      case DAY:
         result = getFormattedDate(index, DAY_PATTERN, locale);
         break;
      case MONTH:
         result = getFormattedDate(index, MONTH_PATTERN, locale);
         break;
      case WEEK:
         result = getFormattedDate(index, DAY_PATTERN, locale);
         // final Date[] weekRange =
         // DateUtils.getWeekStartAndEnd(getDate(index));
         // result = String.format(WEEK_RANGE_MASK, formatDate(weekRange[0],
         // DAY_RANGE_PATTERN, locale), formatDate(weekRange[1],
         // DAY_RANGE_PATTERN, locale));
         break;
      case YEAR:
         result = getFormattedDate(index, YEAR_PATTERN, locale);
         break;
      }

      return result;
   }

   public String getGDataDate(int index)
   {
      Date date = (Date) data.get(index);
      if (date == null) {
         date = new Date();
      }

      final Calendar cal = Calendar.getInstance();
      cal.setTime(date);

      final String strDate = String.format("(%4d, %1d, %1d)", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

      return "new Date" + strDate;
   }

   public Number getNumber()
   {
      return getNumber(0);
   }

   public Number getNumber(int position)
   {
      return (Number) data.get(position);
   }

   public String getString()
   {
      return getString(0);
   }

   public String getString(int position)
   {
      return (String) data.get(position);
   }

   public int indexOf(Object o)
   {
      return data.indexOf(o);
   }

   public boolean isEmpty()
   {
      return data.isEmpty();
   }

   public Iterator<Object> iterator()
   {
      return data.iterator();
   }

   @Override
   public void put(int index, Object value)
   {
      data.set(index, value);
   }

   public int size()
   {
      return data.size();
   }

   @Override
   public Object[] toArray()
   {
      return data.toArray();
   }

   protected List<Object> getData()
   {
      return data;
   }

   private String formatDate(Date date, String pattern, Locale locale)
   {
      return new SimpleDateFormat(pattern, locale).format(date);
   }

}
