package example.webstats.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.server.ManagedAsync;

import strada.viz.ChartTable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import example.webstats.StatsService;
import example.webstats.charts.DefaultChartConfig;
import example.webstats.charts.Funnel;
import example.webstats.charts.HighchartsConfig;
import example.webstats.charts.HighchartsConfig.Axis;
import example.webstats.charts.HighchartsConfig.ChartType;
import example.webstats.charts.HighchartsConfig.Column;
import example.webstats.charts.HighchartsConfig.Legend;
import example.webstats.charts.HighchartsConfig.Title;
import example.webstats.charts.Series;
import example.webstats.hits.Hit;
import example.webstats.hits.Hit.Action;

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
   private static StatsService stats;

   @Inject
   private static ObjectMapper om;

   @GET
   @Path("drop")
   public Response drop() throws ServletException, IOException, URISyntaxException
   {
      stats.drop();
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
      try {
         DBCursor cursor = stats.find(frame, begin, end);

         if (cursor.count() > 0) {
            request.setAttribute("hasData", true);

            ChartTable os = stats.getDynamicData(cursor, "value.os");
            ChartTable browser = stats.getDynamicData(cursor, "value.browser");
            ChartTable action = stats.getDynamicData(cursor, "value.actions");
            ChartTable table = stats.getData(cursor);

            addChart(frame, table, "hits", "Hit vs Unique", 1, 2);
            addChart(frame, table, "loyalty", "First vs Repeat", 3, 4);

            addFrequencyChart(table, 5, "freq", "Frequency");
            addHourFrequencyChart(stats.getData(stats.find("hourly", begin, end)), 0, "hfreq", "Hours");

            request.setAttribute("hitsStd", table.getStd(1));
            request.setAttribute("uniquesStd", table.getStd(2));
            request.setAttribute("firstStd", table.getStd(3));
            request.setAttribute("repeatStd", table.getStd(4));

            request.setAttribute("actions", action.getColumnNames(1));

            addPieChart(table, "loyaltyPie", "Loyalty", 3, 4);
            addPieChart(os, "osPie", "OS", os.getColumnIndexes(1));
            addPieChart(action, "actionsPie", "Actions", action.getColumnIndexes(1));
            addPieChart(browser, Series.detailData(cursor, "value.version"), "browserPie", "Browsers");

            request.setAttribute("origin", request.getRequestURL());
         } else {
            request.setAttribute("hasData", false);
         }
         return forward("/index.jsp");
      } catch (Exception e) {
         e.printStackTrace();
         throw new ServletException(e);
      }
   }

   private static final byte[] TrackingGif = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80,
         0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0,
         0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b };

   @GET
   @Path("agg")
   public Response aggregate() throws URISyntaxException
   {
      stats.aggregate();
      return toHome();
   }

   @GET
   @Path("track")
   @Produces("image/gif")
   public StreamingOutput track()
   {
      Action action = new Action(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE), request.getParameter("__"));
      Hit hit = new Hit(request.getRemoteAddr(), "website", new Date(Long.parseLong(request.getParameter("__time"))), action, request.getHeader(HttpHeaders.USER_AGENT));

      stats.onHit(hit);

      return new StreamingOutput()
      {
         @Override
         public void write(OutputStream out) throws IOException
         {
            out.write(TrackingGif);
         }
      };
   }

   @GET
   @Path("more/{days}/{events}")
   public Response more(@PathParam("days") int days, @PathParam("events") int events) throws URISyntaxException,
         IllegalStateException, JsonProcessingException, ParseException
   {
      stats.generateTraffic(days, events);
      stats.aggregate();

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

   // TODO dynamic indexing of properties by queries?
   // http://docs.mongodb.org/manual/core/indexes/
   @POST
   @Path("funnel/{frame}")
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   public String funnel(@PathParam("frame") String frame, MultivaluedMap<String, String> formParams)
         throws JsonProcessingException
   {
      // XXX @FormParam does not work with ajax post...
      String country = formParams.get("country").get(0);
      String[] steps = formParams.get("names").toArray(new String[] {});
      
      DBObject query;
      String base;
      if ("nil".equalsIgnoreCase(country)) {
         base = "value.actions";
         query = QueryBuilder.start(base + "." + steps[0]).greaterThanEquals(0).get();
      } else {
         base = "value.country." + country;
         query = QueryBuilder.start(base + "." + steps[0]).greaterThanEquals(0).get();
      }
      DBCursor cursor = stats.find(frame, query);
      if (cursor.count() > 0) {
         ChartTable actions = stats.getDynamicData(cursor, base);
         Funnel funnel = new Funnel(actions, steps);
         return om.writeValueAsString(funnel);
      }
      return "{}";
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

   private void addFunnel(String frame)
   {
      try {
         DBObject query = QueryBuilder.start("value.actions.signup").greaterThanEquals(0).get();
         DBCursor applesCursor = stats.find(frame, query);
         if (applesCursor.count() > 0) {
            ChartTable actions = stats.getDynamicData(applesCursor, "value.actions");

            Funnel funnel = new Funnel(actions, "signup", "download", "recommend");
            request.setAttribute("funnel", om.writeValueAsString(funnel));
            addStackedChart(frame, actions, "funnelChart", "Funnel", funnel.indices);
         } else {
            request.setAttribute("funnelChart", "{}");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
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

   protected void addPieChart(ChartTable table, String id, String title, int... columns) throws JsonProcessingException
   {
      HighchartsConfig config = new DefaultChartConfig(id, title);
      config.chart.type = ChartType.pie;
      Series.pieSeries(config, table, columns);
      request.setAttribute(id, om.writeValueAsString(config));
   }

   protected void addStackedChart(String frame, ChartTable table, String id, String title, int... cs)
         throws JsonProcessingException
   {
      HighchartsConfig config = new DefaultChartConfig(id, title);
      config.chart.type = ChartType.column;
      config.plotOptions.column = new Column();
      config.plotOptions.column.stacking = "normal";
      Series.timeSeries(config, table, cs);
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
