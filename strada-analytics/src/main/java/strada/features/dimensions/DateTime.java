package strada.features.dimensions;

import java.util.Date;

import strada.features.BasicFeature;
import strada.features.Feature;

public class DateTime extends BasicFeature
{

   private final Date date;

   public DateTime(String name)
   {
      this(name, new Date());
   }

   public DateTime(String name, Date date)
   {
      super(name);
      this.date = date;
   }

   @Override
   public UpdateOp getUpdateOp()
   {
      return UpdateOp.SET;
   }

   @Override
   public Object getValue()
   {
      return date;
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      return new DateTime(name);
   }

}
