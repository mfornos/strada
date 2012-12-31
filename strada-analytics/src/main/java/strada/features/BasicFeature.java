package strada.features;

import java.util.HashSet;
import java.util.Set;

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
