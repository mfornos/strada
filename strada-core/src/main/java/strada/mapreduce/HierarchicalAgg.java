package strada.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import strada.data.TimeUnit;

import com.google.common.io.Files;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.QueryBuilder;

public abstract class HierarchicalAgg implements Aggregator
{
   protected class Interpolators
   {
      public ScriptInterpolator mapInterpolator;
      public ScriptInterpolator reduceInterpolator;
      public ScriptInterpolator finalizeInterpolator;
   }

   protected class Scripts
   {
      public String map;
      public String reduce;
      public String finalize;

      public boolean hasFinalize()
      {
         return finalize != null;
      }
   }

   public static final String[] DEFAULT_RESOLUTIONS = new String[] { "hourly", "daily", "weekly", "monthly", "yearly" };

   private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalAgg.class);
   protected final String[] resolutions;
   protected final Map<String, Interpolators> interpolators;
   protected final Map<String, Scripts> scripts;
   protected final List<AggregationListener> listeners;
   protected Date lastRun;
   protected int cutoffHours;

   public HierarchicalAgg()
   {
      this(DEFAULT_RESOLUTIONS, 0);
   }

   public HierarchicalAgg(String[] resolutions, int cutoffHours)
   {
      this.resolutions = resolutions;
      this.interpolators = new HashMap<String, Interpolators>();
      this.scripts = new HashMap<String, Scripts>();
      this.listeners = new ArrayList<AggregationListener>();
      this.lastRun = new Date(0);
      this.cutoffHours = cutoffHours;
   }

   @Override
   public void addListener(AggregationListener listener)
   {
      this.listeners.add(listener);
   }

   @Override
   public List<MapReduceOutput> aggregate()
   {
      List<MapReduceOutput> response = new ArrayList<MapReduceOutput>();

      try {
         List<Future<MapReduceOutput>> tasks = new ArrayList<Future<MapReduceOutput>>(resolutions.length);

         Date cutoff = getCutoff();

         beforeAggregation();

         for (String resolution : resolutions) {
            beforeAggregation(resolution);
            Future<MapReduceOutput> t = hierarchicalAggregate(getInput(), resolution, cutoff);
            tasks.add(t);
            afterAggregation(resolution);
         }

         lastRun = cutoff;

         afterAggregation();

         // wait for results
         for (Future<MapReduceOutput> task : tasks) {
            response.add(task.get());
         }
      } catch (Exception e) {
         LOGGER.error(e.getMessage(), e);
         throw new RuntimeException(e);
      }

      return response;
   }

   @Override
   public String[] getResolutions()
   {
      return resolutions;
   }

   @Override
   public boolean removeListener(AggregationListener listener)
   {
      return this.listeners.remove(listener);
   }

   protected Date getCutoff()
   {
      Calendar cal = Calendar.getInstance(TimeUnit.UTC);
      cal.add(Calendar.HOUR, cutoffHours);
      Date cutoff = cal.getTime();
      return cutoff;
   }

   abstract protected DBCollection getInput();

   abstract protected String getOut(String resolution);

   protected OutputType getOutputType()
   {
      return OutputType.MERGE;
   }

   protected String getPathName()
   {
      throw new UnsupportedOperationException("Implement this method");
   }

   protected Future<MapReduceOutput> hierarchicalAggregate(DBCollection in, String resolution, Date cutoff)
   {

      CommandBuilder builder = CommandBuilder.startCommand(in, getOut(resolution), getOutputType());

      Scripts scp = scripts.get(resolution);

      if (scp == null) {

         builder.interpolators(interpolators.get(resolution));
         builder.forPathName(getPathName());

      } else {

         builder.mapScript(scp.map).reduceScript(scp.reduce);
         if (scp.hasFinalize()) {
            builder.finalizeScript(scp.finalize);
         }

      }

      builder.query(QueryBuilder.start(timeQueryProperty()).lessThan(cutoff).greaterThan(lastRun).get());

      onBuilder(builder);

      return submit(builder.buildCallable(in));

   }

   protected void loadScripts(String pathName) throws IOException
   {
      File[] files = ScriptLoader.loadScripts(pathName);

      String mapScript = Files.toString(files[0], Charset.defaultCharset());
      String reduceScript = Files.toString(files[1], Charset.defaultCharset());
      File finalFile = files[2];
      String finalizeScript = finalFile == null ? null : Files.toString(finalFile, Charset.defaultCharset());

      for (String resolution : resolutions) {
         Scripts nsc = new Scripts();
         Interpolators rints = interpolators.get(resolution);

         nsc.map = mapScript;
         nsc.reduce = reduceScript;
         nsc.finalize = finalizeScript;

         if (rints == null) {
            this.scripts.put(resolution, nsc);
            continue;
         }

         if (rints.mapInterpolator != null) {
            nsc.map = rints.mapInterpolator.interpolate(mapScript);
         }
         if (rints.reduceInterpolator != null) {
            nsc.reduce = rints.reduceInterpolator.interpolate(reduceScript);
         }
         if (rints.finalizeInterpolator != null) {
            nsc.finalize = rints.finalizeInterpolator.interpolate(finalizeScript);
         }

         this.scripts.put(resolution, nsc);
      }
   }

   protected void onBuilder(CommandBuilder builder)
   {
      // empty callback
   }

   protected void setInterpolators(String resolution, ScriptInterpolator map)
   {
      setInterpolators(resolution, map, null, null);
   }

   protected void setInterpolators(String resolution, ScriptInterpolator map, ScriptInterpolator reduce)
   {
      setInterpolators(resolution, map, reduce, null);
   }

   protected void setInterpolators(String resolution, ScriptInterpolator map, ScriptInterpolator reduce,
         ScriptInterpolator finalize)
   {
      Interpolators interpols = new Interpolators();
      interpols.mapInterpolator = map;
      interpols.reduceInterpolator = reduce;
      interpols.finalizeInterpolator = finalize;
      this.interpolators.put(resolution, interpols);
   }

   protected void setScripts(String resolution, String map)
   {
      setScripts(resolution, map, null, null);
   }

   protected void setScripts(String resolution, String map, String reduce)
   {
      setScripts(resolution, map, reduce, null);
   }

   protected void setScripts(String resolution, String map, String reduce, String finalize)
   {
      Scripts scripts = new Scripts();
      scripts.map = map;
      scripts.reduce = reduce;
      scripts.finalize = finalize;
      this.scripts.put(resolution, scripts);
   }

   abstract protected Future<MapReduceOutput> submit(Callable<MapReduceOutput> buildCallable);

   protected String timeQueryProperty()
   {
      return "ts";
   }

   private void afterAggregation()
   {
      for (AggregationListener listener : listeners) {
         listener.afterAggregation();
      }
   }

   private void afterAggregation(String resolution)
   {
      for (AggregationListener listener : listeners) {
         listener.afterAggregation(resolution);
      }
   }

   private void beforeAggregation()
   {
      for (AggregationListener listener : listeners) {
         listener.beforeAggregation();
      }
   }

   private void beforeAggregation(String resolution)
   {
      for (AggregationListener listener : listeners) {
         listener.beforeAggregation(resolution);
      }
   }

}
