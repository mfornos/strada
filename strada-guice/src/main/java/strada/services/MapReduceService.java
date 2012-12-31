package strada.services;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.mongodb.MapReduceOutput;

public interface MapReduceService
{
   Future<MapReduceOutput> submit(Callable<MapReduceOutput> call);

   List<Future<MapReduceOutput>> submit(Callable<MapReduceOutput>... tasks) throws InterruptedException;
}
