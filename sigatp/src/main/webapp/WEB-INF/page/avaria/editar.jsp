<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>
				<c:if test="${fixarVeiculo}">
					${avaria.veiculo.dadosParaExibicao} - 
				</c:if>
				${modo} <fmt:message key="avaria" />
			</h2>
			<c:if test="${fixarVeiculo}">
				<jsp:include page="../veiculo/menu.jsp" />
			</c:if>
			<sigatp:erros />
			<form id="formAvarias" action="${linkTo[AvariaController].salvar}" method="post" enctype="multipart/form-data">
				<div class="gt-content-box gt-form clearfix">
					<c:choose>
						<c:when test="${fixarVeiculo}">
			 				<input type="hidden" name="avaria.veiculo.id" value="${avaria.veiculo.id}"/>
						</c:when>
						<c:otherwise>
					       	<label for="avaria.veiculo.id">Ve&iacute;culo</label>
					       	<select name="avaria.veiculo.id">
					       		<c:forEach items="${veiculos}" var="veiculo">
					       			<option value="${veiculo.id}">${veiculo.dadosParaExibicao}</option>
					       		</c:forEach>
					       	</select>
						</c:otherwise>	
					</c:choose>
					
       				<label for="avaria.dataDeRegistro" class= "obrigatorio">Data de Registro</label>
       				<input type="text" name="avaria.dataDeRegistro" class="datePicker" value='<fmt:formatDate pattern="dd/MM/yyyy" value="${avaria.dataDeRegistro.time}"/>' />
       				
       				<label for="avaria.descricao" class= "obrigatorio">Descri&ccedil;&atilde;o</label>
       				<textarea name="avaria.descricao" rows="7" cols="80">${avaria.descricao}</textarea>
       				
       				<label for="avaria.podeCircular" class= "obrigatorio">Pode Circular?</label>
       				<select name="avaria.podeCircular">
       					<c:forEach items="${avaria.podeCircular.values()}" var="opcao">
       						<option value="${opcao}" ${opcao == avaria.podeCircular ? 'selected' : ''} }>${opcao.descricao}</option>
       					</c:forEach>
       				</select>
       				
        			<input type="hidden" name="avaria" value="${avaria.id}"/>
        			<input type="hidden" name="fixarVeiculo" value="${fixarVeiculo}"/>
				</div>
				<span class="alerta menor">* Preenchimento obrigat&oacute;rio</span>
				<div class="gt-table-buttons">
					<input type="submit" value="<fmt:message key="views.botoes.salvar" />" class="gt-btn-medium gt-btn-left" />
					<input type="button" value="<fmt:message key="views.botoes.cancelar" />" class="gt-btn-medium gt-btn-left" 
						onclick="javascript:window.location = '<c:choose> <c:when test="${fixarVeiculo}">${linkTo[AvariaController].listarPorVeiculo[avaria.veiculo.id]} </c:when> <c:otherwise>${linkTo[AvariaController].listar}</c:otherwise></c:choose>'">
					</input>
				</div>
			</form>
		</div>
	</div>
</siga:pagina>

<jsp:include page="../tags/calendario.jsp"/>