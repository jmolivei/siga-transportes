<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="SIGA - Transporte" > 
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<c:choose>
				<c:when test="${andamentos.size() > 0}">
					<h2>Rela&ccedil;&atilde;o de Andamentos da requisi&ccedil;&atilde;o ${andamentos.get(0).requisicaoTransporte.buscarSequence()} </h2>
					<jsp:include page="../requisicao/menu.jsp" ></jsp:include>
					<div class="gt-content-box gt-for-table">     
					 	<table id="htmlgrid" class="gt-table" >
					    	<tr style="font-weight: bold;">
					    		<th width="10%">Data do Andamento</th>
					    	    <th width="10%">Estado</th>
					    		<th width="30%">Respons&aacute;vel</th>
						   		<th width="20%">Descri&ccedil;&atilde;o</th>
							</tr>
							<c:forEach items="${andamentos}" var="andamento" >
							   	<tr id ="row_${andamento.requisicaoTransporte.id}">	
						    	    <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${andamento.dataAndamento.time}" /></td>
						    		<td style="white-space: pre;">${andamento.estadoRequisicao}</td>
						    	    <td style="white-space: pre;">${andamento.responsavel != null ? andamento.responsavel.siglaCompleta : ""} - ${andamento.responsavel != null ? andamento.responsavel.nomePessoaAI : ""}</td>
						    		<td style="white-space: pre;">${andamento.descricao}</td>
								</tr>
							</c:forEach>
					     </table>   
					</div>
				</c:when>
				<c:otherwise>
					<h2>Rela&ccedil;&atilde;o de Andamentos</h2>
					<jsp:include page="../requisicao/menu.jsp" ></jsp:include>
				</c:otherwise>
			</c:choose>
	   </div>
	</div>
</siga:pagina>