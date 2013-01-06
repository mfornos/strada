package strada.viz;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class BasicChartData implements ChartData
{

   private final Map<Integer, Object> data;
   private int currentIndex;

   public BasicChartData()
   {
      this.data = new LinkedHashMap<Integer, Object>();
      this.currentIndex = 0;
   }

   public BasicChartData(Object... objs)
   {
      this();
      for (Object obj : objs) {
         data.put(currentIndex++, obj);
      }
   }

   public void add(int index, Object element)
   {
      data.put(index, element);
   }

   public void add(Object obj)
   {
      data.put(currentIndex++, obj);
   }

   @Override
   public boolean exists(int index)
   {
      return data.containsKey(index);
   }

   @Override
   public Object get(ChartColumn column)
   {
      return get(column.getIndex());
   }

   public Object get(int index)
   {
      return data.get(index);
   }

   public Date getDate(int index)
   {
      return (Date) data.get(index);
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

   public boolean isEmpty()
   {
      return data.isEmpty();
   }

   public Iterator<Object> iterator()
   {
      return data.values().iterator();
   }

   @Override
   public void put(int index, Object value)
   {
      data.put(index, value);
   }

   public int size()
   {
      return data.size();
   }

   @Override
   public Object[] toArray()
   {
      return data.values().toArray();
   }

   @Override
   public String toString()
   {
      return "BasicChartData [data=" + data + "]";
   }

   protected Map<Integer, Object> getData()
   {
      return data;
   }

}
