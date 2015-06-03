<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>

<%-- #{extends 'main.html' /} --%>

<siga:pagina titulo="SIGA::Transportes">

    <div class="gt-bd clearfix">
    <div class="gt-content clearfix">
        <h2>Plant&otilde;es Mensais</h2>
        
<%-- 	#{erros}#{/erros} --%>

	<c:choose>
	   <c:when test="${not empty referencias}">
	        <div class="gt-content-box gt-for-table">     
	            <table id="htmlgrid" class="gt-table">
	            <thead>
	                <tr style="font-weight: bold;">
	                    <th>Refer&ecirc;ncia</th>
	                    <th width="5"></th>
	                    <th width="5"></th>
	                    <th width="5"></th>
	                </tr>
	            </thead>
	            <tbody>
	               <c:forEach items="${referencias}" var="referencia"> 
						<tr>
						    <td>${referencia}</td>
						    <td><a href="@{PlantoesMensais.imprimir(referencia)}"><nobr>Visualizar para impress&atilde;o</nobr></a></td>
						    <td><a href="@{PlantoesMensais.editar(referencia)}">Editar</a></td>
						    <td><a href="@{PlantoesMensais.excluir(referencia)}" onclick="javascript:return confirm('Tem certeza de que deseja excluir TODOS os plantoes de 24h inseridos para este mes?');">Excluir</a></td>
                        </tr>
	               </c:forEach>
	             </tbody>
	             </table> 
                 <div id="pagination"/>   
	        </div>
	   </c:when>
	   <c:otherwise>
    	   <br/><h3>N&atilde;o existem plant&otilde;es mensais cadastrados.</h3>
    	</c:otherwise>
	</c:choose>
	<div class="gt-table-buttons">
	    <a href="@{PlantoesMensais.incluirInicio()}" class="gt-btn-medium gt-btn-left">${views.botoes.incluir}</a>
	</div>
	    </div>
	</div>

</siga:pagina>