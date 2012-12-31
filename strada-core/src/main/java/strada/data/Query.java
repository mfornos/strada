package strada.data;

import java.util.Date;

import strada.points.DataPointId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class Query
{
   public static DBObject byId(Object id, Date ts, TimeUnit unit)
   {
      return new BasicDBObject("_id", DataPointId.resolve(id, ts, unit));
   }

   public static DBObject byRange(Date start, Date end)
   {
      return QueryBuilder.start().greaterThanEquals(start).lessThanEquals(end).get();
   }
}
