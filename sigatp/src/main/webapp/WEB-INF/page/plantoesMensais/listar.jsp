<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>

<siga:pagina titulo="Transportes">

<div class="gt-bd clearfix">
<div class="gt-content clearfix">
    <h2>Plant&otilde;es Mensais</h2>
    
    <sigatp:erros/>
    
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
						    <td><a href="${linkTo[PlantoesMensaisController].imprimir}?referencia=${referencia}"><nobr>Visualizar para impress&atilde;o</nobr></a></td>
						    <td><a href="${linkTo[PlantoesMensaisController].editar}?referencia=${referencia}">Editar</a></td>
						    <td><a href="${linkTo[PlantoesMensaisController].excluir}?referencia=${referencia}" onclick="javascript:return confirm('Tem certeza de que deseja excluir TODOS os plantoes de 24h inseridos para este mes?');">Excluir</a></td>
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
	    <a href="${linkTo[PlantoesMensaisController].incluirInicio}" class="gt-btn-medium gt-btn-left"><fmt:message key="views.botoes.incluir"/></a>
	</div>
	    </div>
	</div>

</siga:pagina>