<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${condutor.dadosParaExibicao} - Rela&ccedil;&atilde;o de Escalas de Trabalho</h2>
			<jsp:include page="../condutor/menu.jsp" />
			<c:choose>
				<c:when test="${escalas.size()>0}">
						<div class="gt-content-box gt-for-table">     
						 	<table id="htmlgrid" class="gt-table">
						 		<thead>
							    	<tr style="font-weight: bold;">
							    	    <th>In&iacute;cio Vig&ecirc;ncia</th>
								   		<th>Fim Vig&ecirc;ncia</th>
								   		<th>Escala</th>
								   		<th width="5"></th>
								   		<th width="5"></th>
									</tr>
						 		</thead>
						 		<tbody>
									<c:forEach items="${escalas}" var="escala">
									   	<tr>
								    	    <td>${escala.dataVigenciaInicio?.format("dd/MM/yyyy")}</td>
								    	    <td>${escala.dataVigenciaFim?.format("dd/MM/yyyy")}</td>
								    		<td>${escala.escalaParaExibicao}</td>
								    		<td><a href="${linkTo[EscalaDeTrabalhoController].editar[escala.id]}"><fmt:message key="views.botoes.editar" /></a></td>
								    		<td><a href="${linkTo[EscalaDeTrabalhoController].excluir[escala.id]}" onclick="javascript:return confirm('Tem certeza de que deseja excluir esta escala?');"><fmt:message key="views.botoes.excluir" /></a></td>
										</tr>
									</c:forEach>
						 		</tbody>
						     </table>   
						</div>
				</c:when>
				<c:otherwise>
					<br/>
					<h3>N&atilde;o existem escalas cadastradas para este condutor.</h3>
				</c:otherwise>
			</c:choose>
			<div class="gt-table-buttons">
				<a href="${linkTo[EscalaDeTrabalhoController].incluir[condutor.id]}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>

<jsp:include page="../tags/calendario.jsp"/>