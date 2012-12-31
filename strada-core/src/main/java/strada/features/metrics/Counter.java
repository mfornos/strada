package strada.features.metrics;

import strada.features.BasicFeature;
import strada.features.Feature;

public class Counter extends BasicFeature
{

   private final Number inc;

   public Counter(String name)
   {
      this(name, 1);
   }

   public Counter(String name, Number inc)
   {
      super(name);
      this.inc = inc;
   }

   @Override
   public UpdateOp getUpdateOp()
   {
      return UpdateOp.INC;
   }

   @Override
   public Object getValue()
   {
      return inc;
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      return new Counter(name);
   }

}
