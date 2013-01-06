package example.webstats;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import strada.mapreduce.CommandBuilder;
import strada.mapreduce.HierarchicalAgg;
import strada.mapreduce.ScriptInterpolator;
import strada.services.MapReduceService;
import strada.summarizers.CountMinSketchSummarizer;
import strada.summarizers.Summarizer;

import com.clearspring.analytics.stream.frequency.CountMinSketch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceOutput;

import example.webstats.hits.Hit;
import example.webstats.hits.PointWriter;

@Singleton
public class WebstatsAggregator extends HierarchicalAgg
{
   private static final String SAMPLE_MR_PATH = "src/main/resources/mr/cmin.sample";

   private final PointWriter proc;

   private final MapReduceService mrs;

   private final Map<String, Summarizer<CountMinSketch>> freqs;

   private String testValue;

   @Inject
   public WebstatsAggregator(DB db, MapReduceService mrs) throws IOException
   {
      super();

      this.freqs = new HashMap<String, Summarizer<CountMinSketch>>();
      freqs.put("daily", new CountMinSketchSummarizer(db, "daily.freq", "ip"));
      freqs.put("weekly", new CountMinSketchSummarizer(db, "weekly.freq", "ip"));
      this.proc = new PointWriter(db, toArray(freqs.values()));
      this.mrs = mrs;

      this.testValue = "['oranges', 'download']";
      initInterpolators();
      loadScripts(SAMPLE_MR_PATH);
   }

   public void clearFrequencies()
   {
      for (Summarizer<CountMinSketch> s : freqs.values()) {
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

   @Override
   protected DBCollection getInput()
   {
      return proc.getDataSource();
   }

   @Override
   protected String getOut(String resolution)
   {
      return resolution + "_stats";
   }

   // XXX testing
   @Override
   protected void onBuilder(CommandBuilder builder)
   {
      // no cutoff
      builder.query(null);
   }

   @Override
   protected Future<MapReduceOutput> submit(Callable<MapReduceOutput> callable)
   {
      return mrs.submit(callable);
   }

   private void initInterpolators()
   {
      ScriptInterpolator reduce = new ScriptInterpolator();
      reduce.addVar("testValue", testValue);

      ScriptInterpolator sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),this._id.d.getHours(), 0, 0, 0)");
      setInterpolators("hourly", sc, reduce);

      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),0, 0, 0, 0)");
      setInterpolators("daily", sc, reduce);

      sc = new ScriptInterpolator();
      sc.addVar("date", "weekDate(this._id.d)");
      setInterpolators("weekly", sc, reduce);

      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(), 1, 0, 0, 0, 0)");
      setInterpolators("monthly", sc, reduce);

      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(),1, 1, 0, 0, 0, 0)");
      setInterpolators("yearly", sc, reduce);
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
