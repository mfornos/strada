package strada.agg;

import java.util.List;

import com.mongodb.MapReduceOutput;

public interface Aggregator
{
   List<MapReduceOutput> aggregate();
}
