package example.webstats.charts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HighchartsConfig
{
   public static enum ChartType {
      area, line, areaspline, pie, bar, column, scatter
   };

   public Chart chart;
   public String[] colors;
   public Labels labels;
   public Title title;
   public Axis xAxis;
   public Axis yAxis;
   public Object[] series;
   public Legend legend;
   public PlotOptions plotOptions;

   public HighchartsConfig()
   {
      this.chart = new Chart();
   }

   public static class Column
   {
      public String stacking;
   }

   public static class Legend
   {
      public String borderColor;
      public Style style;
      public Style itemStyle;
      public Boolean enabled;
   }

   public static class Axis
   {
      public String type;
      public Title title;
      public Labels labels;
      public String gridLineColor;
      public TimeLabelFormats dateTimeLabelFormats;
      @JsonSerialize(using = RawSerializer.class)
      public String tickInterval;
      public Boolean allowDecimals;
      public Number min;
      public Number max;
      public String[] categories;
   }

   public static class Series
   {
      public Boolean allowPointSelect;
      public Marker marker;
      public Number lineWidth;
   }

   public static class States
   {
      public Style select;
   }

   public static class Marker extends Style
   {
      public States states;
      public String symbol;
   }

   public static class PlotOptions
   {
      public Series series;
      public Column column;
      public Line line;
   }

   public static class Line
   {
      public Events events;
      public Boolean showInLegend;
   }

   public static class Events
   {
      @JsonSerialize(using = RawSerializer.class)
      public String legendItemClick;
   }

   public static class TimeLabelFormats
   {
      public String year;
      public String month;
      public String day;
      public String second;
      public String minute;
      public String hour;
      public String week;
   }

   public static class Title
   {
      public String text;
      public Style style;
   }

   public static class Style
   {
      public String color;
      public String cursor;
      public String fillColor;
      @JsonInclude
      public String lineColor;
      public Number lineWidth;
   }

   public static class Labels
   {
      public Style style;
      @JsonSerialize(using = RawSerializer.class)
      public String formatter;
   }

   public static class Chart
   {
      public String renderTo;
      public ChartType type;
      public String zoomType;
      public String backgroundColor;
      public String borderColor;
      public Number borderWidth;
      public Boolean plotShadow;
   }

}
