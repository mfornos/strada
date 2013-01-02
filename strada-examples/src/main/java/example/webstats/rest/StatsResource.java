package example.webstats.rest;

import humanize.Humanize;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.mongodb.DBCursor;

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

   final static StradaStats stats = new StradaStats();

   @GET
   @Path("drop")
   public Response getDrop() throws ServletException, IOException, URISyntaxException
   {
      stats.drop();
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
   @Path("{frame}/{begin}/{end}")
   public Response getIndex(@PathParam("frame") String frame, @PathParam("begin") String begin,
         @PathParam("end") String end) throws ServletException, IOException, ParseException
   {
      DBCursor cursor = openCursor(frame, begin, end);

      ChartTable os = stats.getDynamicData(cursor, "value.os");
      ChartTable browser = stats.getDynamicData(cursor, "value.browser");
      ChartTable action = stats.getDynamicData(cursor, "value.action");
      ChartTable table = stats.getData(cursor);

      request.setAttribute("hitsData", ("hourly".equals(frame)) ? Series.toHourlyDenseData(table, 1, 2)
            : Series.toData(table, 1, 2));
      request.setAttribute("loyaltyData", Series.toData(table, 5, 6));
      request.setAttribute("frequencyData", Series.toFrequency(table, 6));
      request.setAttribute("hitsStd", table.getStd(1));
      request.setAttribute("uniquesStd", table.getStd(2));
      request.setAttribute("firstStd", table.getStd(5));
      request.setAttribute("repeatStd", table.getStd(6));
      request.setAttribute("loyaltyPieData", Series.toPieData(table, 5, 6));
      request.setAttribute("osPieData", Series.toPieData(os, os.getColumnIndexes(1)));
      request.setAttribute("browserPieData", Series.toPieData(browser, browser.getColumnIndexes(1)));
      request.setAttribute("actionsPieData", Series.toPieData(action, action.getColumnIndexes(1)));

      request.setAttribute("versionPieData", Series.toDetailPieChart(browser, Series.getSubData(cursor, "value.version"), browser.getColumnIndexes(1)));

      request.setAttribute("origin", request.getRequestURL());
      return forward("/index.jsp");
   }

   @GET
   @Path("more/{days}/{events}")
   public Response getMore(@PathParam("days") int days, @PathParam("events") int events) throws URISyntaxException
   {
      stats.generateTraffic(days, events);
      stats.aggregate();
      return toHome();
   }

   protected Response forward(String uri) throws ServletException, IOException
   {
      context.getRequestDispatcher(uri).forward(request, response);
      return Response.ok().build();
   }

   protected DBCursor openCursor(String frame, String begin, String end) throws ParseException
   {
      DBCursor cursor;
      if (begin != null && end != null) {
         // XXX dates must not be equals
         DateFormat dateFormat = Humanize.dateFormatInstance("dd-MM-yyyy");
         cursor = stats.getCollection(frame + "_stats", dateFormat.parse(begin), dateFormat.parse(end));
      } else {
         cursor = stats.getCollection(frame + "_stats");
      }
      return cursor;
   }

   protected Response toHome() throws URISyntaxException
   {
      return Response.seeOther(new URI("/stats/")).build();
   }
}
