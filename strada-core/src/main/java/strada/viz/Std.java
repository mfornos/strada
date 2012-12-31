package strada.viz;

public class Std
{
   private double sum, max, min, diff;
   private Double last;

   public Std()
   {
      this.sum = 0;
      this.min = Double.POSITIVE_INFINITY;
      this.max = Double.NEGATIVE_INFINITY;
   }

   public void compute(Number number)
   {
      double v = number.doubleValue();
      sum += v;
      if (v < min)
         min = v;
      if (v > max)
         max = v;
      if (last != null)
         diff = v - last;
      last = v;
   }

   public double getDiff()
   {
      return diff;
   }

   public double getMax()
   {
      return max;
   }

   public double getMin()
   {
      return min;
   }

   public double getSum()
   {
      return sum;
   }

   @Override
   public String toString()
   {
      return "Std [sum=" + sum + ", max=" + max + ", min=" + min + ", diff=" + diff + "]";
   }

}
