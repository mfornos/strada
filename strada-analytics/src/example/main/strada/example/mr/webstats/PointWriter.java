package strada.example.mr.webstats;

import strada.data.TimeUnit;
import strada.features.Feature;
import strada.features.dimensions.DateTime;
import strada.features.dimensions.Value;
import strada.features.metrics.Counter;
import strada.features.summarizers.Bloom;
import strada.points.DataPoint;

import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PointWriter
{
   private final Feature counter = new Counter("hits");
   private final Bloom members;

   private DBCollection traffic;

   public PointWriter(DB db, Bloom members)
   {
      this.members = members;
      openDataSource(db);
   }

   DBCollection getDataSource()
   {
      return traffic;
   }

   DBCollection openDataSource(DB db)
   {
      if (traffic == null) {
         traffic = db.getCollection("traffic");
         traffic.ensureIndex("ts");
      }
      return traffic;
   }

   Counter toActions(Hit hit)
   {
      Counter action = new Counter("action");
      for (String name : hit.actions) {
         action.add(name);
      }
      return action;
   }

   void write(Hit hit)
   {

      DataPoint point = new DataPoint(hit.websiteId + "_" + hit.ip);
      point.withTimeUnit(TimeUnit.HOUR).withTimestamp(hit.ts);
      point.withCollection(traffic).add(toActions(hit));
      point.add(new DateTime("ts", hit.ts), new Value("ip", hit.ip), counter);

      // first summarize
      point.add(members);
      point.summarize();

      // then 'upsert', thus counters can count using summarizer results
      // ...so not in this example :)
      point.upsert();
   }
}
