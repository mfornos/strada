package example.webstats.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import strada.viz.ChartTable;
import example.webstats.StradaStats;
import example.webstats.charts.Series;

@Path("/")
public class StatsResource
{
   @Context
   HttpServletRequest request;
   @Context
   HttpServletResponse response;
   @Context
   ServletContext context;

   final StradaStats stats = new StradaStats();
   final Random rand = new Random();

   @GET
   public Response getIndex() throws ServletException, IOException
   {
      ChartTable table = stats.getData("daily_stats");
      request.setAttribute("data", Series.toData(table, 1, 3));
      return forward("/index.jsp");
   }

   @GET
   @Path("more")
   public Response getMore() throws URISyntaxException
   {
      stats.generateTraffic(rand.nextInt(10) + 1, 10);
      stats.aggregate();
      return Response.seeOther(new URI("/stats/")).build();
   }

   protected Response forward(String uri) throws ServletException, IOException
   {
      context.getRequestDispatcher(uri).forward(request, response);
      return Response.ok().build();
   }
}
