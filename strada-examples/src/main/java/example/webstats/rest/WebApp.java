package example.webstats.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import example.webstats.StradaStats;


@ApplicationPath("/stats")
public class WebApp extends Application
{
   @Override
   public Set<Class<?>> getClasses()
   {
      new StradaStats();
      final Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(StatsResource.class);
      return classes;
   }
}
