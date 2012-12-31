package example.webstats;

import java.util.Date;

public class Hit
{
   public Hit(int ip, String websiteId, Date ts, String[] actions, String os)
   {
      this.websiteId = websiteId;
      this.ts = ts;
      this.ip = ip;
      this.actions = actions;
      this.os = os;
   }

   public String websiteId;
   public Date ts;
   public int ip;
   public String[] actions;
   public String os;
}
