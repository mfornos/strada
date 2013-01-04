package example.webstats.charts;

import humanize.Humanize;
import strada.viz.ChartColumn;
import strada.viz.ChartData;
import strada.viz.ChartTable;

public class Funnel
{

   public int[] levels;
   public String[] levelsPrint;
   public int[] conversionRates;
   public String[] labels;
   public int[] indices;

   public Funnel(ChartTable table, String... columns)
   {
      levels = new int[columns.length];
      levelsPrint = new String[columns.length];
      conversionRates = new int[columns.length];
      indices = new int[columns.length];
      labels = columns;
      int max = 0;

      for (ChartData row : table.getRows()) {
         for (int i = 0; i < columns.length; i++) {
            ChartColumn column = table.getColumn(columns[i]);
            if (column != null) {
               int index = column.getIndex();
               indices[i] = index;
               Number obj = row.getNumber(index);
               levels[i] += obj == null ? 0 : obj.intValue();
               if (levels[i] > max) {
                  max = levels[i];
               }
            } else {
               indices[i] = -1;
               break;
            }
         }
      }

      for (int i = 0; i < levels.length; i++) {
         conversionRates[i] = (int) (((float) levels[i] / max) * 100f);
         levelsPrint[i] = Humanize.metricPrefix(levels[i]);
      }
   }

}
