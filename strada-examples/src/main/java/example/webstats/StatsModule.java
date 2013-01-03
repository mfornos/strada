package example.webstats;

import strada.ioc.StradaModule;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

   @Inject
   @Provides
   @Singleton
   DB providesDB(@Named("dbName") String dbName, MongoClient client)
   {
      DB db = client.getDB(dbName);
      // db.dropDatabase();
      return db;
   }

   @Provides
   @Singleton
   ObjectMapper providesObjectMapper()
   {
      ObjectMapper om = new ObjectMapper();
      om.disable(SerializationFeature.WRAP_ROOT_VALUE);
      om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      om.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      om.setSerializationInclusion(Include.NON_NULL);
      return om;
   }

   @Override
   protected void configure()
   {
      install(new StradaModule());
      bind(String.class).annotatedWith(Names.named("dbName")).toInstance("test");
      bind(WebstatsAggregator.class);
      bind(StatsService.class);
      requestStaticInjection(StatsResource.class);
   }

}
