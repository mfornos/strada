package strada.points;

import java.util.Date;

import strada.data.TimeUnit;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Helper methods to generate date-dependent document identifiers.
 * 
 */
public class DataPointId
{

   public static DBObject resolve(Object id, Date ts, TimeUnit unit)
   {
      Preconditions.checkNotNull(id, "id is required");
      Preconditions.checkNotNull(ts, "timestamp is required");
      Preconditions.checkNotNull(unit, "unit is required");

      return new BasicDBObject("oid", id).append("d", unit.prepareDate(ts));
   }

   private DataPointId()
   {
      //
   }

}
