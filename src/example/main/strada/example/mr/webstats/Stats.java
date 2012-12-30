package strada.example.mr.webstats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import strada.data.TimeUnit;
import strada.features.summarizers.Bloom;
import strada.mapreduce.CommandBuilder;
import strada.mapreduce.MapReduceService;
import strada.mapreduce.ScriptInterpolator;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.QueryBuilder;

public class Stats
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Stats.class);

   private final PointWriter proc;

   private final String[] aggs = new String[] { "daily", "weekly", "monthly", "yearly" };

   private final MapReduceService mrs;

   private final Map<String, ScriptInterpolator> interpols;

   private final Bloom members;

   private Date lastRun;

   @Inject
   public Stats(DB db, MapReduceService mrs)
   {
      this.members = new Bloom(db, "members", "ip");
      this.proc = new PointWriter(db, members);
      this.mrs = mrs;
      this.interpols = new HashMap<String, ScriptInterpolator>();
      this.lastRun = new Date(0);
      initInterpolators();

   }

   public void aggregate()
   {
      DBCursor cursor = proc.getDataSource().find();
      for (DBObject o : cursor) {
         System.out.println(o);
      }

      try {
         List<Future<MapReduceOutput>> tasks = new ArrayList<Future<MapReduceOutput>>(aggs.length);

         Calendar cal = Calendar.getInstance(TimeUnit.UTC);
         // cal.add(Calendar.HOUR, 1);
         Date cutoff = cal.getTime();// new Date(now.getTime() - (60 * 1000));

         for (String agg : aggs) {
            Future<MapReduceOutput> t = hierarchicalAggregate(cutoff, agg);
            tasks.add(t);
         }

         lastRun = cutoff;

         // wait for results
         for (Future<MapReduceOutput> task : tasks) {
            LOGGER.info("Got {}", task.get());
         }
      } catch (Exception e) {
         LOGGER.error(e.getMessage(), e);
      }
   }

   public boolean isMember(String mid)
   {
      return members.getFilter().isPresent(mid);
   }

   public void onHit(final Hit hit)
   {

      proc.write(hit);

   }

   public void serializeMembers()
   {
      members.serialize();
   }

   protected Future<MapReduceOutput> hierarchicalAggregate(Date cutoff, String agg)
   {

      DBCollection c = proc.getDataSource();
      String out = agg + "_stats";

      CommandBuilder builder = CommandBuilder.startCommand(c, out, OutputType.MERGE).forPathName("src/example/resources/mr/sample");
      builder.mapInterpolator(interpols.get(agg));
      builder.query(QueryBuilder.start("ts").lessThan(cutoff).greaterThan(lastRun).get());

      return mrs.submit(builder.buildCallable(c));

   }

   private void initInterpolators()
   {
      // TODO cache interpolated scripts?
      ScriptInterpolator sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),0, 0, 0, 0)");
      interpols.put("daily", sc);
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.valueOf() - this._id.d.getDay()*86400000)");
      interpols.put("weekly", sc);
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(), this._id.d.getMonth(), 1, 0, 0, 0, 0)");
      interpols.put("monthly", sc);
      sc = new ScriptInterpolator();
      sc.addVar("date", "new Date(this._id.d.getFullYear(),1, 1, 0, 0, 0, 0)");
      interpols.put("yearly", sc);
   }

}
