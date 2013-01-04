package example.webstats.charts;


public class DefaultChartConfig extends HighchartsConfig
{
   
   public DefaultChartConfig(String id, String titleText)
   {
      super();

      chart.renderTo = id;
      chart.type = ChartType.line;
      chart.borderColor = "#CCC";
      chart.backgroundColor = "#414541";
      chart.borderWidth = 2.5;
      chart.plotShadow = true;
      chart.zoomType = "x";
      
      plotOptions = new PlotOptions();
      plotOptions.series = new Series();
      plotOptions.series.allowPointSelect = false;
      plotOptions.series.lineWidth = 3;
      plotOptions.series.marker = new Marker();
      plotOptions.series.marker.fillColor = "#414541";
      plotOptions.series.marker.lineWidth = 1.5;
      plotOptions.series.marker.lineColor = null;
      plotOptions.series.marker.symbol = "circle";

      colors = new String[] { "#61D2D6", "#FFE44D", "#B5E156", "#EA3556", "#82187C", "#DB843D", "#92A8CD", "#A47D7C",
            "#B5CA92" };
      labels = new Labels();
      labels.style = new Style();
      labels.style.color = "#EEE";

      title = new Title();
      title.text = titleText;
      title.style = labels.style;

      legend = new Legend();
      legend.borderColor = "";
      legend.style = new Style();
      legend.style.color = "#EEE";
      legend.itemStyle = new Style();
      legend.itemStyle.color = "#EEE";
      legend.itemStyle.cursor = "pointer";

      xAxis = new Axis();
      xAxis.type = "datetime";
      xAxis.labels = new Labels();
      xAxis.labels.style = labels.style;
      xAxis.labels.formatter = "function() { return Highcharts.dateFormat('%b %e', this.value); }";
      xAxis.dateTimeLabelFormats = new TimeLabelFormats();
      xAxis.dateTimeLabelFormats.month = "%e. %b";
      xAxis.dateTimeLabelFormats.year = "%b";
      xAxis.title = new Title();
      xAxis.title.text = "";
      yAxis = new Axis();
      yAxis.labels = labels;
      yAxis.title = xAxis.title;
      yAxis.gridLineColor = "#363836";
   }
}
