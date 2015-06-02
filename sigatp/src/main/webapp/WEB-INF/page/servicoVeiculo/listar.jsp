<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="SIGA-Transporte">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Servi&ccedil;os de Ve&iacute;culos</h2>
			<jsp:include page="menu.jsp" />
			<c:choose>
				<c:when test="${servicos.size() > 0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table">
							<thead>
							<tr style="font-weight: bold;">
								<th width="15%">N&uacute;mero</th>
								<th width="8%">Situa&ccedil;&atilde;o</th>
								<th width="14%">Req. Transp.</th>
								<th width="8%">Tipo de Servi&ccedil;o</th>
								<th width="20%">Ve&iacute;culo</th>
								<th width="10%">In&iacute;cio</th>
								<th width="10%">T&eacute;rmino</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${servicos}" var="item">
								<tr>
								 	
									<td><nobr><a href="${linkTo[ServicoVeiculoController].buscarServico[false][item.sequence]}">${item.sequence}</a> 
									<a href="#" onclick="javascript:window.open('${linkTo[ServicoVeiculoController].buscarServico[true][item.sequence]}');">
									<img src="/sigatp/public/images/linknovajanelaicon.png" 
									alt="Abrir em uma nova janela" title="Abrir em uma nova janela"></a></nobr></td>				
									<td>${item.situacaoServico}</td>
									<td>${item.requisicaoTransporte != null? item.requisicaoTransporte.buscarSequence() : ""}</td>
									<td>${item.tiposDeServico}</td>
									<td>${item.veiculo != null ? item.veiculo.dadosParaExibicao : ""}</td>
									<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataHoraInicioPrevisto.time}" /></td>
									<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataHoraFimPrevisto.time}" /></td>

									<td width="10%">
									<c:if test="${!item.situacaoServico.equals('REALIZADO') && !item.situacaoServico.equals('CANCELADO')}">
										<a href="${linkTo[ServicoVeiculoController].editar[item.id]}">Editar</a>
									</c:if>	
									</td>
									<td width="10%">
									<c:if test="${item.situacaoServico.equals('AGENDADO')}">
										<a onclick="javascript:return confirm('Tem certeza de que deseja excluir os dados deste servi&ccedil;o?');"
										href="${linkTo[ServicoVeiculoController].excluir[item.id]}">Excluir</a>
									</c:if>	
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					<div id="pagination"/> 
					</div>
				</c:when>
				<c:otherwise>
					<br />
					<h3>N&atilde;o existem servi&ccedil;os cadastrados para este ve&iacute;culo.</h3>
				</c:otherwise>
			</c:choose>
			<div class="gt-table-buttons">
			<tptags:link texto="<fmt:message key="views.botoes.incluir"/>" parteTextoLink="<fmt:message key="views.botoes.incluir"/>" comando="${linkTo[ServicoVeiculoController].incluir}">
			  	<a href="${linkTo[ServicoVeiculoController].incluir}" 
					class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir"/></a>
			</tptags:link> 
			</div>
		</div>
	</div>
</siga:pagina>