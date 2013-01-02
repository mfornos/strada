package strada.viz;

import java.util.Date;

public interface ChartData extends Iterable<Object>
{

   void add(int index, Object element);

   void add(Object obj);

   Object get(int index);

   Object get(ChartColumn column);

   Number getNumber();

   Number getNumber(int position);

   String getString();

   String getString(int position);

   boolean isEmpty();

   int size();

   Date getDate(int index);

   void put(int index, Object value);

   Object[] toArray();

   boolean exists(int index);

}
