package example.webstats;

import java.util.Map;

import strada.util.Walker;
import strada.viz.BasicChartData;
import strada.viz.ChartColumn;
import strada.viz.ChartColumn.ColumnType;
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
            table.addColumn(new ChartColumn(entry.getKey()));
         }

         add(table.getColumn(entry.getKey()).getIndex(), entry.getValue());

      }
   }

   // XXX
   public MongoDynamicChartData(DBObject obj, int fixedColumn, int u, String selector, ChartTable table)
   {

      if (fixedColumn > -1) {
         add(Walker.get(obj, table.getColumn(fixedColumn).getName()));
      }

      BasicDBObject dyn = (BasicDBObject) Walker.get(obj, selector);

      for (Map.Entry<String, Object> entry : dyn.entrySet()) {

//         BasicDBObject sub = (BasicDBObject) entry.getValue();
//
//         if (!table.hasColumn(entry.getKey())) {
//            table.addColumn(new ChartColumn(entry.getKey(), ColumnType.TABLE));
//         }
//
//         ChartTable subTable = new ChartTable();
//         BasicChartData row = new BasicChartData();
//
//         for (Map.Entry<String, Object> subEntry : sub.entrySet()) {
//            subTable.addColumn(new ChartColumn(subEntry.getKey()));
//            row.add(subEntry.getValue());
//         }
//         
//         subTable.addRow(row);
//         add(table.getColumn(entry.getKey()).getIndex(), subTable);
      }
   }

}
