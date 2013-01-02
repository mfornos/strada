package strada.viz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import strada.viz.ChartColumn.ColumnType;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public final class MongoTableBuilder
{

   public static final Logger LOGGER = LoggerFactory.getLogger(MongoTableBuilder.class);

   public static MongoTableBuilder fromCursor(DBCursor cursor)
   {
      return new MongoTableBuilder(cursor);
   }

   private final DBCursor cursor;
   private final ChartTable table;
   private String selector;

   private MongoTableBuilder(DBCursor cursor)
   {
      this.cursor = cursor;
      this.table = new ChartTable();
   }

   public ChartTable build()
   {
      if (selector == null) {
         buildStatic();
      } else {
         buildDynamic();
      }
      return table;
   }

   public MongoTableBuilder columns(ChartColumn... cols)
   {
      for (ChartColumn col : cols) {
         table.addColumn(col);
      }
      return this;
   }

   public MongoTableBuilder columns(ColumnType type, String... selectors)
   {
      for (String selector : selectors) {
         table.addColumn(new ChartColumn(selector, type));
      }
      return this;
   }

   public MongoTableBuilder columns(String... selectors)
   {
      return columns(ColumnType.NUMBER, selectors);
   }

   public MongoTableBuilder dynamic(String selector)
   {
      this.selector = selector;
      return this;
   }

   private void buildDynamic()
   {
      for (DBObject obj : cursor) {
         try {
            table.addRow(new MongoDynamicChartData(obj, table, selector));
         } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         }
      }
   }

   private void buildStatic()
   {
      for (DBObject obj : cursor) {
         try {
            table.addRow(new MongoChartData(obj, table.getColumns()));
         } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         }
      }
   }
}
