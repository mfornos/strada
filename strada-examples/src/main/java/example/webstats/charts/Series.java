package example.webstats.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import strada.viz.ChartColumn;
import strada.viz.ChartData;
import strada.viz.ChartTable;
import strada.viz.Std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import example.webstats.charts.Series.Serie;

// XXX quick and dirty
public class Series
{
   public static class DenseSerie
   {
      public String name;
      public List<Number> data;
      public String type = "area";
      public Long pointInterval = 3600 * 1000L;
      public Date pointStart;
   }

   public static class Serie
   {
      public String name;
      public Object[][] data;
      // public FillColor fillColor;
   }

   static ObjectMapper om = new ObjectMapper();

   static {
      om.disable(SerializationFeature.WRAP_ROOT_VALUE);
   }

   public static String toPieData(ChartTable table, int... columns) throws JsonProcessingException
   {
      Serie serie = toPieChartSerie(table, columns);
      String out = om.writeValueAsString(new Serie[] { serie });
      return out;
   }

   private static Serie toPieChartSerie(ChartTable table, int... columns)
   {
      Serie serie = new Serie();
      serie.data = new Object[columns.length][2];
      Std[] stds = new Std[columns.length];
      String[] names = new String[columns.length];

      double total = 0;

      for (int i = 0; i < columns.length; i++) {
         names[i] = table.getColumn(columns[i]).getName();
         stds[i] = table.getStd(table.getColumn(columns[i]).getIndex());
         total += stds[i].getSum();
      }

      for (int i = 0; i < columns.length; i++) {
         serie.data[i] = new Object[] { names[i], stds[i].getSum() / total };
      }
      return serie;
   }

   public static String toData(ChartTable table, int... columns) throws JsonProcessingException
   {
      Serie[] series = new Serie[columns.length];

      for (int i = 0; i < columns.length; i++) {
         String name = table.getColumn(columns[i]).getName();
         series[i] = newSerie(table, columns[i], name);
      }

      String out = om.writeValueAsString(series);
      return out;
   }

   public static String toHourlyDenseData(ChartTable table, int... cs) throws JsonProcessingException
   {
      DenseSerie[] series = new DenseSerie[cs.length];
      int j = 0;
      for (int c : cs) {
         DenseSerie serie = new DenseSerie();
         serie.pointStart = table.getRow(0).getDate(0);
         serie.data = new ArrayList<Number>();
         serie.name = table.getColumn(c).getName();

         Date lastDate = null;
         for (ChartData row : table.getRows()) {
            // fill empty hours
            Date currentDate = row.getDate(0);
            if (lastDate != null) {
               long secs = (currentDate.getTime() - lastDate.getTime()) / 1000;
               long hours = secs / 3600;
               if (hours > 1) {
                  for (int h = 0; h < hours - 1; h++) {
                     serie.data.add(0);
                  }
               }
            }
            Number number = row.getNumber(c);
            serie.data.add(number);
            lastDate = currentDate;
         }
         series[j++] = serie;
      }

      return om.writeValueAsString(series);
   }

   private static Serie newSerie(ChartTable table, int f, String name)
   {
      Serie serie = new Serie();
      serie.data = new Object[table.rowsNum()][2];
      serie.name = name;
      int i = 0;

      for (ChartData row : table.getRows()) {
         Date date = row.getDate(0);
         Number number = row.getNumber(f);
         serie.data[i][0] = date.getTime();
         serie.data[i][1] = number;
         i++;
      }

      return serie;
   }
}
