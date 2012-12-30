package strada.mapreduce;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import humanize.text.util.InterpolationHelper;
import humanize.text.util.Replacer;

public class ScriptInterpolator implements Replacer
{

   private static final Pattern VAR = Pattern.compile("\\$\\{(.+)\\}");

   private final Map<String, String> vars;

   public ScriptInterpolator()
   {
      vars = new HashMap<String, String>();
   }

   public String interpolate(String script)
   {
      return InterpolationHelper.interpolate(script, VAR, this);
   }

   public void addVar(String name, String value)
   {
      vars.put(name, value);
   }

   @Override
   public String replace(String var)
   {
      return vars.get(var);
   }

}
