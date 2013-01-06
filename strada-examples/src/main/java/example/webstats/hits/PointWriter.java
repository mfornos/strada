package example.webstats.hits;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;
import strada.data.TimeUnit;
import strada.features.Feature;
import strada.features.dimensions.DateTime;
import strada.features.dimensions.Dimension;
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

   public DBCollection getDataSource()
   {
      return traffic;
   }

   public void write(Hit hit)
   {

      DataPoint point = new DataPoint(hit.websiteId + "_" + hit.ip);
      point.withTimeUnit(TimeUnit.HOUR).withTimestamp(hit.ts);
      point.withCollection(traffic);
      if (hit.hasAction()) {
         addActions(point, hit);
      }
      point.add(new DateTime("ts", hit.ts), new Value("ip", hit.ip), counter);

      addUA(point, hit);

      // first summarize
      for (Summarizer<CountMinSketch> s : freqs) {
         point.add(s);
      }

      point.summarize();

      for (Summarizer<CountMinSketch> s : freqs) {
         long estimate = s.getSummarizer().estimateCount(hit.ip.hashCode());
         point.add(new Value(s.getName(), estimate));
      }

      // then 'upsert', thus we would use summarizer results
      point.upsert();

   }

   private void addActions(DataPoint point, Hit hit)
   {
      Dimension dimensions = new Dimension("actions");
      Dimension dim = new Dimension(hit.action.name);
      dim.add(new Counter("count"));
      dim.add(new Value("country", hit.action.country));
      dimensions.add(dim);
      point.add(dimensions);
   }

   private void addUA(DataPoint point, Hit hit)
   {
      Dimension os = new Dimension("os");
      Dimension browser = new Dimension("browser");
      Dimension browserVersion = new Dimension("browser_version");

      UserAgent userAgent = UserAgent.parseUserAgentString(hit.ua);

      OperatingSystem operatingSystem = userAgent.getOperatingSystem();
      if (operatingSystem != null)
         os.add(new Counter(operatingSystem.getName()));

      Browser b = userAgent.getBrowser();
      if (b != null) {
         browser.add(new Counter(b.getName()));

         Version bv = userAgent.getBrowserVersion();
         if (bv != null) {
            browserVersion.add(new Dimension(b.getName()).add(new Counter(bv.getVersion().replace('.', '_'))));
         } else {
            browserVersion.add(new Dimension(b.getName()).add(new Counter("unknown")));
         }
      }

      if (!os.isLeaf())
         point.add(os);
      if (!browser.isLeaf())
         point.add(browser);
      if (!browserVersion.isLeaf())
         point.add(browserVersion);

   }

   private DBCollection openDataSource(DB db)
   {
      if (traffic == null) {
         traffic = db.getCollection("traffic");
         traffic.ensureIndex("ts");
      }
      return traffic;
   }
}
