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

   public MongoDynamicChartData(DBObject obj, ChartTable table, String selector)
   {

      this(obj, table, selector, 0);

   }

   public MongoDynamicChartData(DBObject obj, ChartTable table, String selector, int fixedColumn)
   {

      if (fixedColumn > -1) {
         add(Walker.get(obj, table.getColumn(fixedColumn).getName()));
      }

      BasicDBObject dyn = (BasicDBObject) Walker.get(obj, selector);

      for (Map.Entry<String, Object> entry : dyn.entrySet()) {

         ChartColumn column = table.getColumn(entry.getKey());

         if (column == null) {
            column = new ChartColumn(entry.getKey());
            table.addColumn(column);
         }

         add(column.getIndex(), entry.getValue());

      }
   }

}
