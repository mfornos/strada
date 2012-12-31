<%@tag description="Std template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="hmnz" uri="http://mfornos.github.com/humanize/taglib" %>
<%@attribute name="counter" required="true" type="strada.viz.Std" %>
<%@attribute name="title" required="true" type="java.lang.String" %>
    
<div class="std">
  <span class="title">${title}</span>
  <span class="count">
    <hmnz:metricPrefix value="${counter.sum}" />
    <span class="trend">
      <c:choose>
      <c:when test="${counter.diff > 0}">
		    <span class="up">&#x25B2;</span>
      </c:when>
      <c:when test="${counter.diff < 0}">
		    <span class="down">&#x25BC;</span>
      </c:when>
	  <c:otherwise>
		    <span class="none">-</span>
	  </c:otherwise>
      </c:choose>
    </span>
  </span>
  <span class="max">max <hmnz:metricPrefix value="${counter.max}"/></span>
  <span class="min">min <hmnz:metricPrefix value="${counter.min}"/></span>
</div>
