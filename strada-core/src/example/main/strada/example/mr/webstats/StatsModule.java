package strada.example.mr.webstats;

import strada.ioc.StradaModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class StatsModule extends AbstractModule
{

   @Override
   protected void configure()
   {
      install(new StradaModule());
      bind(String.class).annotatedWith(Names.named("dbName")).toInstance("test");
      bind(Stats.class);
   }

   @Inject
   @Provides
   DB providesDB(@Named("dbName") String dbName, MongoClient client)
   {
      DB db = client.getDB(dbName);
      //db.dropDatabase();
      return db;
   }

}
