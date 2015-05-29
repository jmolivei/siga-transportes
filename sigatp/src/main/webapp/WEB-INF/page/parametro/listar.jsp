<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Parametriza&ccedil;&atilde;o do Sistema</h2>

			<sigatp:erros />

			<c:choose>
				<c:when test="${parametros.size() > 0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table">
							<thead>
								<tr>
									<th>Nome do Par&acirc;metro</th>
									<th>Valor do Par&acirc;metro</th>
									<th>Lota&ccedil;&atilde;o</th>
									<th>Servidor</th>
									<th>Org&atilde;o do Usu&aacute;rio</th>
									<th>Complexo</th>
									<th>In&iacute;cio de Vig&ecirc;ncia</th>
									<th>Fim de Vig&ecirc;ncia</th>
									<th width="5%"></th>
									<th width="5%"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${parametros}" var="item">
									<tr>
										<td>${item.nomeParametro}</td>
										<td>${item.valorParametro}</td>
										<td>${item.dpLotacao != null ? item.dpLotacao.nomeLotacao : ""}</td>
										<td>${item.dpPessoa != null ? item.dpPessoa.nomePessoa: ""}</td>
										<td>${item.cpOrgaoUsuario != null ? item.cpOrgaoUsuario.nmOrgaoUsu : ""}</td>
										<td>${item.cpComplexo != null ? item.cpComplexo.nomeComplexo: ""}</td>
										<td>
											<c:if test="${item.dataInicio != null}">
												<fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataInicio.time}" />
										   	</c:if>
									   	</td>
										<td>
											<c:if test="${item.dataFim != null}">
												<fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataFim.time}" />
											</c:if>
										</td>
										<td><a href="${linkTo[ParametroController].editar[item.id]}">Editar</a></td>
										<td><a
											onclick="javascript:return confirm('Tem certeza de que deseja excluir os dados desta parametro?');"
											href="${linkTo[ParametroController].excluir[item.id]}">Excluir</a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div id="pagination" />
					</div>
				</c:when>
				<c:otherwise>
					<br />
					<h3>N&atilde;o existem parametros cadastrados.</h3>
				</c:otherwise>
			</c:choose>
			<div class="gt-table-buttons">
				<a href="${linkTo[ParametroController].incluir}" id="botaoIncluirParametro"
					class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>