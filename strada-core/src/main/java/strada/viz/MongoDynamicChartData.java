package strada.viz;

import java.util.Map;

import strada.util.Walker;

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

      if (dyn != null) {
         addDBObject(table, dyn);
      }
   }

   private void addDBObject(ChartTable table, BasicDBObject dyn)
   {
      for (Map.Entry<String, Object> entry : dyn.entrySet()) {

         if (!table.hasColumn(entry.getKey())) {
            ChartColumn nCol = new ChartColumn(entry.getKey());
            table.addColumn(nCol);
         }

         ChartColumn column = table.getColumn(entry.getKey());
         add(column.getIndex(), entry.getValue());

      }
   }

}
