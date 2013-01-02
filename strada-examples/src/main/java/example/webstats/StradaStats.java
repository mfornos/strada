package example.webstats;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import strada.viz.ChartColumn;
import strada.viz.ChartColumn.ColumnType;
import strada.viz.ChartData;
import strada.viz.ChartTable;
import strada.viz.MongoChartData;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * 
 * 
 */
public class StradaStats extends ApplicationFrame
{

   private static final long serialVersionUID = 513296402814195928L;

   private static String[] ua = new String[] {
         //"Opera/12.30 (Nintendo Wii; U; ; 2071; Wii Shop Channel/2.0; en)",
         "Opera/11.0 (Nintendo Wii; U; ; 2071; Wii Shop Channel/1.0; en)",
         "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
         "SonyEricssonK550i/R1JD Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1",
         "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; en-us; Silk/1.1.0-80) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16 Silk-Accelerated=true" };

   public static void main(String[] args)
   {
      final StradaStats ss = new StradaStats();

      ss.generateTraffic(1, 10);
      ss.aggregate();

      ChartTable table = ss.getData(ss.getCollection("daily_stats"));
      ss.showChart(table);
   }

   @Inject
   private MongoClient client;

   @Inject
   private StatsService stats;

   @Inject
   private DB db;

   private Random r = new Random();

   public StradaStats()
   {
      super("Strada demo");

      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         public void run()
         {
            client.close();
         }
      });

      Injector injector = Guice.createInjector(new StatsModule());
      injector.injectMembers(this);
   }

   public void aggregate()
   {
      stats.aggregate();
      stats.serializeFrequencies();
   }

   public void drop()
   {
      stats.clearFrequencies();
      db.dropDatabase();
      client.getDB("test");
   }

   public void generateTraffic(int days, int hits)
   {
      Calendar cal = Calendar.getInstance();
      for (int i = 0; i < days; i++) {
         for (int n = 0; n < hits; n++) {
            stats.onHit(new Hit(n + r.nextInt(100) /* ip + var */, "website", cal.getTime(), new String[] { "purchase",
                  "orange" }, Arrays.copyOfRange(ua, 0, r.nextInt(ua.length + 1))));
            cal.set(Calendar.HOUR_OF_DAY, r.nextInt(22) + 1);
         }
         cal.add(Calendar.DAY_OF_MONTH, -1);
      }
   }

   public ChartTable getData(DBCursor cursor)
   {

      ChartTable table = new ChartTable();
      table.addColumn(new ChartColumn("value.ts", ColumnType.DATE));
      table.addColumn(new ChartColumn("value.total"));
      table.addColumn(new ChartColumn("value.unique"));
      table.addColumn(new ChartColumn("value.first"));
      table.addColumn(new ChartColumn("value.repeat"));
      table.addColumn(new ChartColumn("value.daily.first"));
      table.addColumn(new ChartColumn("value.daily.repeat"));

      for (DBObject obj : cursor) {
         try {
            table.addRow(new MongoChartData(obj, table.getColumns()));
         } catch (Exception e) {
            //
         }
      }

      return table;
   }

   public DBCursor getCollection(String collection)
   {
      return db.getCollection(collection).find().sort(new BasicDBObject().append("value.ts", 1));
   }

   public ChartTable getDynamicData(DBCursor cursor, String selector)
   {

      ChartTable table = new ChartTable();
      table.addColumn(new ChartColumn("value.ts", ColumnType.DATE));

      for (DBObject obj : cursor) {

         try {
            MongoDynamicChartData data = new MongoDynamicChartData(obj, selector, table);
            table.addRow(data);
         } catch (Exception e) {
            e.printStackTrace();
         }

      }

      return table;
   }

   void showChart(ChartTable table)
   {
      final CategoryDataset dataset = createDataset(table);
      final JFreeChart chart = createChart(dataset);
      final ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new Dimension(500, 270));
      setContentPane(chartPanel);

      pack();
      RefineryUtilities.centerFrameOnScreen(this);
      setVisible(true);
   }

   private JFreeChart createChart(final CategoryDataset dataset)
   {
      final JFreeChart chart = ChartFactory.createBarChart("Title", "Time", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);
      return chart;
   }

   private CategoryDataset createDataset(ChartTable table)
   {
      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

      for (ChartData cd : table.getRows()) {
         dataset.addValue(cd.getNumber(1), "Total Hits", cd.getDate(0));
         dataset.addValue(cd.getNumber(2), "First Visitors", cd.getDate(0));
         dataset.addValue(cd.getNumber(3), "Unique Visitors", cd.getDate(0));
         dataset.addValue(cd.getNumber(4), "Repeat Visitors", cd.getDate(0));
         dataset.addValue(cd.getNumber(5), "Purchase Hits", cd.getDate(0));
      }

      return dataset;
   }

}
