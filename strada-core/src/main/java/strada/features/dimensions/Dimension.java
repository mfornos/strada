package strada.features.dimensions;

/**
 * Represents a named level of aggregation. It doesn't hold any value.
 * 
 * Example:
 * 
 * <pre>
 * Dimension myDim = new Dimension(&quot;myDim&quot;);
 * myDim.add(new Value(&quot;myValue&quot;, 12));
 * </pre>
 * 
 * Aggregates 'myDim.myValue = 12'
 * 
 */
public class Dimension extends Value
{

   public Dimension(String name)
   {
      super(name, null);
   }

}
