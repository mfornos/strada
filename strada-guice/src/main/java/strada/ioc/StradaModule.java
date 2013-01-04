package strada.ioc;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import strada.config.StradaConfig;
import strada.services.DefaultMapReduceService;
import strada.services.MapReduceService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
      ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("MapReduce Thread-%s").build();
      bind(ExecutorService.class).annotatedWith(Names.named("MapReduce")).toInstance(Executors.newFixedThreadPool(5, threadFactory));
   }

   @Override
   protected void configure()
   {
      bind(MapReduceService.class).to(DefaultMapReduceService.class);
      bindExecutor();
   }

}
