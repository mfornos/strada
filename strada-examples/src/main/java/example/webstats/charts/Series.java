package example.webstats.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import strada.util.Walker;
import strada.viz.ChartData;
import strada.viz.ChartTable;
import strada.viz.Std;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
      public String size;
      public Map<String, Object> dataLabels = new HashMap<String, Object>();

      public Serie()
      {
         dataLabels.put("distance", -30);
         dataLabels.put("color", "#FFF");
      }
   }

   static ObjectMapper om = new ObjectMapper();

   static {
      om.disable(SerializationFeature.WRAP_ROOT_VALUE);
      om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      om.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      om.setSerializationInclusion(Include.NON_NULL);
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

   public static String toDetailPieChart(ChartTable table, Map<String, Map<String, Double>> subData, int... columns)
         throws JsonProcessingException
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

      SubSerie subSerie = new SubSerie();

      for (int i = 0; i < columns.length; i++) {

         double sum = stds[i].getSum();

         serie.data[i] = new Object[] { names[i], sum };

         // Normalize sub-data value
         if (subData.containsKey(names[i])) {
            Map<String, Double> subVals = subData.get(names[i]);
            for (Map.Entry<String, Double> subVal : subVals.entrySet()) {
               subSerie.data.add(new Object[] { subVal.getKey(), subVal.getValue() });
            }
         }

      }

      serie.size = "60%";
      String out = om.writeValueAsString(new Object[] { serie, subSerie });
      return out;
   }

   public static class SubSerie
   {
      public List<Object[]> data = new ArrayList<Object[]>();
      public String name;
      public String innerSize = "60%";
   }

   public static Map<String, Map<String, Double>> getSubData(DBCursor cursor, String selector)
   {
      Map<String, Map<String, Double>> subData = new LinkedHashMap<String, Map<String, Double>>();

      for (DBObject obj : cursor) {
         BasicDBObject dyn = (BasicDBObject) Walker.get(obj, selector);

         if (dyn == null)
            continue;

         for (Map.Entry<String, Object> entry : dyn.entrySet()) {

            BasicDBObject nobj = (BasicDBObject) entry.getValue();

            for (Map.Entry<String, Object> subEntry : nobj.entrySet()) {

               Map<String, Double> vs = subData.get(entry.getKey());

               if (vs == null) {
                  vs = new HashMap<String, Double>();
               }

               Double val = 0.0;
               if (vs.containsKey(subEntry.getKey())) {
                  val = vs.get(subEntry.getKey());
               }

               try {
                  vs.put(subEntry.getKey(), val + ((Number) subEntry.getValue()).doubleValue());
                  subData.put(entry.getKey(), vs);
               } catch (Exception e) {
                  e.printStackTrace();
               }

            }

         }
      }

      return subData;
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
