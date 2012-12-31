package example.webstats.charts;

import java.util.Date;

import strada.viz.ChartData;
import strada.viz.ChartTable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Series
{
   static ObjectMapper om = new ObjectMapper();
   static {
      om.disable(SerializationFeature.WRAP_ROOT_VALUE);
   }

   public static String toData(ChartTable table, int... columns) throws JsonProcessingException
   {
      Serie[] series = new Serie[columns.length];
      for (int i = 0; i < columns.length; i++) {
         series[i] = newSerie(table, columns[i], table.getColumn(columns[i]).getName());
      }

      String out = om.writeValueAsString(series);
      System.out.println(out);
      return out;
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

   public static class Serie
   {
      public String name;
      public Object[][] data;
   }
}
