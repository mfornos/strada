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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.Ints;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import example.webstats.charts.HighchartsConfig.Marker;

// XXX quick and dirty
public class Series
{

   public static class Serie<T>
   {
      public String name;
      public T data;
      public String type;
      public String size;
      public Map<String, Object> dataLabels = new HashMap<String, Object>();
      public Long pointInterval;
      public Date pointStart;
      public String dashStyle;
      public Boolean showInLegend;

      public Serie()
      {
         dataLabels.put("distance", -30);
         dataLabels.put("color", "#FFF");
      }
   }

   public static class Data
   {
      public Data(Object x, Object y)
      {
         this.x = x;
         this.y = y;
      }

      public String color;
      public Object x, y;
      public Marker marker;
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

   public static Object[] toDetailPieSeries(ChartTable table, Map<String, Map<String, Double>> subData,
         boolean percent, int... columns) throws JsonProcessingException
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
      return new Object[] { serie, subSerie };
   }

   public static void toDetailPieSeries(HighchartsConfig conf, ChartTable table,
         Map<String, Map<String, Double>> subData, boolean percent, int... columns) throws JsonProcessingException
   {
      conf.series = toDetailPieSeries(table, subData, percent, columns);
   }

   public static void toFrequencySeries(HighchartsConfig conf, ChartTable table, int column)
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

      conf.series = new Object[] { serie };
      conf.xAxis.categories = labels;
   }

   public static void toHourFrequencySeries(HighchartsConfig hfreqConfig, ChartTable table, int column)
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

      hfreqConfig.series = new Object[] { serie };
      hfreqConfig.xAxis.categories = labels;
   }

   public static Serie<List<Number>>[] toHourlyDenseSeries(ChartTable table, int... cs)
   {
      @SuppressWarnings("unchecked")
      Serie<List<Number>>[] series = new Serie[cs.length];
      int j = 0;
      for (int c : cs) {
         Serie<List<Number>> serie = new Serie<List<Number>>();
         serie.pointStart = table.getRow(0).getDate(0);
         serie.pointInterval = 3600 * 1000L;
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
            serie.data.add((number == null) ? 0 : number);
            lastDate = currentDate;
         }
         series[j++] = serie;
      }
      return series;
   }

   public static void toHourlyDenseSeries(HighchartsConfig conf, ChartTable table, int... cs)
   {
      conf.series = toHourlyDenseSeries(table, cs);
   }

   public static Serie<Object[][]> toPieSeries(ChartTable table, int... columns)
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

   public static void toPieSeries(HighchartsConfig conf, ChartTable table, int... columns)
   {
      conf.series = new Object[] { toPieSeries(table, columns) };
   }

   public static Serie<Data[]>[] toSeries(ChartTable table, int... columns)
   {
      @SuppressWarnings("unchecked")
      Serie<Data[]>[] series = new Serie[columns.length * 2];

      for (int i = 0; i < columns.length; i++) {
         String name = table.getColumn(columns[i]).getName();
         series[i] = serieFromTable(table, columns[i], name);
      }

      // TODO configurable open interval!
      // open interval
      for (int i = 0; i < columns.length; i++) {
         // String name = table.getColumn(columns[i]).getName();
         Serie<Data[]> serie = new Serie<Data[]>();
         serie.data = new Data[2];
         serie.showInLegend = false;
         serie.dashStyle = "ShortDash";
         for (int j = 1; j < 3; j++) {
            ChartData row = table.getRow(table.rowsNum() - j);
            serie.data[j - 1] = new Data(row.getDate(0), row.getNumber(columns[i]));
         }
         series[i + columns.length] = serie;
      }

      return series;
   }

   public static void toSeries(HighchartsConfig conf, ChartTable table, int... columns)
   {
      conf.series = toSeries(table, columns);
   }

   private static Serie<Data[]> serieFromTable(ChartTable table, int rowIndex, String name)
   {
      Serie<Data[]> serie = new Serie<Data[]>();
      // TODO configurable open interval!
      // open interval
      serie.data = new Data[table.rowsNum() - 1];
      serie.name = name;

      int i = 0;
      int lastIndex = table.rowsNum() - 1;

      for (ChartData row : table.getRows()) {
         Date date = row.getDate(0);
         Number number = row.getNumber(rowIndex);
         Data data = new Data(date.getTime(), number);
         serie.data[i] = data;
         i++;
         if (i == lastIndex) {
            break;
         }
      }

      return serie;
   }
}
