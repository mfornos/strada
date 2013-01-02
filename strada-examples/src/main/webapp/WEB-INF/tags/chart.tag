<%@tag description="Chart template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="hmnz" uri="http://mfornos.github.com/humanize/taglib" %>
<%@attribute name="data" required="true" type="java.lang.String" %>
<%@attribute name="title" required="true" type="java.lang.String" %>
<%@attribute name="id" required="true" type="java.lang.String" %>
<%@attribute name="type" required="true" type="java.lang.String" %>

     var ${id}_options = {
		   chart: {
               renderTo: '${id}',
               type: '${type}',
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
               text: '${title}',
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
    };
