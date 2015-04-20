<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>

<jsp:include page="../tags/calendario.jsp" />

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${relatorioDiario.veiculo.dadosParaExibicao} - Editar Relat&oacute;rio Di&aacute;rio</h2>
			<jsp:include page="form.jsp" />
		</div>
	</div>
</siga:pagina>