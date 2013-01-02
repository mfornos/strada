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
      <div id="datepicker-calendar"></div>
      
          <div class="btn-toolbar" style="float:right;">
    <div class="btn-group">
      <a id="set-date" class="btn">SET</a>
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
    	  
    	  <t:chart id="hits" title="Hits" data="${hitsData}" type="areaspline" />
          <t:chart id="loyalty" title="Loyalty" data="${loyaltyData}" type="column" />
          <t:chart id="loyaltyPie" title="Loyalty" data="${loyaltyPieData}" type="pie" />
          <t:chart id="osPie" title="OS" data="${osPieData}" type="pie" />
          <t:chart id="actionsPie" title="Actions" data="${actionsPieData}" type="pie" />
          <t:chart id="versionPie" title="Browser" data="${versionPieData}" type="pie" />
    	  
          hits = new Highcharts.Chart(hits_options);
          loyalty = new Highcharts.Chart(loyalty_options);
          loyaltyPie = new Highcharts.Chart(loyaltyPie_options);
          osPie = new Highcharts.Chart(osPie_options);
          actionsPie = new Highcharts.Chart(actionsPie_options);
          versionPie = new Highcharts.Chart(versionPie_options);
          
          var to = new Date();
          var from = new Date(to.getTime() - 1000 * 60 * 60 * 24 * 14);

          $('#datepicker-calendar').DatePicker({
            inline: true,
            date: [from, to],
            calendars: 3,
            mode: 'range',
            current: new Date(to.getFullYear(), to.getMonth() - 1, 1),
            onChange: function(dates,el) {
              // update the range display
              $('#date-range-field span').text(
                dates[0].getDate()+' '+dates[0].getMonthName(true)+', '+
                dates[0].getFullYear()+' - '+
                dates[1].getDate()+' '+dates[1].getMonthName(true)+', '+
                dates[1].getFullYear());
              
              $('#date-value').val(
            	dates[0].getDate()+'-'+(dates[0].getMonth()+1)+'-'+dates[0].getFullYear()+'/'+
            	dates[1].getDate()+'-'+(dates[1].getMonth()+1)+'-'+dates[1].getFullYear()
              );
            }
          });
          
          $('#set-date').click(function(e) {
        	  e.stopPropagation();
        	  // TODO handle this decently... unit already date set... 
        	  var ol = window.location.href;
        	  window.location.href =  ol + (endsWith(ol, "stats/")? "daily/" : "/") + $('#date-value').val();	  
          });
          
       // initialize the special date dropdown field
          $('#date-range-field span').text(from.getDate()+' '+from.getMonthName(true)+', '+from.getFullYear()+' - '+
                                          to.getDate()+' '+to.getMonthName(true)+', '+to.getFullYear());
          
          // bind a click handler to the date display field, which when clicked
          // toggles the date picker calendar, flips the up/down indicator arrow,
          // and keeps the borders looking pretty
          $('#date-range-field').bind('click', function(){
            $('#datepicker-calendar').toggle();
            if($('#date-range-field a').text().charCodeAt(0) == 9660) {
              // switch to up-arrow
              $('#date-range-field a').html('&#9650;');
              $('#date-range-field').css({borderBottomLeftRadius:0, borderBottomRightRadius:0});
              $('#date-range-field a').css({borderBottomRightRadius:0});
            } else {
              // switch to down-arrow
              $('#date-range-field a').html('&#9660;');
              $('#date-range-field').css({borderBottomLeftRadius:5, borderBottomRightRadius:5});
              $('#date-range-field a').css({borderBottomRightRadius:5});
            }
            return false;
          });
          
          // global click handler to hide the widget calendar when it's open, and
          // some other part of the document is clicked.  Note that this works best
          // defined out here rather than built in to the datepicker core because this
          // particular example is actually an 'inline' datepicker which is displayed
          // by an external event, unlike a non-inline datepicker which is automatically
          // displayed/hidden by clicks within/without the datepicker element and datepicker respectively
          $('html').click(function() {
            if($('#datepicker-calendar').is(":visible")) {
              $('#datepicker-calendar').hide();
              $('#date-range-field a').html('&#9660;');
              $('#date-range-field').css({borderBottomLeftRadius:5, borderBottomRightRadius:5});
              $('#date-range-field a').css({borderBottomRightRadius:5});
            }
          });
          
          // stop the click propagation when clicking on the calendar element
          // so that we don't close it
          $('#datepicker-calendar').click(function(event){
            event.stopPropagation();
          });
         
     });
    function endsWith(str, suffix) {
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    }
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
      <div id="loyalty" class="chart" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    </div>
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
