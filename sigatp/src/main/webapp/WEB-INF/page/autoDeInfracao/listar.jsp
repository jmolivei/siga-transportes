<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Lista de Autos de Infra&ccedil;&atilde;o</h2>
			
			<sigatp:erros />
			
			<c:if test="${autosDeInfracao.size() > 0}">
				<div class="gt-content-box gt-for-table">
					<table id="htmlgrid" class="gt-table">
						<thead>
							<tr>
								<th>Data e Hora</th>
								<th>Ve&iacute;culo</th>
								<th>Condutor</th>
								<th>Descri&ccedil;&atilde;o</th>
								<th>Nº Proc. Recurso</th>
								<th>Pago?</th>
								<th>Recebido?</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${autosDeInfracao}" var="autoDeInfracao">
								<tr>
									<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${autoDeInfracao.dataHora.time}"/></td>
									<td>${autoDeInfracao.veiculo.dadosParaExibicao}</td>
									<td>${autoDeInfracao.condutor.dadosParaExibicao}</td>
									<td>${autoDeInfracao.descricao}</td>
									<td>${autoDeInfracao.numeroDoProcesso}</td>
									<td>${autoDeInfracao.foiPago().descricao}</td>
									<td>${autoDeInfracao.foiRecebido.descricao}</td>
									<td><a href="${linkTo[AutoDeInfracaoController].editar[autoDeInfracao.id]}"><fmt:message key="views.botoes.editar"/></a></td>
									<td><a href="${linkTo[AutoDeInfracaoController].excluir[autoDeInfracao.id]}" onclick="javascript:return confirm('Tem certeza de que deseja excluir este auto de infra&ccedil;&atilde;o?');"><fmt:message key="views.botoes.excluir" /></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div id="pagination" />
				</div>
			</c:if>

			<div class="gt-table-buttons">
				<a href="${linkTo[AutoDeInfracaoController].incluir['AUTUACAO']}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluirAutuacao"/></a>
				<a href="${linkTo[AutoDeInfracaoController].incluir['PENALIDADE']}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluirPenalidade"/></a>
			</div>
		</div>
	</div>
</siga:pagina>