package strada.features;

import strada.points.DataPoint;

import com.mongodb.BasicDBObject;

public abstract class Appender extends BasicFeature
{
   public Appender(String name)
   {
      super(name);
   }

   @Override
   public void appendTo(BasicDBObject obj, DataPoint point)
   {
      if (isLeaf()) {
         obj.append(getName(), getValue());
      } else {
         for (Feature dim : children) {
            dim.appendTo(obj, point);
         }
      }
   }

   @Override
   public void summarize(DataPoint dataPoint)
   {
      //
   }

   /**
    * This method will be called on the process of appending feature values to
    * the Mongo update operation.
    * 
    * @return the value to be appended in the Mongo update operation
    */
   abstract public Object getValue();
}
