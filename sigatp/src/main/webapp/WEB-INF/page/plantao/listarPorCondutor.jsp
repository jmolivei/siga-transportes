<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${condutor.dadosParaExibicao}</h2>
			<h3>Plant&otilde;es</h3>

			<c:choose>
				<c:when test="${plantoes.size()>0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table">
							<thead>
								<tr style="font-weight: bold;">
									<th>In&iacute;cio</th>
									<th>Fim</th>
									<th width="5"></th>
									<th width="5"></th>
								</tr>
								
							</thead>
							<tbody>
								<c:forEach items="${plantoes}" var="item">
									<tr>
										<td>${item.getDataHoraInicioFormatada()}</td>
										<td>${item.getDataHoraFimFormatada()}</td>
<%-- 										<td>${item.dataHoraInicio.format("dd/MM/yyyy HH:mm")}aa</td> --%>
<%-- 										<td>${item.dataHoraFim.format("dd/MM/yyyy HH:mm")}aa</td> --%>
										<td><a href="${linkTo[PlantaoController].editar[item.condutor.id][item.id]}">Editar</a></td>
										<td><a href="${linkTo[PlantaoController].excluir[item.id]}"
											onclick="javascript:return confirm('Tem certeza de que deseja excluir este plant\u00E3o?');">Excluir</a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div id="pagination" />
					</div>
				</c:when>
				<c:otherwise>
				<br />
				<h3>N&atilde;o existem plant&otilde;es cadastrados para este
					condutor.</h3>
				</c:otherwise>
			</c:choose>
			<div class="gt-table-buttons">
				<a href="${linkTo[PlantaoController].incluir[idCond]}"
					class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir"/></a>
			</div>
		</div>
	</div>
</siga:pagina>