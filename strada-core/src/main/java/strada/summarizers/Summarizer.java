package strada.summarizers;

import strada.points.DataPoint;

public interface Summarizer<T>
{
   /**
    * Summarizes a data point.
    * 
    * @param point
    *           The data point to be summarized
    */
   void summarize(DataPoint point);

   /**
    * Serializes the summarizer data.
    */
   void serialize();

   /**
    * @return the underlying summarizer implementation
    */
   T getSummarizer();

   /**
    * @return the summarizer name
    */
   String getName();

   /**
    * Reverts to the initial state.
    */
   void reset();
}
