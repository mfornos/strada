package strada.points;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import strada.data.TimeUnit;
import strada.features.dimensions.Bag;
import strada.features.dimensions.DateTime;
import strada.features.dimensions.GeoPosition;
import strada.features.dimensions.Value;

public class TestDataPoint
{
   @Test
   public void creation()
   {
      DataPoint point = new DataPoint("myid");
      Assert.assertEquals(point.getId(), "myid");
      Assert.assertEquals(point.getTimeUnit(), TimeUnit.HOUR);
      Assert.assertNotNull(point.getTimestamp());
      DBObject id = (DBObject) point.byId().get("_id");
      Assert.assertEquals(id.get("oid"),"myid");
      Assert.assertNotNull(id.get("d"));
      Assert.assertTrue(point.getFeatures().isEmpty());
   }

   @Test
   public void features()
   {
      DataPoint point = new DataPoint("myid", new Date(0), TimeUnit.HOUR, null);
      Date dateTen = new Date(10);
      String[] actions = new String[] { "one", "two" };
      point.add(new Value("page", "index.html"), new DateTime("date", dateTen), new Bag("actions", actions));
      Double[] loc = new Double[]{0.1,0.2};
      point.add(new GeoPosition("location", loc));
      point.add(new GeoPosition("location2", 0.1, 0.2));
      BasicDBObject update = point.buildUpdateCommand();
      DBObject set = (DBObject) update.get("$set");
      Assert.assertEquals(set.get("page"), "index.html");
      Assert.assertEquals(set.get("date"), dateTen);
      Assert.assertEquals(set.get("location"), loc);
      Assert.assertEquals(set.get("location2"), loc);
      set = (DBObject) update.get("$addToSet");
      Assert.assertEquals(set.get("actions"), actions);
   }
}
