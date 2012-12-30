package strada.viz;

import java.util.Date;
import java.util.Locale;

import strada.data.TimeUnit;

public interface ChartData extends Iterable<Object>
{

   void add(int index, Object element);

   void add(Object obj);

   Object get(int index);

   Number getNumber();

   Number getNumber(int position);

   String getString();

   String getString(int position);

   boolean isEmpty();

   int size();

   String getFormattedDate(int index);

   Date getDate(int index);

   String getFormattedDate(int index, TimeUnit timeUnit, Locale locale);

   void put(int index, Object value);

   Object[] toArray();

}
