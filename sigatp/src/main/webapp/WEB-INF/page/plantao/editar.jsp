<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>

<script type="text/javascript" language="Javascript1.1">
function verificaCampos(){
	var dataHoraInicio = document.getElementById("dataHoraInicio").value;
	var dataHoraFim = document.getElementById("dataHoraFim").value;
		
	if (dataHoraInicio == "" || dataHoraFim == ""){
		alert("Uma ou mais datas estão sem preenchimento.");
		return false;
	}
	else{
		dataHoraInicio = new Date(dataHoraInicio);
		dataHoraFim = new Date(dataHoraFim);
		if (dataHoraInicio > dataHoraFim) {
			alert("A data e hora do fim do plantão são anteriores ao início.");
			return false;
		}
		return true;
	}	
}
</script>
<siga:pagina titulo="Transportes">
	<div class="gt-content clearfix">
		<h2>${plantao.condutor.dadosParaExibicao} - ${plantao.id > 0 ? "Editar": "Inserir" } Plant&atilde;o</h2>
		<form name="formPlantoes" id="formPlantoes" action="${linkTo[PlantaoController].salvar}" method="post" cssClass="form">
			<input type="hidden" name="plantao.id" value="${plantao.id}" />
			<input type="hidden" name="plantao.condutor.id" value="${plantao.condutor.id}">
			<div class="gt-content-box gt-form clearfix">
				<label for="plantao.dataHoraInicio" class= "obrigatorio">In&iacute;cio</label>
				<input type="text" id="dataHoraInicio" name="plantao.dataHoraInicio" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${plantao.dataHoraInicio.time}" />" size="14" class="dataHora" />
		   		<input type="hidden" id="dataHoraInicioNova" name="plantao.dataHoraInicioNova" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${plantao.dataHoraInicio.time}" />" size="14" class="dataHora" />
				<label for="plantao.dataHoraFim" class= "obrigatorio">Fim</label>
				<input type="text" id="dataHoraFim" name="plantao.dataHoraFim" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${plantao.dataHoraFim.time}" />" size="14" class="dataHora" />
				<input type="hidden" id="dataHoraFimNova" name="plantao.dataHoraFimNova" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${plantao.dataHoraFim.time}" />" size="14" class="dataHora" />
			</div>
			<span class="alerta menor">* Preenchimento obrigat&oacute;rio</span>
			<div class="gt-table-buttons">
<!-- 			<input type="submit" value="Salvar" class="gt-btn-medium gt-btn-left" onClick="return verificaCampos()" /> -->
		 		<input type="submit" value="<fmt:message key="views.botoes.salvar"/>" class="gt-btn-medium gt-btn-left" />
				<input type="button" value="<fmt:message key="views.botoes.cancelar"/>" onClick="javascript:window.location = '${linkTo[PlantaoController].listarPorCondutor[plantao.condutor.id]}'" class="gt-btn-medium gt-btn-left" />
			</div>
		</form>
	</div>
</siga:pagina>



