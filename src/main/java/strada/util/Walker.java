package strada.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Walker
{

   public static Object get(BasicDBObject from, String selector)
   {
      String[] parts = split(selector);
      return walkToNode(from, parts).get(parts[parts.length - 1]);
   }

   public static Object get(DBObject obj, String selector)
   {
      String[] parts = split(selector);
      BasicDBObject node = walkToNode((BasicDBObject) obj, parts);
      return node.get(parts[parts.length - 1]);
   }

   public static String[] split(String selector)
   {
      return selector.split("\\.");
   }

   public static BasicDBObject walkToNode(BasicDBObject from, String[] parts)
   {
      BasicDBObject last = from;
      for (int i = 0; i < parts.length - 1; i++) {
         last = (BasicDBObject) last.get(parts[i]);
      }
      return last;
   }

}
