<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<c:if test="${erros}">
	<fmt:message key="${erros}" />
</c:if>

<form id="formEscalasDeTrabalho" method="get,post" onsubmit="return false;" enctype="multipart/form-data">
	<div class="gt-content-box gt-for-table">
		<h3>&nbsp;&nbsp;Escala de Trabalho Vigente</h3>
		<input type="hidden" name="escala.dataVigenciaInicio" value='<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${escala.dataVigenciaInicio.time}"/>' />
		<input type="hidden" name="escala.id" value="${escala.id}" />
		<table id="htmlgridDiasDeTrabalho" class="gt-table">
			<tbody id="tbody">
				<c:forEach items="${escala.diasDeTrabalho}" var="diaDeTrabalho">
					<tr>
						<th width="15%" class="obrigatorio">Dia In&iacute;cio / Fim :</th>
						<td>
							<select name="diaEntrada" >
								<c:forEach items="${diaSemana.values()}" var="diaDeTrabalho">
									<option value="${diaDeTrabalho}">${diaDeTrabalho}</option>
								</c:forEach>
							</select>
							<input type="text" name="horaEntrada" value='<fmt:formatDate pattern="HH:mm" value="${diaDeTrabalho.horaEntrada.time}"/>' size="8" class="hora selecionado" />
							at&eacute;
							<select name="diaSaida">
								<c:forEach items="${diaSemana.values()}" var="diaDeTrabalho">
									<option value="${diaDeTrabalho}">${diaDeTrabalho}</option>
								</c:forEach>
							</select>
							<input type="text" name="horaSaida" value='<fmt:formatDate pattern="HH:mm" value="${diaDeTrabalho.horaSaida.time}"/>' size="8" class="hora selecionado" />
							<input type="hidden" name="id" class="selecionado" value="${diaDeTrabalho.id}" />
						</td>
						<td width="8%"><a class="linkExcluir" name="linkExcluirSelecionados" style="display: inline" onclick="javascript:apagaLinha(this);" href="#"><fmt:message key="views.botoes.excluir" /></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div id="btngridDiasDeTrabalho" class="gt-table-buttons">
			<input type="button" id="btn-Incluir-DiasDeTrabalho" value='<fmt:message key="views.botoes.incluirNovoDia" />' class="gt-btn-medium gt-btn-left btnSelecao" />
		</div>
		<div id="rowDiasDeTrabalho" style="display: none">
			<select name="diaEntrada">
				<c:forEach items="${diaSemana.values()}" var="diaDeTrabalho">
					<option value="${diaDeTrabalho}">${diaDeTrabalho}</option>
				</c:forEach>
			</select>
			<input type="text" name="horaEntrada" value="11:00" size="8" class="hora naoSelecionado" /> 
			at&eacute;
			<select name="diaSaida">
				<c:forEach items="${diaSemana.values()}" var="diaDeTrabalho">
					<option value="${diaDeTrabalho}">${diaDeTrabalho}</option>
				</c:forEach>
			</select>
			<input type="text" name="horaSaida" value="19:00" size="8" class="hora naoSelecionado" />
			<input type="hidden" name="id" class="naoSelecionado" value="${escala.id}" />
		</div>
		<input type="hidden" name="escala.condutor.id" value="${escala.condutor.id}" />
	</div>
	<br />
	<span style="color: red; font-weight: bolder; font-size: smaller;"><fmt:message key="views.erro.preenchimentoObrigatorio" /></span>
	<div class="gt-table-buttons">
		<input type="button" value='<fmt:message key="views.botoes.salvar" />' onclick="submitForm(${linkTo[EscalaDeTrabalhoController].salvar[escalas][escala]})" class="gt-btn-medium gt-btn-left" />
		<input type="button" value='<fmt:message key="views.botoes.finalizar" />' onclick="submitForm(${linkTo[EscalaDeTrabalhoController].finalizar})" class="gt-btn-medium gt-btn-left" />
		<input type="button" value='<fmt:message key="views.botoes.cancelar" />' onclick="javascript:window.location = ${linkTo[CondutorController].lista};" class="gt-btn-medium gt-btn-left" />
	</div>
</form>

<script type="text/javascript">
	function apagaLinha(link) {

		if ($(link).attr('disabled')) {
			return false;
		}

		var html = "";
		if (confirm('Tem certeza de que deseja excluir este dia?')) {
			var trExcluir = link.parentNode.parentNode;
			var tabela = trExcluir.parentNode;
			tabela.removeChild(trExcluir);
		}
	}

	function submitForm(acao) {
		// You can do things here such as validate the form
		// return false; if the form is not valid
		var formulario = document.getElementById("formEscalasDeTrabalho");
		formulario.setAttribute("action", acao);
		var x = 0;
		var inputsDiaDeTrabalho = $('.selecionado');
		for (var i = 0; i < inputsDiaDeTrabalho.length; i++) {
			var nome = inputsDiaDeTrabalho[i].name;
			// 5 - Número de colunas renomeadas por linha de dia de trabalho
			// x - Indice do array de novaEscala.diasDeTrabalho[x] a ser enviado para o servidor
			x = ~~(i / 5);
			var nomeCompleto = "novaEscala.diasDeTrabalho[" + x + "]." + nome;
			inputsDiaDeTrabalho[i].setAttribute("name", nomeCompleto);
		}

		var tbodydiadetrabalho = document.getElementById("tbody");
		var tform = document.getElementById("formEscalasDeTrabalho");
		//	alert(tform.innerHTML);
		formulario.submit();
	}

	function replaceAll(find, replace, str) {
		return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
	}

	function escapeRegExp(str) {
		return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	}

	$(function() {
		$("#btn-Incluir-DiasDeTrabalho").click( function() {
			var divrow = document.getElementById("rowDiasDeTrabalho");
			var htmlNaoSelecionado = divrow.innerHTML;
			var htmlSelecionado = replaceAll("naoSelecionado", "selecionado", htmlNaoSelecionado);
			var html = '<tr>';
			html = html	+ '<th width="15%" class="obrigatorio">Dia Inicio / Fim :</th>';
			html = html + '<td>';
			html = html + htmlSelecionado;
			html = html + '</td>';
			html = html	+ '<td width="8%" ><a class="linkExcluir" name="linkExcluirSelecionados" style="display:inline" onclick="javascript:apagaLinha(this);" href="#">Excluir</a></td>';
			html = html + '</tr>';

			$("#htmlgridDiasDeTrabalho tbody").append(html);
		});
	});
</script>