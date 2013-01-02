<%@page language="Java" pageEncoding="UTF-8" contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ufn" uri="http://example.com/functions" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="header">
      <div class="left">
        <h1>Strada</h1>
        <p>Simple analytics example.</p>
      </div>

    <div id="date-range">
      <div id="date-range-field">
         <span></span>
         <a href="#">&#9660;</a>
       </div>
      <div id="datepicker-calendar">
        <div class="btn-toolbar">
	    <div class="btn-group" style="float:right;">
	      <a id="set-date" class="btn">SET</a>
	    </div>
	    </div>
      </div>
    </div>

    
    </jsp:attribute>
    <jsp:attribute name="footer">
      <p>
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
      </p>
      <p id="copyright">STRADA @<a href="http://github.com/mfornos">Github</a>.</p>
    </jsp:attribute>
    <jsp:body>
    
    <script>
    var hits, loyalty; // globally available
    $(document).ready(function() {
    	  
    	  <t:chart id="hits" title="Hits" data="${hitsData}" type="area" />
          <t:chart id="loyalty" title="Loyalty" data="${loyaltyData}" type="area" />
          <t:chart id="freq" title="Frequency" data="${frequencyData.data}" type="bar" />
          <t:chart id="hfreq" title="Hours" data="${hourFrequencyData.data}" type="bar" />
          <t:chart id="loyaltyPie" title="Loyalty" data="${loyaltyPieData}" type="pie" />
          <t:chart id="osPie" title="OS" data="${osPieData}" type="pie" />
          <t:chart id="actionsPie" title="Actions" data="${actionsPieData}" type="pie" />
          <t:chart id="versionPie" title="Browser" data="${versionPieData}" type="pie" />
    	  
                    
          hits = new Highcharts.Chart(hits_options);
          loyalty = new Highcharts.Chart(loyalty_options);
          freq = new Highcharts.Chart($.extend(freq_options, {
        	  yAxis: {
        		  min: 0,
        		  allowDecimals: false,
        		  labels: {
        			  style: {
        				  color: '#FFF'
        			  }
        		  },
        		  title: null
        	  },
        	  xAxis: {
        		  title: null,
        		  categories: ${frequencyData.labels},
        		  labels: {
        			  style: {
        				  color: '#FFF'
        			  }
        		  }
        	  },
        	  legend: { enabled: false }
          }));
          hfreq = new Highcharts.Chart($.extend(hfreq_options, {
        	  yAxis: {
        		  min: 0,
        		  allowDecimals: false,
        		  labels: {
        			  style: {
        				  color: '#FFF'
        			  }
        		  },
        		  title: null
        	  },
        	  xAxis: {
        		  title: null,
        		  categories: ${hourFrequencyData.labels},
        		  labels: {
        			  style: {
        				  color: '#FFF'
        			  }
        		  }
        	  },
        	  legend: { enabled: false }
          }));
          loyaltyPie = new Highcharts.Chart(loyaltyPie_options);
          osPie = new Highcharts.Chart(osPie_options);
          actionsPie = new Highcharts.Chart(actionsPie_options);
          versionPie = new Highcharts.Chart(versionPie_options);
         
     });
    </script>
    
    <ul class="nav nav-pills">
      <li class="${ufn:active(origin, '.*(/stats/hourly).*$')}"><a href="/stats/hourly">hour</a></li> 
      <li class="${ufn:active(origin, '.*(index.jsp|/stats/|/stats/daily.*)$')}"><a href="/stats/">day</a></li> 
      <li class="${ufn:active(origin, '.*(/stats/weekly).*$')}"><a href="/stats/weekly">week</a></li>
      <li class="${ufn:active(origin, '.*(/stats/monthly).*$')}"><a href="/stats/monthly">month</a></li>
    </ul>

    <div class="chart-group">
    <div class="fluid counters">
        <t:std counter="${uniquesStd}" title="Uniques" />
        <t:std counter="${hitsStd}" title="Hits" />
    </div>
    <div class="chart">
      <div id="hits" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div>
    <div class="chart-group">
    <div class="fluid counters">
      <t:std counter="${firstStd}" title="First Visitors" />
      <t:std counter="${repeatStd}" title="Repeat Visitors" />
    </div>
    <div class="chart">
      <div id="hfreq" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div><div class="chart-group">
    <div class="chart">
      <div id="loyalty" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div><div class="chart-group">
    <div class="chart">
      <div id="freq" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div><div class="chart-group">
    <div class="chart" style="width: 80%; margin: 10px 0;">
      <div id="loyaltyPie" class="chart" style="min-width: 45%; height: 400px; margin: 0 10px;float:left;"></div>
      <div id="osPie" class="chart" style="min-width: 45%; height: 400px; margin: 0 10px;float:left;"></div>
    </div>
    <div class="chart" style="width: 80%; margin: 10px 0;">
      <div id="actionsPie" class="chart" style="min-width: 45%; height: 400px; margin: 0 10px;float:left;"></div>
      <div id="versionPie" class="chart" style="min-width: 45%; height: 400px; margin: 0 10px;float:left;"></div>
    </div>
    </div>
    
    <input type="hidden" id="date-value" value="/" />
    
    </jsp:body>
</t:layout>
