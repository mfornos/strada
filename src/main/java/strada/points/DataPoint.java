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
import strada.features.Feature.UpdateType;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * A data point that can be <i>upserted</i> in Mongo database.
 * 
 */
public class DataPoint
{
   private final Map<Feature.UpdateType, Set<Feature>> features = new HashMap<Feature.UpdateType, Set<Feature>>();

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
   }

   public DataPoint add(Feature... featsToAdd)
   {
      for (Feature feature : featsToAdd) {
         addFeature(feature);
      }
      return this;
   }

   public BasicDBObject buildUpdateCommand()
   {
      BasicDBObject cmd = new BasicDBObject();
      appendUpsert(cmd, "$inc", Feature.UpdateType.INC);
      appendUpsert(cmd, "$set", Feature.UpdateType.SET);
      appendUpsert(cmd, "$addToSet", Feature.UpdateType.ADD_TO_SET);
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

   public Map<Feature.UpdateType, Set<Feature>> getFeatures()
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

   public Feature lookup(UpdateType type, final String name)
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
      for (Feature feat : features.get(Feature.UpdateType.SUMMARIZE)) {
         feat.summarize(this);
      }
   }

   @Override
   public String toString()
   {
      return "Point id=[" + id + "], unit=[" + getTimeUnit() + "], ts=[" + getTimestamp() + "], increment=["
            + forType(Feature.UpdateType.INC, new BasicDBObject()) + "], upsert=["
            + forType(Feature.UpdateType.SET, new BasicDBObject()) + "], addToSet=["
            + forType(Feature.UpdateType.ADD_TO_SET, new BasicDBObject()) + "]";
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
      Set<Feature> feats = features.get(feature.getUpdateType());
      if (feats == null) {
         feats = new HashSet<Feature>();
         features.put(feature.getUpdateType(), feats);
      }
      feats.add(feature);
   }

   protected void appendUpsert(BasicDBObject cmd, String aggfunc, Feature.UpdateType type)
   {
      if (features.containsKey(type)) {
         BasicDBObject obj = forType(type, new BasicDBObject());
         cmd.append(aggfunc, obj);
      }
   }

   protected BasicDBObject forType(Feature.UpdateType type, BasicDBObject obj)
   {
      for (Feature feat : features.get(type)) {
         feat.appendTo(obj, this);
      }
      return obj;
   }

}
