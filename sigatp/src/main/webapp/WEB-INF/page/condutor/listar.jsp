<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="siga" uri="http://localhost/jeetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Condutores</h2>

			<c:choose>
				<c:when test="${condutores.size() > 0}">
					<div class="gt-content-box gt-for-table">

						<table class="gt-table" width="100%">
							<tr class="header">
								<th align="center" width="7%">Matr.</th>
								<th width="25%">Nome</th>
								<th width="15%">Tel. Inst.</th>
								<th width="15%">Tel. Part.</th>
								<th width="20%">CNH</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
							<c:forEach items="${condutores}" var="item">
								<tr class="${evenorodd}">
									<td><c:if test="${not empty item.matricula}">${item.matricula}</c:if></td>
									<td><c:if test="${not empty item.nomePessoaAI}">${item.nomePessoaAI}</c:if></td>
									<td><c:if test="${not empty item.telefoneInstitucional}">${item.telefoneInstitucional}</c:if></td>
									<td><c:if
											test="${not empty item.telefonePessoal or not empty item.celularPessoal}">${item.telefonePessoal}<br />${item.celularPessoal}</c:if></td>
									<td><c:if test="${not empty item.categoriaCNH.toString()}">
		    								${item.categoriaCNH.toString()}
		  									<br />
											<c:if test="${item.vencimentoCNHExpirado}">
												<span style="color: red; font-weight: bolder;"> <fmt:formatDate
														pattern="dd/MM/yyyy"
														value="${item.dataVencimentoCNH.time}" />
												</span>
											</c:if>
										</c:if></td>
									<td>
										<a href="${linkTo[CondutorController].edita[item.id]}">Editar</a>
									</td>
									<td>
										<a href="${linkTo[CondutorController].exclui[item.id]}"
										   onclick="javascript:return confirm('Tem certeza de que deseja excluir os dados deste condutor?');">Excluir</a>
									</td>
								</tr>
							</c:forEach>
						</table>
						<div id="pagination" />
					</div>
				</c:when>
				<c:otherwise>
					<br/><h3>N&atilde;o existem condutores cadastrados.</h3>
				</c:otherwise>
			</c:choose>
			<div class="gt-table-buttons">
				<a href="${linkTo[CondutorController].inclui}" id="botaoIncluirCondutor"
					class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.salvar"/></a>
			</div>
		</div>
	</div>
</siga:pagina>

