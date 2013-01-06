package example.webstats.hits;

import java.util.Date;

public class Hit
{
   public static class Action
   {
      public String country;
      public String name;

      public Action(String country, String name)
      {
         this.country = country;
         this.name = name;
      }
   }

   public String websiteId;
   public Date ts;
   public String ip;
   public Action action;
   public String ua;

   public Hit(String ip, String websiteId, Date ts, Action action, String ua)
   {
      this.websiteId = websiteId;
      this.ts = ts;
      this.ip = ip;
      this.action = action;
      this.ua = ua;
   }

   public boolean hasAction()
   {
      return action != null;
   }
}
