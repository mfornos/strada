package strada.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import strada.mapreduce.HierarchicalAgg.Interpolators;

import com.google.common.io.Files;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;

/**
 * Builder for Mongo map-reduce commands.
 * 
 */
public class CommandBuilder
{

   private static final Logger LOGGER = LoggerFactory.getLogger(CommandBuilder.class);

   public static CommandBuilder startCommand(DBCollection collection)
   {
      return new CommandBuilder(collection);
   }

   public static CommandBuilder startCommand(DBCollection collection, String out)
   {
      return new CommandBuilder(collection, out);
   }

   public static CommandBuilder startCommand(DBCollection collection, String out, OutputType type)
   {
      return new CommandBuilder(collection, out, type);
   }

   public static CommandBuilder startCommand(DBCollection collection, String outName, OutputType type, DBObject query)
   {
      return new CommandBuilder(collection, outName, type, query);
   }

   private final DBCollection collection;
   private String pathName;
   private File m, r, f;
   private String ms, rs, fs;
   private String out;
   private OutputType outputType;
   private DBObject query;
   private ScriptInterpolator mi, ri, fi;

   private CommandBuilder(DBCollection collection)
   {
      this(collection, null, OutputType.INLINE, null);
   }

   private CommandBuilder(DBCollection collection, String out)
   {
      this(collection, out, OutputType.REDUCE, null);
   }

   private CommandBuilder(DBCollection collection, String out, OutputType type)
   {
      this(collection, out, type, null);
   }

   private CommandBuilder(DBCollection collection, String outName, OutputType type, DBObject query)
   {
      this.collection = collection;
      this.out = outName;
      this.outputType = type;
      this.query = query;
   }

   public MapReduceCommand build()
   {
      if (emptyScripts()) {
         try {
            initScripts();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      interpolate();

      MapReduceCommand command = new MapReduceCommand(collection, ms, rs, out, outputType, query);
      if (fs != null) {
         command.setFinalize(fs);
      }
      return command;
   }

   public Callable<MapReduceOutput> buildCallable()
   {
      return buildCallable(collection);
   }

   public Callable<MapReduceOutput> buildCallable(final DBCollection collection)
   {
      final MapReduceCommand command = build();

      return new Callable<MapReduceOutput>()
      {
         @Override
         public MapReduceOutput call() throws Exception
         {
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("Executing map reduce command {}", command);
            }
            return collection.mapReduce(command);
         }
      };
   }

   public CommandBuilder finalizeFile(File finalize)
   {
      this.f = finalize;
      return this;
   }

   public CommandBuilder finalizeFile(String finalize)
   {
      return finalizeFile(new File(finalize));
   }

   public CommandBuilder finalizeInterpolator(ScriptInterpolator sc)
   {
      this.fi = sc;
      return this;
   }

   public CommandBuilder finalizeScript(String script)
   {
      this.fs = script;
      return this;
   }

   public CommandBuilder forFiles(File map, File reduce)
   {
      this.m = map;
      this.r = reduce;
      return this;
   }

   public CommandBuilder forFiles(String mapName, String reduceName)
   {
      return forFiles(new File(mapName), new File(reduceName));
   }

   public CommandBuilder forPathName(String pathName)
   {
      this.pathName = pathName;
      return this;
   }

   public CommandBuilder mapInterpolator(ScriptInterpolator sc)
   {
      this.mi = sc;
      return this;
   }

   public CommandBuilder mapScript(String script)
   {
      this.ms = script;
      return this;
   }

   public CommandBuilder outCollectionName(String out)
   {
      this.out = out;
      return this;
   }

   public CommandBuilder outputType(OutputType type)
   {
      this.outputType = type;
      return this;
   }

   public CommandBuilder query(DBObject query)
   {
      this.query = query;
      return this;
   }

   public CommandBuilder reduceInterpolator(ScriptInterpolator sc)
   {
      this.ri = sc;
      return this;
   }

   public CommandBuilder reduceScript(String script)
   {
      this.rs = script;
      return this;
   }

   protected void initScripts() throws IOException
   {
      if (pathName != null) {
         setScriptsFromPathName();
      }
      ms = Files.toString(m, Charset.defaultCharset());
      rs = Files.toString(r, Charset.defaultCharset());
      fs = f == null ? null : Files.toString(f, Charset.defaultCharset());
   }

   protected void interpolators(Interpolators interpols)
   {
      if (interpols == null)
         return;

      mapInterpolator(interpols.mapInterpolator);
      reduceInterpolator(interpols.reduceInterpolator);
      finalizeInterpolator(interpols.finalizeInterpolator);
   }

   private boolean emptyScripts()
   {
      return (ms == null && rs == null);
   }

   private void interpolate()
   {
      if (mi != null) {
         ms = mi.interpolate(ms);
      }
      if (ri != null) {
         rs = ri.interpolate(rs);
      }
      if (fi != null) {
         fs = fi.interpolate(fs);
      }
   }

   private void setScriptsFromPathName()
   {
      File[] files = ScriptLoader.loadScripts(pathName);
      this.m = files[0];
      this.r = files[1];
      this.f = files[2];
   }

}
