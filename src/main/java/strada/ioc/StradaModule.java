package strada.ioc;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import strada.config.StradaConfig;
import strada.mapreduce.DefaultMapReduceService;
import strada.mapreduce.MapReduceService;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class StradaModule extends AbstractModule
{

   @Provides
   @Singleton
   MongoClient provideMongoClient() throws UnknownHostException
   {
      StradaConfig config = new StradaConfig();
      MongoClientURI uri = config.clientURI();

      return uri == null ? new MongoClient() : new MongoClient(uri);
   }

   protected void bindExecutor()
   {
      bind(ExecutorService.class).annotatedWith(Names.named("MapReduce")).toInstance(Executors.newFixedThreadPool(5));
   }

   @Override
   protected void configure()
   {
      bind(MapReduceService.class).to(DefaultMapReduceService.class);
      bindExecutor();
   }
}
