package strada.summarizers;

import strada.points.DataPoint;

import com.clearspring.analytics.stream.frequency.CountMinSketch;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;

public class CountMinSketchSummarizer extends AbstractSummarizer<CountMinSketch>
{
   private static final double EPS_DEFAULT = 0.0001;
   private static final double CONFIDENCE_DEFAULT = 0.99;

   protected double epsOfTotalCount;
   protected double confidence;
   protected int seed = 7364181;

   public CountMinSketchSummarizer(DB db, double epsOfTotalCount, double confidence, String name, String property)
   {
      this(new GridFS(db, name), epsOfTotalCount, confidence, name, property);
   }

   public CountMinSketchSummarizer(DB db, String name)
   {
      this(db, EPS_DEFAULT, CONFIDENCE_DEFAULT, name, null);
   }

   public CountMinSketchSummarizer(DB db, String name, String property)
   {
      this(db, EPS_DEFAULT, CONFIDENCE_DEFAULT, name, property);
   }

   public CountMinSketchSummarizer(GridFS gridFs, double epsOfTotalCount, double confidence, String name,
         String property)
   {
      super(gridFs, name, property);
      this.epsOfTotalCount = epsOfTotalCount;
      this.confidence = confidence;
   }

   public void serialize()
   {
      save(CountMinSketch.serialize(summarizer));
   }

   protected CountMinSketch onByteArray(byte[] byteArray)
   {
      return CountMinSketch.deserialize(byteArray);
   }

   @Override
   protected CountMinSketch createSummarizer()
   {
      return new CountMinSketch(epsOfTotalCount, confidence, seed);
   }

   @Override
   protected void onDataPoint(DataPoint point)
   {
      summarizer.add(getMemberId(point).hashCode(), 1);
   }

}
