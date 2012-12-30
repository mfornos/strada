package strada.features.dimensions;

public class Bag extends Value
{

   public Bag(String name, Object value)
   {
      super(name, value);
   }

   @Override
   public UpdateType getUpdateType()
   {
      return UpdateType.ADD_TO_SET;
   }

}
