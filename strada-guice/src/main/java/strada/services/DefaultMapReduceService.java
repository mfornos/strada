package strada.services;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.MapReduceOutput;

public class DefaultMapReduceService implements MapReduceService
{
   private final ExecutorService executorService;

   @Inject
   public DefaultMapReduceService(@Named("MapReduce") ExecutorService executorService)
   {
      this.executorService = executorService;
   }

   @Override
   public Future<MapReduceOutput> submit(Callable<MapReduceOutput> call)
   {
      return executorService.submit(call);
   }

   @Override
   public List<Future<MapReduceOutput>> submit(Callable<MapReduceOutput>... tasks) throws InterruptedException
   {
      return executorService.invokeAll(Arrays.asList(tasks));
   }

}
