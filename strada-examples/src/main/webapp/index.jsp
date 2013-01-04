<%@page language="Java" pageEncoding="UTF-8" contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ufn" uri="http://example.com/functions" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="header">
      <div class="left">
        <h1>Strada</h1>
        <p>Simply analytics</p>
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
    
    <c:if test="${hasData}">
    
    <script>
    var hits, loyalty; // globally available
    $(document).ready(function() {
    	
        hits = new Highcharts.Chart(${hits});
        loyalty = new Highcharts.Chart(${loyalty});
        conversion = new Highcharts.Chart(${conversion});
        freq = new Highcharts.Chart(${freq});
        hfreq = new Highcharts.Chart(${hfreq});
        actionsPie = new Highcharts.Chart(${actionsPie});
        loyaltyPie = new Highcharts.Chart(${loyaltyPie});
        osPie = new Highcharts.Chart(${osPie});
        browserPie = new Highcharts.Chart(${browserPie});

          
          /*
          
          requestData();
          
          function requestData() {
        	  $.ajax({
        	     url: '/stats/daily/hits.json',
        	     success: function(json) {
        	         $.each(json, function(event, value) {
        	             hits.addSeries(value);
        	         });
        	     },
        	     cache: false
        	 });
         };
         
         (function poll() {
         setTimeout(function() {
        	 console.log('suscribed');
         $.ajax({url: '/stats/notify', async: true, success: function(json){
        	    console.log(json);
        	    $.each(json, function(event, value) {
   	             hits.addSeries(value);
   	         });
         },  complete: poll})
         }, 5000);
         })();*/
         
     });
    </script>
    
    <ul class="nav nav-pills">
      <li class="${ufn:active(origin, '.*(/stats/hourly).*$')}"><a href="/stats/hourly">hour</a></li> 
      <li class="${ufn:active(origin, '.*(index.jsp|/stats/|/stats/daily.*)$')}"><a href="/stats/">day</a></li> 
      <li class="${ufn:active(origin, '.*(/stats/weekly).*$')}"><a href="/stats/weekly">week</a></li>
      <li class="${ufn:active(origin, '.*(/stats/monthly).*$')}"><a href="/stats/monthly">month</a></li>
    </ul>

    <div id="msg"></div>
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
    </div>
    <div class="chart-group">
    <div class="chart">
      <div id="conversion" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div>
    <div class="chart-group">
    <div class="chart">
      <div id="loyalty" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div><div class="chart-group">
    <div class="chart">
      <div id="freq" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
    </div><div class="chart-group">
    <div class="chart" style="width: 100%; margin: 10px 0;">
      <div id="loyaltyPie" class="chart" style="min-width: 47%; height: 400px; margin: 0 5px;float:left;"></div>
      <div id="osPie" class="chart" style="min-width: 47%; height: 400px; margin: 0 5px;float:left;"></div>
    </div>
    <div class="chart" style="width: 100%; margin: 10px 0;">
      <div id="actionsPie" class="chart" style="min-width: 47%; height: 400px; margin: 0 5px;float:left;"></div>
      <div id="browserPie" class="chart" style="min-width: 47%; height: 400px; margin: 0 5px;float:left;"></div>
    </div>
    </div>
    
    <input type="hidden" id="date-value" value="/" />
    
    </c:if>
    
    <c:if test="${not hasData}">
      <h3>No data. Feed me.</h3>
    </c:if>
    
    </jsp:body>
</t:layout>
