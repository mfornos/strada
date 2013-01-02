package strada.agg;

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
import strada.mapreduce.CommandBuilder;
import strada.mapreduce.ScriptInterpolator;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.QueryBuilder;

public abstract class HierarchicalAgg implements Aggregator
{
   private class Interpolators
   {
      public ScriptInterpolator mapInterpolator;
      public ScriptInterpolator reduceInterpolator;
      public ScriptInterpolator finalizeInterpolator;
   }

   private class Scripts
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
   private final String[] resolutions;
   private final Map<String, Interpolators> interpolators;
   private final Map<String, Scripts> scripts;
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
      this.lastRun = new Date(0);
      this.cutoffHours = cutoffHours;
   }

   @Override
   public List<MapReduceOutput> aggregate()
   {
      List<MapReduceOutput> response = new ArrayList<MapReduceOutput>();

      try {
         List<Future<MapReduceOutput>> tasks = new ArrayList<Future<MapReduceOutput>>(resolutions.length);

         Date cutoff = getCutoff();

         for (String resolution : resolutions) {
            Future<MapReduceOutput> t = hierarchicalAggregate(getInput(), resolution, cutoff);
            tasks.add(t);
         }

         lastRun = cutoff;

         // wait for results
         for (Future<MapReduceOutput> task : tasks) {
            response.add(task.get());
         }
      } catch (Exception e) {
         LOGGER.error(e.getMessage(), e);
      }

      return response;
   }

   protected void addInterpolators(String resolution, ScriptInterpolator map)
   {
      addInterpolators(resolution, map, null, null);
   }

   protected void addInterpolators(String resolution, ScriptInterpolator map, ScriptInterpolator reduce)
   {
      addInterpolators(resolution, map, reduce, null);
   }

   protected void addInterpolators(String resolution, ScriptInterpolator map, ScriptInterpolator reduce,
         ScriptInterpolator finalize)
   {
      Interpolators interpols = new Interpolators();
      interpols.mapInterpolator = map;
      interpols.reduceInterpolator = reduce;
      interpols.finalizeInterpolator = finalize;
      this.interpolators.put(resolution, interpols);
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

   protected Future<MapReduceOutput> hierarchicalAggregate(DBCollection in, String resolution, Date cutoff)
   {
      Scripts scp = scripts.get(resolution);

      Preconditions.checkNotNull(scp, "Please, initialize your map reduce scripts.");

      CommandBuilder builder = CommandBuilder.startCommand(in, getOut(resolution), getOutputType()).mapScript(scp.map).reduceScript(scp.reduce);
      if (scp.hasFinalize()) {
         builder.finalizeScript(scp.finalize);
      }
      builder.query(QueryBuilder.start(timeQueryProperty()).lessThan(cutoff).greaterThan(lastRun).get());
      return submit(builder.buildCallable(in));
   }

   abstract protected void initInterpolators();

   protected void loadScripts(String pathName) throws IOException
   {

      File mapFile = new File(pathName + CommandBuilder.MAP_SCRIPT_EXT);
      File reduceFile = new File(pathName + CommandBuilder.REDUCE_SCRIPT_EXT);
      File finalFile = new File(pathName + CommandBuilder.FINALIZE_SCRIPT_EXT);

      if (!finalFile.exists()) {
         finalFile = null;
      }

      String mapScript = Files.toString(mapFile, Charset.defaultCharset());
      String reduceScript = Files.toString(reduceFile, Charset.defaultCharset());
      String finalizeScript = finalFile == null ? null : Files.toString(finalFile, Charset.defaultCharset());

      initInterpolators();

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

   abstract protected Future<MapReduceOutput> submit(Callable<MapReduceOutput> buildCallable);

   protected String timeQueryProperty()
   {
      return "ts";
   }

}
