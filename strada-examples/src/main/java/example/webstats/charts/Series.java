package example.webstats.charts;

import humanize.Humanize;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.google.common.primitives.Ints;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

// XXX quick and dirty
public class Series
{

   public static class FrequencyData
   {

      final String data;
      final String labels;

      public FrequencyData(String data, String labels)
      {
         this.data = data;
         this.labels = labels;
      }

      public String getData()
      {
         return data;
      }

      public String getLabels()
      {
         return labels;
      }

   }

   public static class Serie<T>
   {
      public String name;
      public T data;
      public String type;
      public String size;
      public Map<String, Object> dataLabels = new HashMap<String, Object>();
      public Long pointInterval;
      public Date pointStart;

      public Serie()
      {
         dataLabels.put("distance", -30);
         dataLabels.put("color", "#FFF");
      }
   }

   public static class SubSerie
   {
      public List<Object[]> data = new ArrayList<Object[]>();
      public String name;
      public String innerSize = "60%";
   }

   static ObjectMapper om = new ObjectMapper();

   static {
      om.disable(SerializationFeature.WRAP_ROOT_VALUE);
      om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      om.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      om.setSerializationInclusion(Include.NON_NULL);
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
      @SuppressWarnings("unchecked")
      Serie<Object[][]>[] series = new Serie[columns.length];

      for (int i = 0; i < columns.length; i++) {
         String name = table.getColumn(columns[i]).getName();
         series[i] = serieFromTable(table, columns[i], name);
      }

      String out = om.writeValueAsString(series);
      return out;
   }

   public static String toDetailPieChart(ChartTable table, Map<String, Map<String, Double>> subData, boolean percent,
         int... columns) throws JsonProcessingException
   {
      Serie<Object[][]> serie = new Serie<Object[][]>();
      serie.data = new Object[columns.length][2];
      Std[] stds = new Std[columns.length];
      String[] names = new String[columns.length];

      double total = 0;

      for (int i = 0; i < columns.length; i++) {
         names[i] = table.getColumn(columns[i]).getName();
         stds[i] = table.getStd(table.getColumn(columns[i]).getIndex());
         if (percent) {
            total += stds[i].getSum();
         }
      }

      SubSerie subSerie = new SubSerie();

      for (int i = 0; i < columns.length; i++) {

         double sum = stds[i].getSum();
         serie.data[i] = new Object[] { names[i], (percent) ? sum / total : sum };

         // Normalize sub-data value
         if (subData.containsKey(names[i])) {
            Map<String, Double> subVals = subData.get(names[i]);
            for (Map.Entry<String, Double> subVal : subVals.entrySet()) {
               subSerie.data.add(new Object[] { subVal.getKey(),
                     (percent) ? subVal.getValue() / total : subVal.getValue() });
            }
         }

      }

      serie.size = "60%";
      String out = om.writeValueAsString(new Object[] { serie, subSerie });
      return out;
   }

   public static FrequencyData toFrequency(ChartTable table, int column) throws JsonProcessingException
   {
      Serie<int[]> serie = new Serie<int[]>();

      double[] data = table.getDoubleRowArray(column);

      int max = 50;
      int min = 5;
      int numBins = 10;
      int[] first = new int[min];
      int[] last = new int[1];
      String[] labels = new String[numBins + min + 1];

      final int[] result = new int[numBins];
      final double binSize = (max - min) / numBins;

      for (double d : data) {
         if (d < 1)
            continue;

         int bin = (int) ((d - min) / binSize);
         if (d <= min) {
            int i = (int) d;
            first[i - 1] += 1;
         } else if (d >= max || bin >= numBins) {
            last[0] += 1;
         } else {
            result[bin] += 1;
         }
      }

      for (int i = 0; i < numBins + min + 1; i++) {

         if (i < min) {
            labels[i] = Humanize.spellDigit(i + 1);
         } else if (i >= numBins + min) {
            labels[i] = "+" + max;
         } else {
            int bin = (int) ((i - min) * binSize);
            labels[i] = String.format("%s-%s", bin + min, (int) (bin + min + binSize));
         }
      }

      serie.data = Ints.concat(first, result, last);

      String out = om.writeValueAsString(new Object[] { serie });
      return new FrequencyData(out, om.writeValueAsString(labels));
   }

   public static String toHourlyDenseData(ChartTable table, int... cs) throws JsonProcessingException
   {
      @SuppressWarnings("unchecked")
      Serie<List<Number>>[] series = new Serie[cs.length];
      int j = 0;
      for (int c : cs) {
         Serie<List<Number>> serie = new Serie<List<Number>>();
         serie.pointStart = table.getRow(0).getDate(0);
         serie.pointInterval = 3600 * 1000L;
         serie.type = "area";
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

   public static String toPieData(ChartTable table, int... columns) throws JsonProcessingException
   {
      Serie<Object[][]> serie = toPieChartSerie(table, columns);
      String out = om.writeValueAsString(new Serie[] { serie });
      return out;
   }

   protected static void putInBin(Map<String, Double> bins, double v, String key)
   {
      if (!bins.containsKey(key)) {
         bins.put(key, v);
      } else {
         bins.put(key, bins.get(v) + v);
      }
   }

   private static Serie<Object[][]> serieFromTable(ChartTable table, int rowIndex, String name)
   {
      Serie<Object[][]> serie = new Serie<Object[][]>();
      serie.data = new Object[table.rowsNum()][2];
      serie.name = name;
      int i = 0;
      for (ChartData row : table.getRows()) {
         Date date = row.getDate(0);
         Number number = row.getNumber(rowIndex);
         serie.data[i][0] = date.getTime();
         serie.data[i][1] = number;
         i++;
      }
      return serie;
   }

   private static Serie<Object[][]> toPieChartSerie(ChartTable table, int... columns)
   {
      Serie<Object[][]> serie = new Serie<Object[][]>();
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

   public static Object toHourFrequency(ChartTable table, int column) throws JsonProcessingException
   {
      Serie<int[]> serie = new Serie<int[]>();

      String[] labels = new String[24];
      final int[] result = new int[24];

      Calendar cal = Calendar.getInstance();

      for (ChartData row : table.getRows()) {
         cal.setTime(row.getDate(column));
         int hour = cal.get(Calendar.HOUR_OF_DAY);
         result[hour] += 1;
      }

      for (int i = 0; i < 24; i++) {

         labels[i] = String.format("%s:00", i);
      }

      serie.data = result;

      String out = om.writeValueAsString(new Object[] { serie });
      return new FrequencyData(out, om.writeValueAsString(labels));
   }
}
