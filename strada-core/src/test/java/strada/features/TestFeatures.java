package strada.features;

import org.testng.Assert;
import org.testng.annotations.Test;

import strada.features.dimensions.Bag;
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
      counter.appendTo(obj, null);
      Assert.assertEquals(obj.get("hi"), 1);

      counter.add("hi").add("ho", 0.5);

      obj = new BasicDBObject();
      counter.appendTo(obj, null);

      Assert.assertEquals(obj.get("hi.ho"), 0.5);
      Assert.assertEquals(obj.get("hi.hi"), 1);
   }

   @Test
   public void geo()
   {
      GeoPosition loc = new GeoPosition("loc", 0.5, 0.5);

      BasicDBObject obj = new BasicDBObject();
      loc.appendTo(obj, null);
      Assert.assertEquals(obj.get("loc"), new double[] { 0.5, 0.5 });
   }
}
