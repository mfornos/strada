package strada.features.dimensions;

import strada.features.Appender;
import strada.features.Feature;

import com.google.common.base.Preconditions;

public class Value extends Appender
{

   private final Object value;

   public Value(String name, Object value)
   {
      super(name);
      this.value = value;
   }

   @Override
   public UpdateType getUpdateType()
   {
      return UpdateType.SET;
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      Preconditions.checkArgument(params.length > 0, "Please, specify a value parameter.");

      return new Value(name, params[0]);
   }

   @Override
   public Object getValue()
   {
      return value;
   }

}
