package example.webstats.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.DBCursor;

import example.webstats.StatsService;
import example.webstats.charts.DefaultChartConfig;
import example.webstats.charts.HighchartsConfig;
import example.webstats.charts.HighchartsConfig.Axis;
import example.webstats.charts.HighchartsConfig.ChartType;
import example.webstats.charts.HighchartsConfig.Legend;
import example.webstats.charts.HighchartsConfig.Title;
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

   @Inject
   private static ObjectMapper om;

   @GET
   @Path("drop")
   public Response drop() throws ServletException, IOException, URISyntaxException
   {
      q.drop();
      return toHome();
   }

   @GET
   public Response index() throws ServletException, IOException, ParseException
   {
      return index("daily", null, null);
   }

   @GET
   @Path("{frame}")
   public Response index(@PathParam("frame") String frame) throws ServletException, IOException, ParseException
   {
      return index(frame, null, null);
   }

   @GET
   @Path("{frame}/{begin}/{end}")
   public Response index(@PathParam("frame") String frame, @PathParam("begin") String begin,
         @PathParam("end") String end) throws ServletException, IOException, ParseException
   {
      DBCursor cursor = q.openCursor(frame, begin, end);

      ChartTable os = q.getDynamicData(cursor, "value.os");
      ChartTable browser = q.getDynamicData(cursor, "value.browser");
      ChartTable action = q.getDynamicData(cursor, "value.action");
      ChartTable conversion = q.getDynamicData(cursor, "value.conversion");
      ChartTable table = q.getData(cursor);

      addChart(frame, table, "hits", "Hit vs Unique", 1, 2);
      addChart(frame, table, "loyalty", "First vs Repeat", 5, 6);
      addChart(frame, conversion, "conversion", "Conversion", conversion.getColumnIndexes(1));

      addFrequencyChart(table, 6, "freq", "Frequency");
      addHourFrequencyChart(q.getData(q.openCursor("hourly", begin, end)), 0, "hfreq", "Hours");

      request.setAttribute("hitsStd", table.getStd(1));
      request.setAttribute("uniquesStd", table.getStd(2));
      request.setAttribute("firstStd", table.getStd(5));
      request.setAttribute("repeatStd", table.getStd(6));

      addPieChart(table, "loyaltyPie", "Loyalty");
      addPieChart(os, "osPie", "OS");
      addPieChart(action, "actionsPie", "Actions");
      addPieChart(browser, Series.detailData(cursor, "value.version"), "browserPie", "Browsers");

      request.setAttribute("origin", request.getRequestURL());
      return forward("/index.jsp");
   }

   @GET
   @Path("daily/hits.json")
   @Produces(MediaType.APPLICATION_JSON)
   public String json() throws ParseException, JsonProcessingException
   {
      DBCursor cursor = q.openCursor("daily", null, null);
      ChartTable table = q.getData(cursor);
      return om.writeValueAsString(Series.timeSeries(table, 1, 2));
   }

   @GET
   @Path("more/{days}/{events}")
   public Response more(@PathParam("days") int days, @PathParam("events") int events) throws URISyntaxException,
         IllegalStateException, JsonProcessingException, ParseException
   {
      q.generateTraffic(days, events);
      q.aggregate();

      // try {
      // if (!suspended.isEmpty()) {
      // DBCursor cursor = openCursor("daily", null, null);
      // ChartTable table = stats.getData(cursor);
      // AsyncResponse ar = suspended.take();
      // ar.resume(Series.toData(table, 1, 2));
      // }
      // } catch (Exception e) {
      // e.printStackTrace();
      // }
      //
      // System.out.println("sent");

      return toHome();
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

   protected void addChart(String frame, ChartTable table, String id, String title, int... cs)
         throws JsonProcessingException
   {
      HighchartsConfig config = new DefaultChartConfig(id, title);
      serieByFrame(frame, config, table, cs);
      request.setAttribute(id, om.writeValueAsString(config));
   }

   protected void addFrequencyChart(ChartTable table, int column, String id, String title)
         throws JsonProcessingException
   {
      HighchartsConfig freqConfig = new DefaultChartConfig(id, title);
      freqAxis(freqConfig);
      Series.frequencySeries(freqConfig, table, column);
      request.setAttribute(id, om.writeValueAsString(freqConfig));
   }

   protected void addHourFrequencyChart(ChartTable table, int column, String id, String title) throws ParseException,
         JsonProcessingException
   {
      HighchartsConfig hfreqConfig = new DefaultChartConfig(id, title);
      freqAxis(hfreqConfig);
      Series.hourFrequencySeries(hfreqConfig, table, column);
      request.setAttribute(id, om.writeValueAsString(hfreqConfig));
   }

   protected void addPieChart(ChartTable table, Map<String, Map<String, Double>> detail, String id, String title)
         throws JsonProcessingException
   {
      HighchartsConfig config = new DefaultChartConfig(id, title);
      config.chart.type = ChartType.pie;
      Series.pieDetailSeries(config, table, detail, false, table.getColumnIndexes(1));
      request.setAttribute(id, om.writeValueAsString(config));
   }

   protected void addPieChart(ChartTable table, String id, String title) throws JsonProcessingException
   {
      HighchartsConfig config = new DefaultChartConfig(id, title);
      config.chart.type = ChartType.pie;
      Series.pieSeries(config, table, table.getColumnIndexes(1));
      request.setAttribute(id, om.writeValueAsString(config));
   }

   protected Response forward(String uri) throws ServletException, IOException
   {
      context.getRequestDispatcher(uri).forward(request, response);
      return Response.ok().build();
   }

   protected void freqAxis(HighchartsConfig freqConfig)
   {
      freqConfig.chart.type = ChartType.bar;
      freqConfig.yAxis = new Axis();
      freqConfig.yAxis.allowDecimals = false;
      freqConfig.yAxis.title = new Title();
      freqConfig.yAxis.labels = freqConfig.labels;
      freqConfig.yAxis.title.text = "";
      freqConfig.yAxis.gridLineColor = "#363836";
      freqConfig.yAxis.min = 0;
      freqConfig.xAxis = new Axis();
      freqConfig.xAxis.labels = freqConfig.labels;
      freqConfig.legend = new Legend();
      freqConfig.legend.enabled = false;
   }

   protected void serieByFrame(String frame, HighchartsConfig conf, ChartTable table, int... cs)
   {
      if ("hourly".equals(frame)) {
         Series.hourlyDenseSeries(conf, table, cs);
      } else {
         Series.timeSeriesOpenEnd(conf, table, cs);
      }
   }

   protected Response toHome() throws URISyntaxException
   {
      return Response.seeOther(new URI("/stats/")).build();
   }
}
