package strada.service;

import com.google.inject.Inject;
import com.mongodb.DB;

public abstract class DefaultAnalyticsService implements AnalyticsService
{

   // TODO Implement
   // - lifecycle
   // - collections management
   // - call point writers
   // - call map reduce procs

   private final DB database;

   @Inject
   public DefaultAnalyticsService(DB database)
   {
      this.database = database;
   }

}
