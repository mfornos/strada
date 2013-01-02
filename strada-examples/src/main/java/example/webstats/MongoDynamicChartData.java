package example.webstats;

import java.util.Map;

import strada.util.Walker;
import strada.viz.BasicChartData;
import strada.viz.ChartColumn;
import strada.viz.ChartTable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoDynamicChartData extends BasicChartData
{

   public MongoDynamicChartData(DBObject obj, String selector, ChartTable table)
   {

      this(obj, 0, selector, table);

   }

   public MongoDynamicChartData(DBObject obj, int fixedColumn, String selector, ChartTable table)
   {

      if (fixedColumn > -1) {
         add(Walker.get(obj, table.getColumn(fixedColumn).getName()));
      }

      BasicDBObject dyn = (BasicDBObject) Walker.get(obj, selector);

      for (Map.Entry<String, Object> entry : dyn.entrySet()) {

         if (!table.hasColumn(entry.getKey())) {
            ChartColumn col = new ChartColumn(entry.getKey());
            table.addColumn(col);
         }

         try {
            ChartColumn column = table.getColumn(entry.getKey());
            add(column.getIndex(), entry.getValue());
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

}
