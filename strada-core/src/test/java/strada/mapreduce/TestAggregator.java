package strada.mapreduce;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.mongodb.DBCollection;
import com.mongodb.MapReduceOutput;

public class TestAggregator
{
   private class DummyAggregator extends HierarchicalAgg
   {

      private final String path;

      public DummyAggregator(String path)
      {
         this.path = path;
      }

      @Override
      public String getPathName()
      {
         return path;
      }

      @Override
      protected DBCollection getInput()
      {
         return Mockito.mock(DBCollection.class);
      }

      @Override
      protected String getOut(String resolution)
      {
         return "dummy";
      }

      @Override
      @SuppressWarnings("unchecked")
      protected Future<MapReduceOutput> submit(Callable<MapReduceOutput> buildCallable)
      {

         return Mockito.mock(Future.class);

      }

   }

   @Test
   public void basic()
   {
      DummyAggregator agg = new DummyAggregator("src/test/resources/sample");
      AggregationListener listener = Mockito.mock(AggregationListener.class);
      agg.addListener(listener);
      List<MapReduceOutput> result = agg.aggregate();

      Mockito.verify(listener).beforeAggregation();
      Mockito.verify(listener).afterAggregation();

      for (String resolution : agg.getResolutions()) {
         Mockito.verify(listener).beforeAggregation(resolution);
         Mockito.verify(listener).afterAggregation(resolution);
      }

      Assert.assertNotNull(result);
      Assert.assertEquals(result.size(), agg.getResolutions().length);
   }

   @Test(expectedExceptions = java.lang.RuntimeException.class)
   public void noScripts()
   {
      DummyAggregator agg = new DummyAggregator("nowhere/no");
      agg.aggregate();
   }

   @Test
   public void stringScripts()
   {
      DummyAggregator agg = new DummyAggregator(null);
      for (String r : agg.getResolutions()) {
         agg.setScripts(r, "function() {}", "function() {}");
      }
      agg.aggregate();
   }
}
