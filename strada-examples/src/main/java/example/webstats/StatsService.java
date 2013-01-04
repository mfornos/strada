package example.webstats;

import humanize.Humanize;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import strada.viz.ChartColumn.ColumnType;
import strada.viz.ChartTable;
import strada.viz.MongoTableBuilder;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import example.webstats.charts.Series;
import example.webstats.hits.Hit;
import example.webstats.hits.Hit.Action;
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

   private static Action[] actions = new Action[] { new Action("es", "signup"), new Action("es", "dummy"),
         new Action("es", "download"), new Action("es", "other"), new Action("es", "other 2"),
         new Action("es", "recommend") };
   // Test cohort
   private static Action[] actionsEn = new Action[] { new Action("en", "signup"), new Action("en", "dummy"),
      new Action("en", "download"), new Action("en", "other"), new Action("en", "other 2"),
      new Action("en", "recommend") };

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

   // Regex like: db.users.find({"name": /m/})
   public DBCursor find(String frame, DBObject query)
   {
      return db.getCollection(collectionName(frame)).find(query).sort(new BasicDBObject().append("_id.d", 1));
   }

   public DBCursor find(String frame, String begin, String end) throws ParseException
   {
      DBCursor cursor;
      if (begin != null && end != null) {
         DateFormat dateFormat = Humanize.dateFormatInstance("dd-MM-yyyy");
         cursor = getCollection(collectionName(frame), dateFormat.parse(begin), dateFormat.parse(end));
      } else {
         cursor = getCollection(collectionName(frame));
      }
      return cursor;
   }

   public void generateTraffic(int days, int hits)
   {
      // String[] actions = randomActions();

      Calendar cal = Calendar.getInstance(Series.UTC);
      for (int i = 0; i < days; i++) {
         for (int n = 0; n < hits; n++) {
            stats.onHit(new Hit(n + r.nextInt(100) /* ip + var */, "website", cal.getTime(), randomArray(actions), randomArray(ua)));
            cal.set(Calendar.HOUR_OF_DAY, r.nextInt(23));
         }
         cal.add(Calendar.DATE, -1);
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

   protected String collectionName(String name)
   {
      return name + "_stats";
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

   private <T> T[] randomArray(T[] ar)
   {
      return Arrays.copyOfRange(ar, 0, r.nextInt(ar.length + 1));
   }

}
