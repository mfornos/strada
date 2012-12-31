package strada.points;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import strada.data.Query;
import strada.data.TimeUnit;
import strada.features.Feature;
import strada.features.Feature.UpdateOp;
import strada.summarizers.Summarizer;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * A data point that can be aggregated in Mongo database.
 * 
 */
public class DataPoint
{
   private final Map<UpdateOp, Set<Feature>> features;

   private final Set<Summarizer<?>> summarizers;

   private final Object id;

   private Date timestamp;

   private DBCollection coll;

   private TimeUnit unit;

   public DataPoint(Object id)
   {
      this(id, null, null, null);
   }

   public DataPoint(Object id, Date timestamp, TimeUnit unit, DBCollection coll)
   {
      Preconditions.checkNotNull(id, "Please, specify a identifier.");

      this.id = id;
      this.timestamp = timestamp;
      this.unit = unit;
      this.coll = coll;
      this.features = new HashMap<UpdateOp, Set<Feature>>();
      this.summarizers = new HashSet<Summarizer<?>>();
   }

   public DataPoint add(Feature... featsToAdd)
   {
      for (Feature feature : featsToAdd) {
         addFeature(feature);
      }
      return this;
   }

   public DataPoint add(Summarizer<?>... summs)
   {
      for (Summarizer<?> summarizer : summs) {
         summarizers.add(summarizer);
      }
      return this;
   }

   public BasicDBObject buildUpdateCommand()
   {
      BasicDBObject cmd = new BasicDBObject();
      for (UpdateOp op : UpdateOp.values()) {
         appendUpsert(cmd, op);
      }
      return cmd;
   }

   public DBObject byId()
   {
      return byId(getTimeUnit());
   }

   public DBObject byId(TimeUnit unit)
   {
      return Query.byId(id, getTimestamp(), unit);
   }

   public boolean exists()
   {
      Preconditions.checkNotNull(coll, "Please, especify a collection using withCollection.");

      return exists(coll, getTimeUnit());
   }

   public boolean exists(DBCollection coll, TimeUnit unit)
   {
      return coll.count(byId(unit)) > 0;
   }

   public DBObject findOne()
   {
      Preconditions.checkNotNull(coll, "Please, especify a collection using withCollection.");

      return findOne(coll, getTimeUnit());
   }

   public DBObject findOne(DBCollection coll, TimeUnit day)
   {
      return coll.findOne(byId(TimeUnit.DAY));
   }

   public DBCollection getDBCollection()
   {
      return coll;
   }

   public Map<Feature.UpdateOp, Set<Feature>> getFeatures()
   {
      return Collections.unmodifiableMap(features);
   }

   public Object getId()
   {
      return id;
   }

   public Date getTimestamp()
   {
      if (timestamp == null) {
         timestamp = new Date();
      }
      return timestamp;
   }

   public TimeUnit getTimeUnit()
   {
      if (unit == null) {
         // max resolution by default
         unit = TimeUnit.HOUR;
      }
      return unit;
   }

   public Feature lookup(UpdateOp type, final String name)
   {
      Set<Feature> feats = features.get(type);
      if (!(feats == null || feats.isEmpty())) {
         return Iterables.find(feats, new Predicate<Feature>()
         {
            @Override
            public boolean apply(Feature f)
            {
               return f.getName().equalsIgnoreCase(name);
            }
         });
      }
      return null;
   }

   public void summarize()
   {
      for (Summarizer<?> sum : summarizers) {
         sum.summarize(this);
      }
   }

   @Override
   public String toString()
   {
      return Objects.toStringHelper(this.getClass()).add("id", id).add("ts", timestamp).add("unit", unit).add("dbcollection", coll).add("features", features).add("summarizers", summarizers).toString();
   }

   public WriteResult upsert()
   {
      Preconditions.checkNotNull(coll, "Please, especify a collection using withCollection.");

      return upsert(coll, getTimeUnit());
   }

   public WriteResult upsert(DBCollection coll, TimeUnit unit)
   {
      BasicDBObject cmd = buildUpdateCommand();
      return cmd.isEmpty() ? null : coll.update(byId(unit), cmd, true, false);
   }

   public DataPoint withCollection(DBCollection coll)
   {
      this.coll = coll;
      return this;
   }

   public DataPoint withTimestamp(Date timestamp)
   {
      this.timestamp = timestamp;
      return this;
   }

   public DataPoint withTimeUnit(TimeUnit unit)
   {
      this.unit = unit;
      return this;
   }

   protected void addFeature(Feature feature)
   {
      Set<Feature> feats = features.get(feature.getUpdateOp());
      if (feats == null) {
         feats = new HashSet<Feature>();
         features.put(feature.getUpdateOp(), feats);
      }
      feats.add(feature);
   }

   protected void appendUpsert(BasicDBObject cmd, UpdateOp op)
   {
      if (features.containsKey(op)) {
         BasicDBObject obj = forOp(op, new BasicDBObject());
         cmd.append(op.op(), obj);
      }
   }

   protected BasicDBObject forOp(UpdateOp op, BasicDBObject obj)
   {
      for (Feature feat : features.get(op)) {
         feat.appendTo(obj, this);
      }
      return obj;
   }

}
