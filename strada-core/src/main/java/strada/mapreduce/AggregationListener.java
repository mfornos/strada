package strada.mapreduce;

public interface AggregationListener
{
   void beforeAggregation();

   void afterAggregation();

   void beforeAggregation(String resolution);

   void afterAggregation(String resolution);
}
