package strada.mapreduce;

import java.io.File;

import com.google.common.base.Preconditions;

public class ScriptLoader
{
   public static final String FINALIZE_SCRIPT_EXT = ".final.js";
   public static final String REDUCE_SCRIPT_EXT = ".reduce.js";
   public static final String MAP_SCRIPT_EXT = ".map.js";

   public static File[] loadScripts(String pathName)
   {
      File m = new File(pathName + MAP_SCRIPT_EXT);
      Preconditions.checkArgument(m.exists(), "Please, provide a '%s%s' file", pathName, MAP_SCRIPT_EXT);
      File r = new File(pathName + REDUCE_SCRIPT_EXT);
      Preconditions.checkArgument(r.exists(), "Please, provide a '%s%s' file", pathName, REDUCE_SCRIPT_EXT);
      File f = new File(pathName + FINALIZE_SCRIPT_EXT);
      if (!f.exists()) {
         f = null;
      }

      return new File[] { m, r, f };
   }
}
