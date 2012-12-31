<%@ page language="Java" pageEncoding="UTF-8" contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="header">
      <h1>STRADA Example</h1>
    </jsp:attribute>
    <jsp:attribute name="footer">
      <p id="copyright">STRADA. <a href="http://github.com/mfornos">Github</a>.</p>
    </jsp:attribute>
    <jsp:body>
    <script>
    var chart1, loyalty; // globally available
    $(document).ready(function() {
          chart1 = new Highcharts.Chart({
             chart: {
                renderTo: 'container',
                type: 'spline'
             },
             title: {
                text: 'Fruit Consumption'
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
                     }
                 }
             },
             yAxis: {
                title: {
                   text: 'Fruit eaten'
                }
             },
             series: ${data}
          }); 
          loyalty = new Highcharts.Chart({
              chart: {
                 renderTo: 'loyalty',
                 type: 'column'
              },
              title: {
                 text: 'User loyalty'
              },
              xAxis: {
            	 tickInterval: 1
              },
              series: ${loyaltyData}
           });   
       });
    </script>
    
      <div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
      <div id="loyalty" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    
    </jsp:body>
</t:layout>
