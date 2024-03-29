package strada.viz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartTable
{
   private List<ChartColumn> columns;

   private List<ChartData> rows;

   private final AtomicInteger autoIncrement;

   private boolean stdEnabled;

   private Map<Integer, Std> stds;

   public ChartTable()
   {
      this.stdEnabled = true;
      this.columns = new ArrayList<ChartColumn>();
      this.rows = new ArrayList<ChartData>();
      this.autoIncrement = new AtomicInteger(-1);
      this.stds = new HashMap<Integer, Std>();
   }

   public ChartTable addColumn(ChartColumn column)
   {
      if (column.getIndex() < 0) {
         column.setIndex(autoIncrement.incrementAndGet());
      } else if (autoIncrement.get() < column.getIndex()) {
         autoIncrement.set(column.getIndex());
      }
      columns.add(column);
      return this;
   }

   public ChartTable addColumns(ChartColumn... columns)
   {
      for (ChartColumn column : columns) {
         addColumn(column);
      }
      return this;
   }

   public ChartTable addRow(ChartData row)
   {
      if (stdEnabled) {
         computeStd(row);
      }
      rows.add(row);
      return this;
   }

   public int columnsNum()
   {
      return this.columns.size();
   }

   public ChartColumn getColumn(int index)
   {
      return columns.get(index);
   }

   public ChartColumn getColumn(String name)
   {
      for (ChartColumn c : columns) {
         if (name.equals(c.getName()))
            return c;
      }
      return null;
   }

   public int[] getColumnIndexes()
   {
      return getColumnIndexes(0);
   }

   public int[] getColumnIndexes(int from)
   {
      int[] columns = null;
      int cnum = columnsNum();
      if (cnum > from) {
         columns = new int[cnum - from];
         for (int i = from; i < cnum; i++) {
            columns[i - from] = i;
         }
      } else {
         columns = new int[] { 0 };
      }
      return columns;
   }

   public List<String> getColumnNames()
   {
      return getColumnNames(0);
   }

   public List<String> getColumnNames(int from)
   {
      List<String> names = new ArrayList<String>();
      int cnum = columns.size();
      for (int i = from; i < cnum; i++) {
         names.add(columns.get(i).getName());
      }
      return names;
   }

   public List<ChartColumn> getColumns()
   {
      return Collections.unmodifiableList(columns);
   }

   public double[] getDoubleRowArray(int column)
   {
      double[] values = new double[rowsNum()];
      int i = 0;
      for (ChartData row : rows) {
         Number number = (Number) row.get(column);
         values[i++] = (number == null) ? 0 : number.doubleValue();
      }
      return values;
   }

   public long[] getLongRowArray(int column)
   {
      long[] values = new long[rowsNum()];
      int i = 0;
      for (ChartData row : rows) {
         values[i++] = (Long) row.get(column);
      }
      return values;
   }

   public long[] getLongRowArrayAllowingNull(int column)
   {
      long[] values = new long[rowsNum()];
      int i = 0;
      for (ChartData row : rows) {
         values[i++] = row.get(column) != null ? (Long) row.get(column) : 0;
      }
      return values;
   }

   public ChartData getRow(int rowIndex)
   {
      return rows.get(rowIndex);
   }

   public List<ChartData> getRows()
   {
      return Collections.unmodifiableList(rows);
   }

   public Std getStd(int index)
   {
      Std tmp = stds.get(index);
      if (tmp == null) {
         tmp = new Std();
         stds.put(index, tmp);
      }
      return tmp;
   }

   public boolean hasColumn(ChartColumn column)
   {
      return columns.contains(column);
   }

   public boolean hasColumn(String columnName)
   {
      for (ChartColumn c : columns) {
         if (columnName.equals(c.getName()))
            return true;
      }
      return false;
   }

   public boolean isStdEnabled()
   {
      return stdEnabled;
   }

   public void removeColumn(int columnIndex)
   {
      this.columns.remove(columnIndex);
   }

   public int rowsNum()
   {
      return this.rows.size();
   }

   public ChartTable setColumns(List<ChartColumn> columns)
   {
      for (ChartColumn column : columns) {
         addColumn(column);
      }
      return this;
   }

   public ChartTable setRows(List<ChartData> rows)
   {
      for (ChartData row : rows) {
         addRow(row);
      }
      return this;
   }

   public void setStdEnabled(boolean stdEnabled)
   {
      this.stdEnabled = stdEnabled;
   }

   public Object[][] toArray()
   {
      Object[][] dst = new Object[rowsNum()][columnsNum()];
      int r = 0;
      for (ChartData data : rows) {
         dst[r++] = data.toArray();
      }
      return dst;
   }

   @Override
   public String toString()
   {
      return "ChartTable [columns=" + columns + ", rows=" + rows + ", autoIncrement=" + autoIncrement + ", stdEnabled="
            + stdEnabled + ", stds=" + stds + "]";
   }

   protected void computeStd(ChartData row)
   {
      for (ChartColumn column : columns) {
         if (column.isNumeric()) {
            int index = column.getIndex();
            Std std = getStd(index);
            Number number = row.getNumber(index);
            if (number != null) {
               std.compute(number);
            }
         }
      }
   }

}
