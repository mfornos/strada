package example.webstats;

import humanize.Humanize;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import strada.viz.ChartTable;
import strada.viz.MongoTableBuilder;
import strada.viz.ChartColumn.ColumnType;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import example.webstats.hits.Hit;
import example.webstats.misc.RandomString;

@Singleton
public class StatsService
{
   private static String[] ua = new String[] {
         "Opera/9.0 (Nintendo Wii; U; ; 2071; Wii Shop Channel/1.0; en)",
         "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
         "SonyEricssonK550i/R1JD Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
         "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; en-us; Silk/1.1.0-80) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16 Silk-Accelerated=true" };

   private final WebstatsAggregator stats;
   private final DB db;
   private final Random r = new Random();
   private final RandomString rs = new RandomString(15);

   @Inject
   public StatsService(DB db, WebstatsAggregator stats)
   {
      this.db = db;
      this.stats = stats;
   }

   public void aggregate()
   {
      stats.aggregate();
      stats.serializeFrequencies();
   }

   public void drop()
   {
      stats.clearFrequencies();
      db.dropDatabase();
   }

   public void generateTraffic(int days, int hits)
   {
      // String[] actions = randomActions();
      String[] actions = new String[] { "pears", "oranges", "apples", "lemons" };
      Calendar cal = Calendar.getInstance();
      for (int i = 0; i < days; i++) {
         for (int n = 0; n < hits; n++) {
            stats.onHit(new Hit(n + r.nextInt(100) /* ip + var */, "website", cal.getTime(), actions, Arrays.copyOfRange(ua, 0, r.nextInt(ua.length + 1))));
            cal.set(Calendar.HOUR_OF_DAY, r.nextInt(22) + 1);
         }
         cal.add(Calendar.DAY_OF_MONTH, -1);
      }
   }

   public DBCursor getCollection(String collection)
   {
      return db.getCollection(collection).find().sort(new BasicDBObject().append("_id.d", 1));
   }

   public DBCursor getCollection(String collection, Date begin, Date end)
   {
      DBObject query = QueryBuilder.start("_id.d").greaterThanEquals(begin).lessThanEquals(end).get();
      return db.getCollection(collection).find(query).sort(new BasicDBObject().append("_id.d", 1));
   }

   public ChartTable getData(DBCursor cursor)
   {

      return MongoTableBuilder.fromCursor(cursor).columns(ColumnType.DATE, "value.ts").columns("value.total", "value.unique", "value.first", "value.repeat", "value.daily.first", "value.daily.repeat").build();

   }

   public ChartTable getDynamicData(DBCursor cursor, String selector)
   {

      return MongoTableBuilder.fromCursor(cursor).columns(ColumnType.DATE, "value.ts").dynamic(selector).build();

   }

   public DBCursor openCursor(String frame, String begin, String end) throws ParseException
   {
      DBCursor cursor;
      if (begin != null && end != null) {
         DateFormat dateFormat = Humanize.dateFormatInstance("dd-MM-yyyy");
         cursor = getCollection(frame + "_stats", dateFormat.parse(begin), dateFormat.parse(end));
      } else {
         cursor = getCollection(frame + "_stats");
      }
      return cursor;
   }

   protected String[] randomActions()
   {
      int na = r.nextInt(15) + 1;
      String[] actions = new String[na];
      for (int j = 0; j < na; j++) {
         actions[j] = rs.nextString();
      }
      return actions;
   }

}
