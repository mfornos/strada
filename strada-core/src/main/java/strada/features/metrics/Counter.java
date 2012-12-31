package strada.features.metrics;

import strada.features.BasicFeature;
import strada.features.Feature;

import com.google.common.base.Objects;

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
   public String toString()
   {
      return Objects.toStringHelper(this.getClass()).add("inc", inc).toString();
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      return params.length > 0 ? new Counter(name, (Number) params[0]) : new Counter(name);
   }

}
