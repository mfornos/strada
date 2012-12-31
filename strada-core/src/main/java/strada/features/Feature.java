package strada.features;

import strada.points.DataPoint;

import com.mongodb.BasicDBObject;

/**
 * Contract for a feature that defines an aspect of a {@link DataPoint}.
 * 
 */
public interface Feature
{

   /**
    * Specifies the update operation to be performed.
    * 
    */
   public enum UpdateOp {

      INC {
         @Override
         public String op()
         {
            return "$inc";
         }
      },
      SET {
         @Override
         public String op()
         {
            return "$set";
         }
      },
      ADD_TO_SET {
         @Override
         public String op()
         {
            return "$addToSet";
         }
      };

      public abstract String op();
   }

   /**
    * Gets the type of the Mongo update operation.
    * 
    * @return the type of the update operation
    */
   UpdateOp getUpdateOp();

   /**
    * Gets the feature name.
    * 
    * @return the feature name
    */
   String getName();

   /**
    * Answers true if the feature has no parent.
    * 
    * @return true if the feature has no parent
    */
   boolean isRoot();

   /**
    * Appends the feature to the update operation.
    * 
    * @param obj
    *           Update operation holder
    * @param dataPoint
    */
   void appendTo(BasicDBObject obj, DataPoint dataPoint);

   /**
    * Answers true if the feature has no children.
    * 
    * @return true if the feature has no children
    */
   boolean isLeaf();

   /**
    * Adds a child feature by name.
    * 
    * @param childName
    *           Name of the feature to be created
    * @param params
    *           Constructor parameters
    * @return this feature instance
    */
   Feature add(String childName, Object... params);

   /**
    * Adds a child feature.
    * 
    * @param child
    *           The feature to be added
    * @return this feature instance
    */
   Feature add(Feature child);

   /**
    * Sets the parent feature.
    * 
    * @param parent
    *           The parent feature
    * @return this feature instance
    */
   Feature setParent(Feature parent);

}
