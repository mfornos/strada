package strada.viz;

import java.util.List;

import strada.util.Walker;

import com.mongodb.DBObject;

public class MongoChartData extends BasicChartData
{

   public MongoChartData(DBObject obj, List<ChartColumn> columns)
   {
      for (ChartColumn column : columns) {
         add(Walker.get(obj, column.getName()));
      }
   }

}
