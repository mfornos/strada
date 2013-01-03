package strada.mapreduce;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand;

public class TestCommandBuilder
{
   @Test
   public void pathBuild()
   {
      MapReduceCommand cmd = CommandBuilder.startCommand(Mockito.mock(DBCollection.class)).forPathName("src/test/resources/sample").build();
      Assert.assertNotNull(cmd);
      Assert.assertTrue(cmd.getMap().contains("emit(id, values);"));
      Assert.assertTrue(cmd.getReduce().contains("return result;"));
   }

   @Test
   public void pathInterpolBuild()
   {
      ScriptInterpolator mint = new ScriptInterpolator();
      mint.addVar("date", "hello");

      ScriptInterpolator rint = new ScriptInterpolator();
      rint.addVar("dummy", "hello");

      MapReduceCommand cmd = CommandBuilder.startCommand(Mockito.mock(DBCollection.class)).forPathName("src/test/resources/sample").mapInterpolator(mint).reduceInterpolator(rint).build();
      Assert.assertNotNull(cmd);
      Assert.assertTrue(cmd.getMap().contains("hello"));
      Assert.assertTrue(cmd.getReduce().contains("hello"));
   }
   
   @Test
   public void simpleInterpolBuild()
   {
      ScriptInterpolator mint = new ScriptInterpolator();
      mint.addVar("date", "hello");

      ScriptInterpolator rint = new ScriptInterpolator();
      rint.addVar("dummy", "hello");

      MapReduceCommand cmd = CommandBuilder.startCommand(Mockito.mock(DBCollection.class)).mapScript("function(){ ${date}; }").reduceScript("function(){ ${dummy}; }").mapInterpolator(mint).reduceInterpolator(rint).build();
      Assert.assertNotNull(cmd);
      Assert.assertTrue(cmd.getMap().contains("hello"));
      Assert.assertTrue(cmd.getReduce().contains("hello"));
   }

   @Test
   public void simpleBuild()
   {
      MapReduceCommand cmd = CommandBuilder.startCommand(Mockito.mock(DBCollection.class)).mapScript("function(){}").reduceScript("function(){}").build();
      Assert.assertNotNull(cmd);
      Assert.assertEquals(cmd.getMap(), "function(){}");
      Assert.assertEquals(cmd.getReduce(), "function(){}");
   }

}
