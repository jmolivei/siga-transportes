<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>

<c:if test="${erros}">
	<fmt:message key="${erros}" />
</c:if>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2><fmt:message key="finalidades" /></h2>
			<%-- #{include 'Finalidades/menu.html' /} --%>
			<%-- #{if finalidades.size() > 0} --%>
			<c:choose>
				<c:when test="${finalidades.size() > 0}">
					<div class="gt-content-box gt-for-table">
						<table id="htmlgrid" class="gt-table" >
					    	<thead>
					    		<tr>
					    	    	<th width="90%">Descri&ccedil;&atilde;o</th>
						   			<th width="5%"></th>
						   			<th width="5%"></th>
								</tr>
							</thead>
							<tbody>
								<%-- #{list finalidades, as:'item'} --%>
								<c:forEach items="${finalidades}" var="item">
							   		<tr>
						    	    	<td>${item.descricao}</td>
						    			<td><a href="${linkTo[FinalidadeController].editar[item.id]}"><fmt:message key="views.botoes.editar" /></a></td>
						    			<td><a href="${linkTo[FinalidadeController].excluir[item.id]}" onclick="javascript:return confirm('Tem certeza de que deseja excluir os dados desta finalidade?');"><fmt:message key="views.botoes.excluir" /></a></td>
									</tr>
								</c:forEach>
							</tbody>
					    </table>
						<div id="pagination"/> 
					</div>
				</c:when>
				<c:otherwise>
					<br/>
					<h3>N&atilde;o existem finalidades cadastradas.</h3>
				</c:otherwise>
			</c:choose>
			
			<div class="gt-table-buttons">
				<a href="${linkTo[FinalidadeController].editar[0]}" id="botaoIncluirFinalidade" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>