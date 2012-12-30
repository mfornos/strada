package strada.features.dimensions;

import java.util.Date;

import strada.features.Appender;
import strada.features.Feature;

public class DateTime extends Appender
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
   public UpdateType getUpdateType()
   {
      return UpdateType.SET;
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      return new DateTime(name);
   }

   @Override
   public Object getValue()
   {
      return date;
   }

}
