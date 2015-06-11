<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>Cancelar Miss&atilde;o: ${missao.sequence}</h2>
			<sigatp:erros />
			<form method="post" action="${linkTo[MissaoController].cancelarMissao}" enctype="multipart/form-data">
				<div class="gt-content-box gt-form clearfix">
					<div id ="infCancelamento" class="coluna margemDireitaG">
						<label for="missao.justificativa" class="obrigatorio">Justificativa:</label>
			        	<textarea name="missao.justificativa" rows="7" cols="80"></textarea>
					</div>
					<input type="hidden" name="missao" value="${missao.id}"/>
					<br/>
				</div>
				<span style="color: red; font-weight: bolder; font-size: smaller;">
					<fmt:message key="views.erro.preenchimentoObrigatorio" />
				</span>
				<div class="gt-table-buttons">
					<input type="submit" value="<fmt:message key="views.botoes.cancelarMissao" />" class="gt-btn-medium gt-btn-left" />
					<input type="button" value="<fmt:message key="views.botoes.voltar" />" class="gt-btn-medium gt-btn-left" onclick="javascript:window.location = '${linkTo[MissaoController].buscarPelaSequence[false][missao.sequence]}';" />
				</div>
			</form>
		</div>
	</div>
</siga:pagina>