<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2><fmt:message key="${action}" /> <fmt:message key="finalidades" /></h2>
			<form id="formFinalidade" action="${linkTo[FinalidadeController].salvar[finalidade]}" method="post" enctype="multipart/form-data">
				<div class="gt-content-box gt-form"> 
					<label for="finalidade.descricao" class="obrigatorio">Descri&ccedil;&atilde;o:</label>
					<input type="text" name="finalidade.descricao" value="${finalidade.descricao}" />
			     	<input type="hidden" name="finalidade.id" value="${finalidade.id}"/>
				</div>
				<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio" /></span>
				<div class="gt-table-buttons">
					<input type="submit" value="<fmt:message key="views.botoes.salvar" />" class="gt-btn-medium gt-btn-left" />
					<input type="button" value="<fmt:message key="views.botoes.cancelar" />" class="gt-btn-medium gt-btn-left" onclick='javascript:window.location = "${linkTo[FinalidadeController].listar}";' />
				</div>
			</form>
		</div>
	</div>
</siga:pagina>