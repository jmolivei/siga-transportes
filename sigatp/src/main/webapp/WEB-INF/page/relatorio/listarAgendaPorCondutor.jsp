<%-- #{extends 'main.html' /} --%>
<%-- #{calendario}#{/calendario} --%>
<%-- #{set title:'SIGA::Transportes' /} --%>


<!--     %{ -->
<!--          entidade = "Condutor"  -->
<!-- 	}% -->

<%-- #{include 'Relatorios/timeline.html' /} --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>

<siga:pagina>
	<jsp:include page="timeline.jsp" />
</siga:pagina>