package example.webstats.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import strada.viz.ChartTable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.mongodb.DBCursor;

import example.webstats.StatsService;
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

   private static final BlockingQueue<AsyncResponse> suspended = new ArrayBlockingQueue<AsyncResponse>(5);

   @Inject
   private static StatsService q;

   @GET
   @Path("drop")
   public Response getDrop() throws ServletException, IOException, URISyntaxException
   {
      q.drop();
      return toHome();
   }

   @GET
   public Response getIndex() throws ServletException, IOException, ParseException
   {
      return getIndex("daily", null, null);
   }

   @GET
   @Path("{frame}")
   public Response getIndex(@PathParam("frame") String frame) throws ServletException, IOException, ParseException
   {
      return getIndex(frame, null, null);
   }

   @GET
   @Path("daily/hits.json")
   @Produces(MediaType.APPLICATION_JSON)
   public String getJson() throws ParseException, JsonProcessingException
   {
      DBCursor cursor = q.openCursor("daily", null, null);
      ChartTable table = q.getData(cursor);
      return Series.toData(table, 1, 2);
   }

   @GET
   @ManagedAsync
   @Path("notify")
   @Produces(MediaType.APPLICATION_JSON)
   public void notify(@Suspended final AsyncResponse ar, @QueryParam("id") int requestId) throws IOException,
         InterruptedException
   {
      suspended.put(ar);
   }

   @GET
   @Path("{frame}/{begin}/{end}")
   public Response getIndex(@PathParam("frame") String frame, @PathParam("begin") String begin,
         @PathParam("end") String end) throws ServletException, IOException, ParseException
   {
      // TODO move to Facade service... Stats
      DBCursor cursor = q.openCursor(frame, begin, end);

      ChartTable os = q.getDynamicData(cursor, "value.os");
      ChartTable browser = q.getDynamicData(cursor, "value.browser");
      ChartTable action = q.getDynamicData(cursor, "value.action");
      ChartTable conversion = q.getDynamicData(cursor, "value.conversion");
      ChartTable table = q.getData(cursor);

      request.setAttribute("hitsData", ("hourly".equals(frame)) ? Series.toHourlyDenseData(table, 1, 2)
            : Series.toData(table, 1, 2));
      request.setAttribute("loyaltyData", ("hourly".equals(frame)) ? Series.toHourlyDenseData(table, 6, 5)
            : Series.toData(table, 6, 5));
      request.setAttribute("conversionData", Series.toData(conversion, conversion.getColumnIndexes(1)));

      request.setAttribute("frequencyData", Series.toFrequency(table, 6));
      request.setAttribute("hourFrequencyData", Series.toHourFrequency(q.getData(q.openCursor("hourly", begin, end)), 0));

      request.setAttribute("hitsStd", table.getStd(1));
      request.setAttribute("uniquesStd", table.getStd(2));
      request.setAttribute("firstStd", table.getStd(5));
      request.setAttribute("repeatStd", table.getStd(6));
      
      request.setAttribute("loyaltyPieData", Series.toPieData(table, 5, 6));
      request.setAttribute("osPieData", Series.toPieData(os, os.getColumnIndexes(1)));
      request.setAttribute("browserPieData", Series.toPieData(browser, browser.getColumnIndexes(1)));
      request.setAttribute("actionsPieData", Series.toPieData(action, action.getColumnIndexes(1)));

      request.setAttribute("versionPieData", Series.toDetailPieChart(browser, Series.getSubData(cursor, "value.version"), false, browser.getColumnIndexes(1)));

      request.setAttribute("origin", request.getRequestURL());
      return forward("/index.jsp");
   }

   @GET
   @Path("more/{days}/{events}")
   public Response getMore(@PathParam("days") int days, @PathParam("events") int events) throws URISyntaxException,
         IllegalStateException, JsonProcessingException, ParseException
   {
      q.generateTraffic(days, events);
      q.aggregate();

//      try {
//         if (!suspended.isEmpty()) {
//            DBCursor cursor = openCursor("daily", null, null);
//            ChartTable table = stats.getData(cursor);
//            AsyncResponse ar = suspended.take();
//            ar.resume(Series.toData(table, 1, 2));
//         }
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//
//      System.out.println("sent");

      return toHome();
   }

   protected Response forward(String uri) throws ServletException, IOException
   {
      context.getRequestDispatcher(uri).forward(request, response);
      return Response.ok().build();
   }



   protected Response toHome() throws URISyntaxException
   {
      return Response.seeOther(new URI("/stats/")).build();
   }
}
