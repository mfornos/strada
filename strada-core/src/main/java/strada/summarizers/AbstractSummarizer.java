package strada.summarizers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import strada.features.Feature.UpdateOp;
import strada.features.dimensions.Value;
import strada.points.DataPoint;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public abstract class AbstractSummarizer<T> implements Summarizer<T>
{
   public AbstractSummarizer(GridFS gridFs, String name, String property)
   {
      this.gridFs = gridFs;
      this.name = name;
      this.property = property;
   }

   @Override
   public String getName()
   {
      return name;
   }

   protected final String name;
   protected GridFS gridFs;
   protected String property;
   protected T summarizer;

   protected String getMemberId(DataPoint point)
   {
      // TODO generalize
      return property == null ? point.getId().toString()
            : ((Value) point.lookup(UpdateOp.SET, property)).getValue().toString();
   }

   protected String formatFileName()
   {
      return String.format("%s_%s.bin", getClass().getSimpleName(), name);
   }

   protected T deserializeSummarizer(GridFSDBFile filterFile)
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
         return onByteArray(byteArray);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return null;
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

   public T getSummarizer()
   {
      return summarizer;
   }

   @Override
   public void summarize(DataPoint point)
   {
      if (summarizer == null) {
         summarizer = loadSummarizer();
      }

      onDataPoint(point);
   }

   abstract protected void onDataPoint(DataPoint point);

   protected T loadSummarizer()
   {
      T summarizer = null;

      GridFSDBFile filterFile = gridFs.findOne(formatFileName());
      if (filterFile != null) {
         summarizer = deserializeSummarizer(filterFile);
      }

      return summarizer == null ? createSummarizer() : summarizer;
   }

   abstract protected T createSummarizer();

   protected void save(byte[] ser)
   {
      GridFSInputFile file = gridFs.createFile(ser);
      file.setFilename(formatFileName());
      file.save();
   }

   @Override
   public void reset()
   {
      summarizer = null;
   }

   abstract protected T onByteArray(byte[] byteArray);
}
