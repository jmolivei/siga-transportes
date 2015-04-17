<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Rela&ccedil;&atilde;o de Veiculos</h2>
			
			<sigatp:erros></sigatp:erros>

			<c:if test="${veiculos.size() > 0}">
				<div class="gt-content-box gt-for-table">     
				 	<table id="htmlgrid" class="gt-table" >
					 	<thead>
					    	<tr style="font-weight: bold;">
					    	    <th width="5%">Placa</th>
						   		<th width="5%">Ano</th>
						   		<th width="15%">Modelo</th>
						   		<th width="15%">Grupo</th>
						   		<th width="20%">Lotacao</th>
						   		<th width="10%">Situacao</th>
								<th width="5%"></th>
								<th width="5%"></th>
							</tr>
						</thead>
						<tbody>	
							<c:forEach items="${veiculos}" var="veiculo">
							   	<tr>	
						    	    <td>${veiculo.placa}</td>
						    		<td>${veiculo.anoFabricacao}</td>
						    		<td style="white-space: pre;">${veiculo.modelo}</td>
						    		<td style="white-space: pre;">${veiculo.grupo.nome}</td>
						    		<td style="white-space: pre;">${veiculo.lotacoes[0].lotacao.descricaoAmpliada}</td>
						    		<td style="white-space: pre;">${veiculo.situacao}</td>		    		
						    		<td><a href="${linkTo[VeiculoController].editar[veiculo.id]}"><fmt:message key="views.botoes.editar"/></a></td>
						    		<td><a onclick="javascript:return confirm('Tem certeza de que deseja excluir este veiculo ?');" href="${linkTo[VeiculoController].excluir[veiculo.id]}"><fmt:message key="views.botoes.excluir"/></a></td>
								</tr>
							</c:forEach>
						</tbody>	
				     </table>
		   		    <div id="pagination"></div>    
				</div>
			</c:if>
			<div class="gt-table-buttons">
				<a href="${linkTo[VeiculoController].incluir}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir" /></a>
			</div>
		</div>
	</div>
</siga:pagina>
