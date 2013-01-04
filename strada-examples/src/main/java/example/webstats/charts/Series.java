package example.webstats.charts;

import humanize.Humanize;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import strada.util.Walker;
import strada.viz.ChartData;
import strada.viz.ChartTable;
import strada.viz.Std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import example.webstats.charts.HighchartsConfig.Marker;

public class Series
{

   public static class Data
   {
      public String color;

      public Object x, y;
      public Marker marker;

      public Data(Object x, Object y)
      {
         this.x = x == null ? 0 : x;
         this.y = y == null ? 0 : y;
      }
   }

   public static class Serie<T>
   {
      public Number lineWidth;
      public String color;
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

   public static class SubSerie
   {
      public List<Object[]> data = new ArrayList<Object[]>();
      public String name;
      public String innerSize = "60%";
   }

   public static final TimeZone UTC = TimeZone.getTimeZone("GMT+00:00");

   private static long millisInDay = 60 * 60 * 24 * 1000;

   public static Map<String, Map<String, Double>> detailData(DBCursor cursor, String selector)
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

   public static void frequencySeries(HighchartsConfig conf, ChartTable table, int column)
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

   public static void hourFrequencySeries(HighchartsConfig hfreqConfig, ChartTable table, int column)
   {
      Serie<int[]> serie = new Serie<int[]>();

      String[] labels = new String[24];
      final int[] result = new int[24];

      Calendar cal = Calendar.getInstance(UTC);

      for (ChartData row : table.getRows()) {
         cal.setTime(row.getDate(column));
         int hour = cal.get(Calendar.HOUR_OF_DAY);
         result[hour] += 1;
      }

      int lastHour = result[23];
      result[23] = result[0];
      result[0] = lastHour;

      for (int i = 0; i < 24; i++) {
         labels[i] = String.format("%s:00", i + 1);
      }

      serie.data = result;

      hfreqConfig.series = new Object[] { serie };
      hfreqConfig.xAxis.categories = labels;
   }

   public static Serie<List<Number>>[] hourlyDenseSeries(ChartTable table, int... cs)
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

   public static void hourlyDenseSeries(HighchartsConfig conf, ChartTable table, int... cs)
   {
      conf.series = hourlyDenseSeries(table, cs);
   }

   public static Object[] pieDetailSeries(ChartTable table, Map<String, Map<String, Double>> subData, boolean percent,
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
      return new Object[] { serie, subSerie };
   }

   public static void pieDetailSeries(HighchartsConfig conf, ChartTable table,
         Map<String, Map<String, Double>> subData, boolean percent, int... columns) throws JsonProcessingException
   {
      conf.series = pieDetailSeries(table, subData, percent, columns);
   }

   public static Serie<Object[][]> pieSeries(ChartTable table, int... columns)
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

   public static void pieSeries(HighchartsConfig conf, ChartTable table, int... columns)
   {
      conf.series = new Object[] { pieSeries(table, columns) };
   }

   public static Serie<Data[]>[] timeSeries(ChartTable table, int... columns)
   {
      return timeSeries(table, 0, columns);
   }

   @SuppressWarnings("unchecked")
   public static Serie<Data[]>[] timeSeries(ChartTable table, int skipRows, int[] columns)
   {
      List<Serie<Data[]>> series = new ArrayList<Serie<Data[]>>();

      for (int i = 0; i < columns.length; i++) {
         if (columns[i] > -1) {
            String name = table.getColumn(columns[i]).getName();
            series.add(timeSerie(table, columns[i], skipRows, name));
         }
      }

      return series.toArray(new Serie[] {});
   }

   public static void timeSeries(HighchartsConfig conf, ChartTable table, int... columns)
   {
      conf.series = timeSeries(table, columns);
   }

   public static Serie<Data[]>[] timeSeriesOpenEnd(ChartTable table, int... columns)
   {
      @SuppressWarnings("unchecked")
      Serie<Data[]>[] openSeries = new Serie[columns.length];
      Serie<Data[]>[] series = timeSeries(table, 1, columns);

      for (int i = 0; i < columns.length; i++) {
         Serie<Data[]> serie = new Serie<Data[]>();
         serie.data = new Data[2];
         serie.showInLegend = false;
         serie.dashStyle = "ShortDash";
         for (int j = 2, n = 0; j > 0; j--) {
            ChartData row = table.getRow(table.rowsNum() - j);
            serie.data[n++] = new Data(zeroTime(row.getDate(0)), row.get(columns[i]));
         }
         openSeries[i] = serie;
      }

      @SuppressWarnings("unchecked")
      Serie<Data[]>[] concat = ObjectArrays.concat(series, openSeries, Serie.class);
      return concat;
   }

   public static void timeSeriesOpenEnd(HighchartsConfig conf, ChartTable table, int... columns)
   {
      Serie<Data[]>[] series = timeSeriesOpenEnd(table, columns);
      int len = columns.length * 2;
      int c = 0;
      for (int i = columns.length; i < len; i++) {
         series[i].color = conf.colors[c++];
         series[i].lineWidth = 2;
      }
      conf.series = series;
   }

   private static Serie<Data[]> timeSerie(ChartTable table, int rowIndex, int skipRows, String name)
   {
      Serie<Data[]> serie = new Serie<Data[]>();
      serie.data = new Data[table.rowsNum() - skipRows];
      serie.name = name;

      int i = 0;
      int lastIndex = table.rowsNum() - skipRows;

      for (ChartData row : table.getRows()) {
         Data data = new Data(zeroTime(row.getDate(0)), row.get(rowIndex));
         serie.data[i] = data;
         i++;
         if (i == lastIndex) {
            break;
         }
      }

      return serie;
   }

   private static long zeroTime(Date date)
   {
      return (date.getTime() / millisInDay) * millisInDay;
   }
}
