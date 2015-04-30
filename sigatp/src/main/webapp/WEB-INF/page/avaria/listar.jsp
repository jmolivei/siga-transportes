<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2><fmt:message key="avarias" /></h2>
			<c:choose>
				<c:when test="${avarias.size() > 0}">
					<div class="gt-content-box gt-for-table">
				 		<table id="htmlgrid" class="gt-table" >
				 			<thead>
						    	<tr style="font-weight: bold;">
						    	    <th width="15%">Ve&iacute;culo</th>
							   		<th width="10%">Data Registro</th>
							   		<th width="10%">Data Solu&ccedil;&atilde;o</th>
							   		<th>Descri&ccedil;&atilde;o</th>
							   		<th width="5%"></th>
							   		<th width="5%"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${avarias}" var="item">
							   	<tr>
						    	    <td><a href="${linkTo[AvariaController].listarPorVeiculo[item.veiculo.id]}" alt="Veja apenas as avarias desde ve&iacute;culo">${(null != item.veiculo) ? item.veiculo.dadosParaExibicao : ""}</a></td>
						    		<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataDeRegistro.time}"/></td>
						    		<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataDeSolucao.time}"/></td>
						    		<td style="white-space: pre-line;">${item.descricao}</td>
						    		<td><a href="${linkTo[AvariaController].editar[item.veiculo.id][item.id][false]}"><fmt:message key="views.botoes.editar" /></a></td>
						    		<td><a onclick="javascript:return confirm('Tem certeza de que deseja excluir esta avaria?');" href="${linkTo[AvariaController].excluir[item.id][false]}"><fmt:message key="views.botoes.excluir" /></a></td>
								</tr>
								</c:forEach>
							</tbody>
				     	</table>
			     		<div id="pagination"/>
					</div>
				</c:when>
				<c:otherwise>
					<br/>
					<h3>N&atilde;o existem avarias cadastradas.</h3>
				</c:otherwise>
			</c:choose>
		
			<div class="gt-table-buttons">
				<a href="${linkTo[AvariaController].incluir}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>