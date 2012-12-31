package strada.features.dimensions;

import java.util.Collection;

public class Bag extends Value
{

   public Bag(String name, Collection<?> value)
   {
      super(name, value);
   }

   public Bag(String name, Object[] value)
   {
      super(name, value);
   }

   @Override
   public UpdateOp getUpdateOp()
   {
      return UpdateOp.ADD_TO_SET;
   }

}
