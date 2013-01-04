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
   public int ip;
   public Action[] actions;
   public String[] ua;

   public Hit(int ip, String websiteId, Date ts, Action[] actions, String[] ua)
   {
      this.websiteId = websiteId;
      this.ts = ts;
      this.ip = ip;
      this.actions = actions;
      this.ua = ua;
   }

   public boolean hasActions()
   {
      return actions != null && actions.length > 0;
   }
}
