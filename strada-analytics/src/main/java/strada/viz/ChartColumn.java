package strada.viz;

import strada.data.TimeUnit;

public class ChartColumn
{
   public static enum ColumnType {
      NUMBER, STRING, DATE, TIMESTAMP, FLOAT
   }

   private String name;

   private String displayName;

   private ColumnType type;

   private int index;

   private TimeUnit timeUnit;

   public ChartColumn()
   {
   }

   public ChartColumn(int index, String name, ColumnType type)
   {
      this.name = name;
      this.type = type;
      this.index = index;
   }

   public ChartColumn(String name)
   {
      this(-1, name, ColumnType.NUMBER);
   }

   public ChartColumn(String name, ColumnType type)
   {
      this(-1, name, type);
   }

   public String getDisplayName()
   {
      return displayName;
   }

   public ColumnType getEffectiveType()
   {
      return isDate() ? ColumnType.STRING : type;
   }

   public int getIndex()
   {
      return index;
   }

   public String getName()
   {
      return name;
   }

   public TimeUnit getTimeUnit()
   {
      return timeUnit;
   }

   public ColumnType getType()
   {
      return type;
   }

   public boolean isDate()
   {

      return (ColumnType.DATE.equals(type) || ColumnType.TIMESTAMP.equals(type));
   }

   public boolean isNumeric()
   {
      return (ColumnType.NUMBER.equals(type) || ColumnType.FLOAT.equals(type));
   }

   public boolean isString()
   {
      return ColumnType.STRING.equals(type);
   }

   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   public void setIndex(int index)
   {
      this.index = index;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setTimeUnit(TimeUnit timeUnit)
   {
      this.timeUnit = timeUnit;
   }

   public void setType(ColumnType type)
   {
      this.type = type;
   }
}
