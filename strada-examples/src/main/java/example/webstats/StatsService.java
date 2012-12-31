package example.webstats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import strada.data.TimeUnit;
import strada.mapreduce.CommandBuilder;
import strada.mapreduce.ScriptInterpolator;
import strada.services.MapReduceService;
import strada.summarizers.CountMinSketchSummarizer;
import strada.summarizers.Summarizer;

import com.clearspring.analytics.stream.frequency.CountMinSketch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.QueryBuilder;

@Singleton
public class StatsService
{
   private static final String SAMPLE_MR_PATH = "src/main/resources/mr/cmin.sample";

   private static final Logger LOGGER = LoggerFactory.getLogger(StatsService.class);

   private final PointWriter proc;

   private final String[] aggs = new String[] { "hourly", "daily", "weekly", "monthly", "yearly" };

   private final MapReduceService mrs;

   private final Map<String, ScriptInterpolator> interpols;

   private final Map<String, Summarizer<CountMinSketch>> freqs;

   private Date lastRun;

   @Inject
   public StatsService(DB db, MapReduceService mrs)
   {
      this.freqs = new HashMap<String, Summarizer<CountMinSketch>>();
      freqs.put("daily", new CountMinSketchSummarizer(db, "daily.freq", "ip"));
      freqs.put("weekly", new CountMinSketchSummarizer(db, "weekly.freq", "ip"));
      this.proc = new PointWriter(db, toArray(freqs.values()));
      this.mrs = mrs;
      this.interpols = new HashMap<String, ScriptInterpolator>();
      this.lastRun = new Date(0);
      initInterpolators();
   }

   public void aggregate()
   {
      // XXX debug
      // DBCursor cursor = proc.getDataSource().find();
      // for (DBObject o : cursor) {
      // System.out.println(o);
      // }

      try {
         List<Future<MapReduceOutput>> tasks = new ArrayList<Future<MapReduceOutput>>(aggs.length);

         Calendar cal = Calendar.getInstance(TimeUnit.UTC);
         Date cutoff = cal.getTime();

         for (String agg : aggs) {
            Future<MapReduceOutput> t = hierarchicalAggregate(cutoff, agg);
            tasks.add(t);
         }

         // XXX testing
         // lastRun = cutoff;

         // wait for results
         for (Future<MapReduceOutput> task : tasks) {
            LOGGER.info("Got {}", task.get());
         }
      } catch (Exception e) {
         LOGGER.error(e.getMessage(), e);
      }
   }

   public void clearFrequencies()
   {
      for(Summarizer<CountMinSketch> s : freqs.values()) {
         s.reset();
      }
   }

   public void onHit(final Hit hit)
   {

      proc.write(hit);

   }

   public void serializeFrequencies()
   {
      for (Summarizer<CountMinSketch> freq : freqs.values()) {
         freq.serialize();
      }
   }

   protected Future<MapReduceOutput> hierarchicalAggregate(Date cutoff, String agg)
   {

      DBCollection c = proc.getDataSource();
      String out = agg + "_stats";

      CommandBuilder builder = CommandBuilder.startCommand(c, out, OutputType.MERGE).forPathName(SAMPLE_MR_PATH);
      builder.mapInterpolator(interpols.get(agg));
      builder.query(QueryBuilder.start("ts").lessThan(cutoff).greaterThan(lastRun).get());

      return mrs.submit(builder.buildCallable(c));

   }

   private void initInterpolators()
   {
      ScriptInterpolator sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),this._id.d.getHours(), 0, 0, 0)");
      interpols.put("hourly", sc);
      
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),0, 0, 0, 0)");
      interpols.put("daily", sc);
      
      sc = new ScriptInterpolator();
      // XXX not working for hourly variations
      sc.addVar("date", "new Date(this._id.d.valueOf() - this._id.d.getDay()*86400000)");
      interpols.put("weekly", sc);
      
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(), 1, 0, 0, 0, 0)");
      interpols.put("monthly", sc);
      
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(),1, 1, 0, 0, 0, 0)");
      interpols.put("yearly", sc);
   }

   private Summarizer<CountMinSketch>[] toArray(Collection<Summarizer<CountMinSketch>> elements)
   {
      @SuppressWarnings("unchecked")
      Summarizer<CountMinSketch>[] result = new Summarizer[elements.size()];
      int index = 0;
      for (Summarizer<CountMinSketch> k : elements)
         result[index++] = k;
      return result;
   }

}
