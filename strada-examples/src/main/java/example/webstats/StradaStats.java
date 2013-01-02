package example.webstats;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.MongoClient;

/**
 * 
 * 
 */
public class StradaStats
{

   public static void main(String[] args)
   {
      new StradaStats();

   }

   @Inject
   private MongoClient client;

   public StradaStats()
   {

      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         public void run()
         {
            client.close();
         }
      });

      Injector injector = Guice.createInjector(new StatsModule());
      injector.injectMembers(this);
   }

}
