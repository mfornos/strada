package strada.viz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BasicChartData implements ChartData
{

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

   @Override
   public String toString()
   {
      return "BasicChartData [data=" + data + "]";
   }

   protected List<Object> getData()
   {
      return data;
   }

}
