<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>

<jsp:include page="../tags/calendario.jsp" />
<jsp:include page="../tags/decimal.jsp" />

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Abastecimentos</h2>

			<sigatp:erros />

			<c:choose>
				<c:when test="${abastecimentos.size() > 0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table">
							<thead>
								<tr>
									<th>Data e Hora</th>
									<th>Tipo</th>
									<th>Qtd.(l)</th>
									<th>Pre&ccedil;o/litro (R$)</th>
									<th>Valor da NF (R$)</th>
									<th>Ve&iacute;culo</th>
									<th>Condutor</th>
									<th>Nota Fiscal</th>
									<th width="5%"></th>
									<th width="5%"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${abastecimentos}" var="abastecimento">
									<tr>
										<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${abastecimento.dataHora.time}" /></td>
										<td>${abastecimento.tipoDeCombustivel.descricao}</td>
										<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.quantidadeEmLitros)}</td>
										<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.precoPorLitro)}</td>
										<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.valorTotalDaNotaFiscal)}</td>
										<td>${abastecimento.veiculo.placa}</td>
										<td>${abastecimento.condutor.dadosParaExibicao}</td>
										<td>${abastecimento.numeroDaNotaFiscal}</td>
										<td><a
											href="${linkTo[AbastecimentoController].editar[abastecimento.id]}">Editar</a></td>
										<td><a class="lnkMotivoLog"
											href="${linkTo[AbastecimentoController].excluir[abastecimento.id]}" onclick="javascript:return confirm('Tem certeza de que deseja excluir este abastecimento?');">Excluir</a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div id="pagination" />
					</div>
				</c:when>
				<c:otherwise>
					<br />
					<h3>N&atilde;o existem abastecimentos cadastrados.</h3>
				</c:otherwise>
			</c:choose>

			<div class="gt-table-buttons">
				<a href="${linkTo[AbastecimentoController].incluir}"
					id="botaoIncluirAbastecimento" class="gt-btn-medium gt-btn-left"><fmt:message
						key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>