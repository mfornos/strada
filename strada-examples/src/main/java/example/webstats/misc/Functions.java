package example.webstats.misc;

public class Functions
{
   private Functions()
   {
      //
   }

   public static String active(String string, String pattern)
   {
      return string.matches(pattern) ? "active" : "none";
   }
}
