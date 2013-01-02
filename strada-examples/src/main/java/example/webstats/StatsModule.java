package example.webstats;

import strada.ioc.StradaModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import example.webstats.rest.StatsResource;

public class StatsModule extends AbstractModule
{

   @Override
   protected void configure()
   {
      install(new StradaModule());
      bind(String.class).annotatedWith(Names.named("dbName")).toInstance("test");
      bind(WebstatsAggregator.class);
      bind(StatsService.class);
      requestStaticInjection(StatsResource.class);
   }

   @Inject
   @Provides
   @Singleton
   DB providesDB(@Named("dbName") String dbName, MongoClient client)
   {
      DB db = client.getDB(dbName);
      //db.dropDatabase();
      return db;
   }

}
