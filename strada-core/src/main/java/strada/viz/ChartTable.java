package strada.viz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartTable
{
   private List<ChartColumn> columns;

   private List<ChartData> rows;

   private final AtomicInteger autoIncrement;

   public ChartTable()
   {
      this.columns = new ArrayList<ChartColumn>();
      this.rows = new ArrayList<ChartData>();
      this.autoIncrement = new AtomicInteger(-1);
   }

   public ChartTable addColumn(ChartColumn column)
   {
      if (column.getIndex() < 0) {
         column.setIndex(autoIncrement.incrementAndGet());
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

   public int[] getColumnIndexes()
   {
      return getColumnIndexes(0);
   }

   public int[] getColumnIndexes(int index)
   {
      int[] columns = null;
      if (columnsNum() > index) {
         columns = new int[columnsNum() - index];
         for (int i = index; i < columnsNum(); i++) {
            columns[i - index] = i;
         }
      } else {
         columns = new int[] { 0 };
      }
      return columns;
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
         values[i++] = ((Number) row.get(column)).doubleValue();
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
      this.rows = rows;
      return this;
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
}
