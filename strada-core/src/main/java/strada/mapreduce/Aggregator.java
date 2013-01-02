package strada.mapreduce;

import java.util.List;

import com.mongodb.MapReduceOutput;

public interface Aggregator
{
   List<MapReduceOutput> aggregate();
}
