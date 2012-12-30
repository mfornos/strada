package strada.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class TestScriptInterpolator
{
   @Test
   public void basic() throws IOException
   {
      ScriptInterpolator interpol = new ScriptInterpolator();
      String value = "new Date(this._id.d.getFullYear(), this._id.d.getMonth(),this._id.d.getDate(),0, 0, 0, 0)";
      interpol.addVar("date", value);

      String script = Files.toString(new File("src/test/resources/sample.map"), Charset.defaultCharset());
      String res = interpol.interpolate(script);
      Assert.assertTrue(res.contains(": " + value));
   }
}
