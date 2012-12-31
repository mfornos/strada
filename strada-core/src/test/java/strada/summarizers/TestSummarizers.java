package strada.summarizers;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import strada.features.dimensions.Value;
import strada.points.DataPoint;

import com.mongodb.gridfs.GridFS;

public class TestSummarizers
{

   @Test
   public void bloom()
   {
      GridFS fs = Mockito.mock(GridFS.class);
      BloomSummarizer bloom = new BloomSummarizer(fs, 20, 0.0f, "members", "mid");
      DataPoint point = new DataPoint("blah");
      point.add(new Value("mid", "blah"));
      bloom.summarize(point);
      Assert.assertTrue(bloom.getSummarizer().isPresent("blah"));
      Assert.assertFalse(bloom.getSummarizer().isPresent("bleh"));
   }

   @Test
   public void countMinSketch()
   {
      GridFS fs = Mockito.mock(GridFS.class);
      CountMinSketchSummarizer cms = new CountMinSketchSummarizer(fs, 0.0001, 0.99, "members", "mid");
      DataPoint point = new DataPoint("blah");
      point.add(new Value("mid", "blah"));
      cms.summarize(point);
      Assert.assertEquals(cms.getSummarizer().estimateCount("blah".hashCode()), 1);
      Assert.assertEquals(cms.getSummarizer().estimateCount("bleh".hashCode()), 0);
      cms.summarize(point);
      Assert.assertEquals(cms.getSummarizer().estimateCount("blah".hashCode()), 2);
      Assert.assertEquals(cms.getSummarizer().estimateCount("bleh".hashCode()), 0);
   }

}
