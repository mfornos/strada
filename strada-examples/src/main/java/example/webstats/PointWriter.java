package example.webstats;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;
import strada.data.TimeUnit;
import strada.features.Feature;
import strada.features.dimensions.DateTime;
import strada.features.dimensions.Value;
import strada.features.metrics.Counter;
import strada.points.DataPoint;
import strada.summarizers.Summarizer;

import com.clearspring.analytics.stream.frequency.CountMinSketch;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PointWriter
{
   private final Feature counter = new Counter("hits");
   private final Summarizer<CountMinSketch>[] freqs;

   private DBCollection traffic;

   public PointWriter(DB db, Summarizer<CountMinSketch>[] summarizers)
   {
      this.freqs = summarizers;
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

      addUA(point, hit);

      // first summarize
      for (Summarizer<CountMinSketch> s : freqs) {
         point.add(s);
      }
      point.summarize();

      for (Summarizer<CountMinSketch> s : freqs) {
         point.add(new Counter(s.getName(), s.getSummarizer().estimateCount(Integer.toString(hit.ip).hashCode())));
      }

      // then 'upsert', thus we would use summarizer results
      point.upsert();

   }

   private void addUA(DataPoint point, Hit hit)
   {

      Counter os = new Counter("os");
      Counter browser = new Counter("browser");
      Counter browserVersion = new Counter("browser_version");
      for (String name : hit.ua) {
         UserAgent userAgent = UserAgent.parseUserAgentString(name);

         OperatingSystem operatingSystem = userAgent.getOperatingSystem();
         if (operatingSystem != null)
            os.add(operatingSystem.getName());

         Browser b = userAgent.getBrowser();
         if (b != null) {
            browser.add(b.getName());

            Version bv = userAgent.getBrowserVersion();
            if (bv != null) {
               browserVersion.add(new Counter(b.getName()).add(bv.getVersion().replace('.', '_')));
            } else {
               browserVersion.add(new Counter(b.getName()).add("unknown"));
            }
         }
      }

      if (!os.isLeaf())
         point.add(os);
      if (!browser.isLeaf())
         point.add(browser);
      if (!browserVersion.isLeaf())
         point.add(browserVersion);

   }
}
