package strada.features.summarizers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import strada.features.dimensions.Value;
import strada.points.DataPoint;

import com.clearspring.analytics.stream.membership.BloomFilter;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

// TODO guava cache?
public class Bloom extends Summarizer
{
   private static final int DEFAULT_SIZE = 1000000;
   private static final float DEFAULT_MAX_FALSE_POS = 0.01f;

   private BloomFilter filter;
   private GridFS bloomFs;
   private String property;
   private int size;
   private float maxFalsePos;

   public Bloom(DB db, int size, float maxFalsePos, String name, String property)
   {
      super(name);
      this.bloomFs = new GridFS(db, name);
      this.property = property;
      this.size = size;
      this.maxFalsePos = maxFalsePos;
   }

   public Bloom(DB db, String name)
   {
      this(db, DEFAULT_SIZE, DEFAULT_MAX_FALSE_POS, name, null);
   }

   public Bloom(DB db, String name, String property)
   {
      this(db, DEFAULT_SIZE, DEFAULT_MAX_FALSE_POS, name, property);
   }

   public BloomFilter getFilter()
   {
      return filter;
   }

   public void serialize()
   {
      byte[] ser = BloomFilter.serialize(filter);
      GridFSInputFile file = bloomFs.createFile(ser);
      file.setFilename(formatFileName());
      file.save();
   }

   @Override
   public void summarize(DataPoint point)
   {
      if (filter == null) {
         GridFSDBFile filterFile = bloomFs.findOne(formatFileName());
         if (filterFile != null && filter == null) {
            deserializeFilter(filterFile);
         }

         if (filter == null) {
            filter = new BloomFilter(size, maxFalsePos);
         }
      }

      filter.add(getMemberId(point));

   }

   protected void deserializeFilter(GridFSDBFile filterFile)
   {
      InputStream is = null;
      try {
         is = filterFile.getInputStream();
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();

         int nRead;
         byte[] data = new byte[16384];

         while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
         }

         buffer.flush();

         byte[] byteArray = buffer.toByteArray();
         filter = BloomFilter.deserialize(byteArray);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } finally {
         try {
            if (is != null)
               is.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   protected String formatFileName()
   {
      return String.format("bloom_%s.bin", getName());
   }

   protected String getMemberId(DataPoint point)
   {
      // TODO generalize
      return property == null ? point.getId().toString()
            : ((Value) point.lookup(UpdateType.SET, property)).getValue().toString();
   }

}
