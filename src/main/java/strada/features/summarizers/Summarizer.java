package strada.features.summarizers;

import strada.features.BasicFeature;
import strada.features.Feature;
import strada.points.DataPoint;

import com.mongodb.BasicDBObject;

public abstract class Summarizer extends BasicFeature
{

   public Summarizer(String name)
   {
      super(name);
   }

   @Override
   public void appendTo(BasicDBObject obj, DataPoint dataPoint)
   {
      throw new UnsupportedOperationException("Summarizer cannot be called for upsert.");
   }

   @Override
   public UpdateType getUpdateType()
   {
      return UpdateType.SUMMARIZE;
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      throw new UnsupportedOperationException();
   }
}
