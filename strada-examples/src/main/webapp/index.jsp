<%@page language="Java" pageEncoding="UTF-8" contentType="text/html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ufn" uri="http://example.com/functions" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="header">
    
    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">Strada</a>
          <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#about">About</a></li>
              <li><a href="#contact">Contact</a></li>
            </ul>
            
                      <div class="navbar-search pull-right">
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
        </div>
            
          </div><!--/.nav-collapse -->
        </div>
        
      </div>
    </div>
    
      <div class="left">
        <h1>Strada</h1>
        <p>Simply analytics</p>
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
        freq = new Highcharts.Chart(${freq});
        hfreq = new Highcharts.Chart(${hfreq});
        actionsPie = new Highcharts.Chart(${actionsPie});
        loyaltyPie = new Highcharts.Chart(${loyaltyPie});
        osPie = new Highcharts.Chart(${osPie});
        browserPie = new Highcharts.Chart(${browserPie});
        
        //funnelChart = new Highcharts.Chart(${funnelChart});
        
        $('#fs').submit(function(e) {
        	  e.preventDefault();
        	  console.log($(this).serialize());
        	  console.log($(this).attr('method'));
        	  $.post($(this).attr('action'), $(this).serialize(), function(json) {
        	         new Funnel('#levels', json);
        	     });
        });
        
        $(".multiselect").multiselect();
        
        $.ajax({
        	  url : 'http://127.0.0.1:8080/stats/hello/jsonp',
        	  dataType : 'jsonp',
        	  data     : {some: "data"},
        	  success  : function(jsonp) {
        		  console.log(jsonp);
        	  }
        });
          
          /*
                   
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
    
    <h2>Funnels</h2>
    <div class="chart-group">
    <form method="POST" action="/stats/funnel/daily" id="fs" name="fs">
        <label>Steps</label>
        <select name="country">
          <option value="nil" selected="selected">-</option>
          <option value="es">Spain</option>
          <option value="en">UK</option>
        </select>
        <select name="names" multiple="multiple" class="multiselect">
          <c:forEach items="${actions}" var="action">
            <option value="${action}">${action}</option>
          </c:forEach>
        </select>
        <button class="btn" type="submit">Create</button>
    </form>
    <div class="funnels" id="levels">
      
    </div>
    <!--<div id="funnelChart" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>-->
    </div>
    
    <h2>Some charts</h2>
    
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
