package example.webstats.charts;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RawSerializer extends JsonSerializer<String>
{

   @Override
   public void serialize(String field, JsonGenerator jgen, SerializerProvider provider) throws IOException,
         JsonProcessingException
   {
      if (field == null)
         return;

      jgen.writeRaw(":" + field);
   }

}
