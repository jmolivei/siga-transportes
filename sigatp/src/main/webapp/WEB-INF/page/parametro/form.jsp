<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<!-- <script src="@{'/public/javascripts/jquery/jquery-ui-1.8.16.custom.min.js'}"></script> -->
<!-- <script src="@{'/public/javascripts/jquery/jquery-1.6.4.min.js'}"></script> -->

<sigatp:erros />
<sigatp:calendario/>
<sigatp:decimal />

<siga:pagina>
	<form id="formParametro" action="${linkTo[ParametroController].salvar}" enctype="multipart/form-data">
		<div class="gt-content-box gt-form"> 
			<label for="parametro.nomeParametro" class="obrigatorio">Nome do Par&acirc;metro:</label>
			<input type="text" name="parametro.nomeParametro" value="${parametro.nomeParametro}" />
	
			<label for="parametro.valorParametro" class="obrigatorio">Valor do Par&acirc;metro:</label>
			<input type="text" name="parametro.valorParametro" value="${parametro.valorParametro}" />
		    
		    <label for= "parametro.dpLotacao.id">Lota&ccedil;&atilde;o:</label>
					<siga:selecao tipo="lotacao" propriedade="lotacao" tema="simple" modulo="siga" urlAcao="buscar" siglaInicial="${parametro.dpLotacao}" idInicial="${parametro.dpLotacao.id}" descricaoInicial="${parametro.dpLotacao.descricao}"/>
<%-- 					#{selecao 	tipo:'lotacao',  --%>
<%-- 								nome:'parametro.dpLotacao',  --%>
<%-- 								value:parametro?.dpLotacao/}  --%>
				
				<label for="parametro.dpPessoa.id">Servidor: </label>
					<siga:selecao tipo="pessoa" propriedade="pessoa" tema="simple" modulo="siga" urlAcao="buscar" siglaInicial="${parametro.dpPessoa}" idInicial="${parametro.dpPessoa.id}" descricaoInicial="${parametro.dpPessoa.descricao}"/>
<%-- 					#{selecao tipo:'pessoa',nome:'parametro.dpPessoa', value:parametro?.dpPessoa/}							 --%>
		    
		    
		    	<label for="parametro.cpOrgaoUsuario.idOrgaoUsu">Org&atilde;o do Usu&aacute;rio:</label>
		    		<siga:select id="comboorgao" name="parametro.cpOrgaoUsuario?" list="cpOrgaoUsuarios" listKey="idOrgaoUsu" listValue="nmOrgaoUsu" value="${parametro.cpOrgaoUsuario.idOrgaoUsu}" headerKey="0" headerValue=""/>
<%-- 					#{select 'parametro.cpOrgaoUsuario.idOrgaoUsu', --%>
<%-- 							id: 'comboorgao', --%>
<%-- 					        items:cpOrgaoUsuarios, --%>
<%-- 				    	    labelProperty:'nmOrgaoUsu', --%>
<%-- 				    	    value:parametro.cpOrgaoUsuario?.idOrgaoUsu} --%>
<%-- 				    	    #{option ''} #{/option} --%>
<%-- 					#{/select}   --%>
					
		    	<label for= "parametro.cpComplexo">Complexo:</label>	
	
				<select name="parametro.cpComplexo.idComplexo" size="1" >
					<option value=""></option>
					<c:forEach items="${cpComplexos}" var="cpComplexo">
						<c:choose>
						<c:when test="${parametro.cpComplexo != null && cpComplexo.idComplexo == parametro.cpComplexo.idComplexo}">
	   						<option value="${cpComplexo.idComplexo}" selected=selected>${cpComplexo.nomeComplexo}</option>
						</c:when>
						<c:otherwise>
							<option value="${cpComplexo.idComplexo}">${cpComplexo.nomeComplexo}</option>
						</c:otherwise>
						</c:choose>
					</c:forEach>
	   			</select>
	
	   			<label for="parametro.dataInicio">In&iacute;cio de Vig&ecirc;ncia:</label>
		    	<input type="text" name="parametro.dataInicio" class="datePicker" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataInicio.time}" />" />
		    
			    <label for="parametro.dataFim">Fim de Vig&ecirc;ncia</label>
			    <input type="text" name="parametro.dataFim" class="datePicker" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataFim.time}" />" />
			    
			    <label for="parametro.descricao" class="obrigatorio">Descri&ccedil;&atilde;o</label>
				<textarea name="parametro.descricao" rows="7" cols="80">${parametro.descricao}</textarea>
										
			    <input type="hidden" name="parametro" value="${parametro.id}"/>
			</div>
		<span class="alerta menor">* Preenchimento obrigat&oacute;rio</span>
		<div class="gt-table-buttons">
			<input type="submit" value="Salvar" class="gt-btn-medium gt-btn-left" />
			<input type="button" value="Cancelar" class="gt-btn-medium gt-btn-left" onclick="javascript:window.location = '${linkTo[ParametroController].listar}';" />
		</div>
	</form>
</siga:pagina>