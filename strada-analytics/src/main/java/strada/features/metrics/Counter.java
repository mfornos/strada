package strada.features.metrics;

import strada.features.Appender;
import strada.features.Feature;

public class Counter extends Appender
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
   protected Feature createChildByName(String name, Object... params)
   {
      return new Counter(name);
   }

   @Override
   public Object getValue()
   {
      return inc;
   }

}
