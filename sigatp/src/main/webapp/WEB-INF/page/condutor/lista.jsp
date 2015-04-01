<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

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
									<td><c:set var="editarUrl">
											<c:url value="/app/condutor/editar">
												<c:param name="id" value="${item.id}" />
											</c:url>
										</c:set> <a href="${editarUrl}">Editar</a></td>
									<td><c:set var="urlExcluir">
											<c:url value="/app/condutor/excluir">
												<c:param name="id" value="${item.id}" />
											</c:url>
										</c:set> <a
										onclick="javascript:return confirm('Tem certeza de que deseja excluir os dados deste condutor?');"
										href="${urlExcluir}">Excluir</a></td>
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
				<c:url var="urlIncluir" value="/app/condutor/incluir" />
				<a href="${urlIncluir}" id="botaoIncluirCondutor"
					class="gt-btn-medium gt-btn-left">Incluir</a>
			</div>
		</div>
	</div>
</siga:pagina>

