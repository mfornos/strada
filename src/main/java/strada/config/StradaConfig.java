package strada.config;

import com.mongodb.MongoClientURI;

public class StradaConfig extends DefaultYamlConfig
{

   public MongoClientURI clientURI()
   {
      String uri = find("mongo.clientURI", "mongodb://127.0.0.1");
      return new MongoClientURI(uri);
   }

}
