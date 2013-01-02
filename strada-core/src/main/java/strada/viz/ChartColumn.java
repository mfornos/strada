package strada.viz;

import strada.data.TimeUnit;

public class ChartColumn
{
   public static enum ColumnType {
      OBJECT, NUMBER, INTEGER, DECIMAL, TEXT, DATE, TIMESTAMP
   }

   private String name;

   private String displayName;

   private ColumnType type;

   private int index;

   private TimeUnit timeUnit;

   public ChartColumn()
   {
      this(-1, "unnamed", ColumnType.NUMBER);
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
      return isDate() ? ColumnType.TEXT : type;
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

      return ColumnType.DATE.equals(type) || ColumnType.TIMESTAMP.equals(type);
   }

   public boolean isNumeric()
   {
      return ColumnType.NUMBER.equals(type) || ColumnType.INTEGER.equals(type) || ColumnType.DECIMAL.equals(type);
   }

   public boolean isText()
   {
      return ColumnType.TEXT.equals(type);
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

   @Override
   public String toString()
   {
      return "ChartColumn [name=" + name + ", displayName=" + displayName + ", type=" + type + ", index=" + index
            + ", timeUnit=" + timeUnit + "]";
   }
}
