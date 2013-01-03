package example.webstats;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.MongoClient;

/**
 * 
 * 
 */
public class WebstatsExampleApp
{

   public static void main(String[] args)
   {
      new WebstatsExampleApp();

   }

   @Inject
   private MongoClient client;

   public WebstatsExampleApp()
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
