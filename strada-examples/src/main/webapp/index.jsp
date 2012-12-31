<%@ page language="Java" pageEncoding="UTF-8" contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ufn" uri="http://example.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="header">
      <div class="left">
        <h1>Strada</h1>
        <p>Simple analytics example.</p>
      </div>
      <div class="btn-toolbar" style="float:right;">
    <div class="btn-group">
     <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
      Send
      <span class="caret"></span>
     </a>
      <ul class="dropdown-menu">
      <li><a href="/stats/more/10/10">10/10 day-hits</a></li>
      <li><a href="/stats/more/20/10">20/10 day-hits</a></li>
      <li><a href="/stats/more/30/20">30/20 day-hits</a></li>
     </ul>
    </div>
    <div class="btn-group">
     <a class="btn" href="/stats/drop">Clear</a>
    </div>
    </div>
    </jsp:attribute>
    <jsp:attribute name="footer">
      <p id="copyright">STRADA @<a href="http://github.com/mfornos">Github</a>.</p>
    </jsp:attribute>
    <jsp:body>
    
    <script>
    // XXX extract js...!
    var chart1, loyalty; // globally available
    $(document).ready(function() {
          chart1 = new Highcharts.Chart({
             chart: {
                renderTo: 'hitsvsuniques',
                zoomType: 'x',
                type: 'spline',
                backgroundColor:'rgba(255, 255, 255, 0.1)',
                borderColor: '#CCC',
                borderWidth: 2.5,
                plotShadow: true
             },
             colors: [
                  	'#61D2D6', 
                	'#FFE44D', 
                	'#B5E156',
                	'#EA3556', 
                  	'#82187C', 
                  	'#DB843D', 
                  	'#92A8CD', 
                  	'#A47D7C', 
                  	'#B5CA92'
                  ],
            labels: {
                  style: {
                		color: '#EEE'
                	}
            },
             title: {
                text: 'Hits vs Uniques',
                style: {
            		color: '#EEE'
            	}
             },
             legend: {
            	borderColor: '#CCC',
            	style: {
             		color: '#EEE'
             	},
             	itemStyle: {
             		cursor: 'pointer',
             		color: '#EEE'
             	}
             },
             xAxis: {
            	 type: 'datetime',
            	 dateTimeLabelFormats: { // don't display the dummy year
                     month: '%e. %b',
                     year: '%b'
                 },
                 labels: {
                     formatter: function() {
                          return Highcharts.dateFormat("%b %e", this.value);
                     },
                     style: {
                 		color: '#EEE'
                 	}
                 }
             },
             yAxis: {
            	 gridLineColor: '#CCC',
            	 title: {
            		 text: '',
            		 style: {
                 		color: '#EEE'
                 	}
            	 },
            	 labels: {
                     style: {
                 		color: '#EEE'
                 	}
                 }
             },
             series: ${data}
          }); 
          loyalty = new Highcharts.Chart({
              chart: {
                 renderTo: 'loyalty',
                 type: 'column',
                 zoomType: 'x',
              backgroundColor:'rgba(255, 255, 255, 0.1)',
              borderColor: '#CCC',
              borderWidth: 2.5,
              plotShadow: true
           },
           colors: [ 
                	'#61D2D6', 
                	'#FFE44D', 
                	'#B5E156',
                	'#EA3556',
                	'#82187C', 
                	'#DB843D', 
                	'#92A8CD', 
                	'#A47D7C', 
                	'#B5CA92'
                ],
          labels: {
                style: {
              		color: '#EEE'
              	}
          },
           title: {
              text: 'Hits vs Uniques',
              style: {
          		color: '#EEE'
          	}
           },
           legend: {
          	borderColor: '#CCC',
          	style: {
           		color: '#EEE'
           	},
           	itemStyle: {
           		cursor: 'pointer',
           		color: '#EEE'
           	}
           },
           xAxis: {
          	 type: 'datetime',
          	 dateTimeLabelFormats: { // don't display the dummy year
                   month: '%e. %b',
                   year: '%b'
               },
               labels: {
                   formatter: function() {
                        return Highcharts.dateFormat("%b %e", this.value);
                   },
                   style: {
               		color: '#EEE'
               	}
               }
           },
           yAxis: {
          	 gridLineColor: '#CCC',
          	 title: {
          		 text: '',
          		 style: {
               		color: '#EEE'
               	}
          	 },
          	 labels: {
                   style: {
               		color: '#EEE'
               	}
               }
           },
              series: ${loyaltyData}
           });   
       });
    </script>
    <ul class="nav nav-pills">
      <li class="${ufn:active(origin, '.*(/stats/hourly)$')}"><a href="/stats/hourly">hour</a></li> 
      <li class="${ufn:active(origin, '.*(index.jsp|/stats/|/stats/daily)$')}"><a href="/stats/">day</a></li> 
      <li class="${ufn:active(origin, '.*(/stats/weekly*)$')}"><a href="/stats/weekly">week</a></li>
      <li class="${ufn:active(origin, '.*(/stats/monthly*)$')}"><a href="/stats/monthly">month</a></li>
    </ul>

    <div class="chart-group">
    <div class="fluid counters">
        <t:std counter="${uniquesStd}" title="Uniques" />
        <t:std counter="${hitsStd}" title="Hits" />
    </div>
    <div class="chart">
      <div id="hitsvsuniques" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div>
    <div class="chart-group">
    <div class="fluid counters">
      <t:std counter="${firstStd}" title="First Visitors" />
      <t:std counter="${repeatStd}" title="Repeat Visitors" />
    </div>
    <div class="chart">
      <div id="loyalty" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div>
    
    </jsp:body>
</t:layout>
