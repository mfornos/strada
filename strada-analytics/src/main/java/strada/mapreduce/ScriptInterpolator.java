package strada.mapreduce;

import humanize.text.util.InterpolationHelper;
import humanize.text.util.Replacer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ScriptInterpolator implements Replacer
{

   private static final Pattern VAR = Pattern.compile("\\$\\{(.+)\\}");

   private final Map<String, String> vars;

   public ScriptInterpolator()
   {
      vars = new HashMap<String, String>();
   }

   public void addVar(String name, String value)
   {
      vars.put(name, value);
   }

   public String interpolate(String script)
   {
      return InterpolationHelper.interpolate(script, VAR, this);
   }

   @Override
   public String replace(String var)
   {
      return vars.get(var);
   }

}
