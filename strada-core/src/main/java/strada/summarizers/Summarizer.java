package strada.summarizers;

import strada.points.DataPoint;

public interface Summarizer<T>
{
   void summarize(DataPoint point);

   void serialize();

   T getSummarizer();
   
   String getName();
}
