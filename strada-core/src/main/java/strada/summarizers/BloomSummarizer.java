package strada.summarizers;

import strada.points.DataPoint;

import com.clearspring.analytics.stream.membership.BloomFilter;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;

// TODO guava cache?
public class BloomSummarizer extends AbstractSummarizer<BloomFilter>
{
   private static final int DEFAULT_SIZE = 1000000;
   private static final float DEFAULT_MAX_FALSE_POS = 0.01f;

   protected int size;
   protected float maxFalsePos;

   public BloomSummarizer(DB db, int size, float maxFalsePos, String name, String property)
   {
      this(new GridFS(db, name), size, maxFalsePos, name, property);
   }

   public BloomSummarizer(DB db, String name)
   {
      this(db, DEFAULT_SIZE, DEFAULT_MAX_FALSE_POS, name, null);
   }

   public BloomSummarizer(DB db, String name, String property)
   {
      this(db, DEFAULT_SIZE, DEFAULT_MAX_FALSE_POS, name, property);
   }

   public BloomSummarizer(GridFS gridFs, int size, float maxFalsePos, String name, String property)
   {
      super(gridFs, name, property);
      this.size = size;
      this.maxFalsePos = maxFalsePos;
   }

   public void serialize()
   {
      save(BloomFilter.serialize(summarizer));
   }

   protected BloomFilter onByteArray(byte[] byteArray)
   {
      return BloomFilter.deserialize(byteArray);
   }

   @Override
   protected BloomFilter createSummarizer()
   {
      return new BloomFilter(size, maxFalsePos);
   }

   @Override
   protected void onDataPoint(DataPoint point)
   {
      summarizer.add(getMemberId(point));
   }

}
