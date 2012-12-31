package strada.features;

import java.util.HashSet;
import java.util.Set;

import strada.points.DataPoint;

import com.mongodb.BasicDBObject;

/**
 * Base class for {@link Feature} implementations.
 * 
 */
public abstract class BasicFeature implements Feature
{

   private final String name;

   protected final Set<Feature> children;

   protected Feature parent;

   public BasicFeature(String name)
   {
      this.name = name;
      this.children = new HashSet<Feature>();
   }

   @Override
   public Feature add(Feature child)
   {
      children.add(child.setParent(this));
      return this;
   }

   @Override
   public Feature add(String name, Object... params)
   {
      add(createChildByName(name, params));
      return this;
   }

   @Override
   public String getName()
   {
      return isRoot() ? name : String.format("%s.%s", parent.getName(), name);
   }

   @Override
   public boolean isLeaf()
   {
      return children.isEmpty();
   }

   @Override
   public boolean isRoot()
   {
      return parent == null;
   }

   @Override
   public Feature setParent(Feature parent)
   {
      this.parent = parent;
      return this;
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

   /**
    * This method will be called on the process of appending feature values to
    * the Mongo update operation.
    * 
    * @return the value to be appended in the Mongo update operation
    */
   abstract public Object getValue();

   /**
    * Creates this Feature by name.
    * 
    * @param name
    *           The feature name
    * @param params
    *           List of construction parameters
    * @return a new Feature instance
    */
   abstract protected Feature createChildByName(String name, Object... params);

}
