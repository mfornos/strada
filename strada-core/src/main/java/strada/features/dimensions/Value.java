package strada.features.dimensions;

import strada.features.BasicFeature;
import strada.features.Feature;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Value extends BasicFeature
{

   private final Object value;

   public Value(String name, Object value)
   {
      super(name);
      this.value = value;
   }

   @Override
   public UpdateOp getUpdateOp()
   {
      return UpdateOp.SET;
   }

   @Override
   public Object getValue()
   {
      return value;
   }

   @Override
   public String toString()
   {
      return Objects.toStringHelper(this.getClass()).add("value", value).toString();
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      Preconditions.checkArgument(params.length > 0, "Please, specify a value parameter.");

      return new Value(name, params[0]);
   }

}
