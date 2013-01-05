package strada.features;

import org.testng.Assert;
import org.testng.annotations.Test;

import strada.features.Feature.UpdateOp;
import strada.features.dimensions.Bag;
import strada.features.dimensions.Dimension;
import strada.features.dimensions.GeoPosition;
import strada.features.dimensions.Value;
import strada.features.metrics.Counter;

import com.mongodb.BasicDBObject;

public class TestFeatures
{
   @Test
   public void bag()
   {
      Bag bag = new Bag("bag", new Integer[] { 1, 2, 3, 4 });
      Assert.assertEquals(bag.getName(), "bag");
      bag.add("one", new int[] { 5, 6, 7 });
      Assert.assertEquals(bag.children.iterator().next().getName(), "bag.one");
      Assert.assertEquals(((Value) bag.children.iterator().next()).getValue(), new int[] { 5, 6, 7 });
   }

   @Test
   public void counter()
   {
      Counter counter = new Counter("hi");

      BasicDBObject obj = new BasicDBObject();
      counter.appendTo(UpdateOp.INC, obj, null);
      Assert.assertEquals(obj.get("hi"), 1);

      counter.add("hi").add("ho", 0.5);

      obj = new BasicDBObject();
      counter.appendTo(UpdateOp.INC, obj, null);

      Assert.assertEquals(obj.get("hi.ho"), 0.5);
      Assert.assertEquals(obj.get("hi.hi"), 1);
   }

   @Test
   public void hierarchy()
   {
      Dimension counters = new Dimension("level0");
      counters.add(new Counter("hi"));
      counters.add(new Counter("ho"));
      counters.add(new Value("country", "en"));
      Value l1 = new Value("level1", "test");
      l1.add(new Counter("l1c"));
      counters.add(l1);
      BasicDBObject obj = new BasicDBObject();
      counters.appendTo(UpdateOp.INC, obj, null);

      Assert.assertEquals(obj.get("level0.hi"), 1);
      Assert.assertEquals(obj.get("level0.ho"), 1);
      Assert.assertEquals(obj.get("level0.level1.l1c"), 1);

      counters.appendTo(UpdateOp.SET, obj, null);
      Assert.assertEquals(obj.get("level0.country"), "en");
      Assert.assertEquals(obj.get("level0.level1"), "test");
   }

   @Test
   public void geo()
   {
      GeoPosition loc = new GeoPosition("loc", 0.5, 0.5);

      BasicDBObject obj = new BasicDBObject();
      loc.appendTo(UpdateOp.SET, obj, null);
      Assert.assertEquals(obj.get("loc"), new double[] { 0.5, 0.5 });
   }
}
