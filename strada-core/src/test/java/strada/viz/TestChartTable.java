package strada.viz;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import strada.viz.ChartColumn.ColumnType;

public class TestChartTable
{
   @Test
   public void arrayData() throws IOException
   {
      ChartTable ct = new ChartTable();
      ct.addColumns(new ChartColumn("one", ColumnType.DATE), new ChartColumn("two"), new ChartColumn("three"));
      ct.addRow(new BasicChartData(new Date(5000000000L * 24), 10, 0.5));
      ct.addRow(new BasicChartData(new Date(5100000000L * 24), 15, 1.5));

      Assert.assertEquals(ct.toArray()[1][1], 15);
   }

   @Test
   public void simpleData()
   {
      ChartTable ct = new ChartTable();
      ct.addColumns(new ChartColumn("one", ColumnType.DATE), new ChartColumn("two"), new ChartColumn("three"));
      ct.addRow(new BasicChartData(new Date(0), 10, 0.5));
      ct.addRow(new BasicChartData(new Date(1), 15, 1.5));

      Assert.assertEquals(ct.getRows().size(), 2);
      Assert.assertEquals(ct.getRow(0).getDate(0), new Date(0));
      Assert.assertEquals(ct.getRow(1).getNumber(2), 1.5);
   }

   @Test
   public void simpleHead()
   {
      ChartTable ct = new ChartTable();
      ct.addColumns(new ChartColumn("one", ColumnType.DATE), new ChartColumn("two"), new ChartColumn("three"));
      List<ChartColumn> cols = ct.getColumns();
      Assert.assertEquals(cols.size(), 3);
      Assert.assertEquals(ct.getColumn(2).getName(), "three");
      Assert.assertEquals(ct.getColumn(0).getType(), ColumnType.DATE);
   }
}
