<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<jsp:include page="../tags/calendario.jsp" />
<sigatp:erros/>

<script>
function verificaCampos(){
	
	var dataHora = document.getElementById("dataHora").value;
	var codigoDaAutuacao = document.getElementById("codigoDaAutuacao").value;
	var codigoDaPenalidade = document.getElementById("codigoDaPenalidade").value;
	var descricao = document.getElementById("descricao").value;
	var enquadramento = document.getElementById("enquadramento").value;
	var local = document.getElementById("local").value;
	var valor = document.getElementById("valor").value;
	var quantidadeDePontos = document.getElementById("quantidadeDePontos").value;
	var dataDeVencimento = document.getElementById("dataDeVencimento").value;
	var codigoNotificacao = "";

	if (dataHora == "" || 
		codigoDaAutuacao == "" ||
		codigoDaPenalidade == "" ||
		descricao == "" ||
		enquadramento == "" ||
		local == "" ||
		valor == "" ||
		quantidadeDePontos == "" ||
		dataDeVencimento == ""){
			alert("Um ou mais campos obrigatÃ³rios estÃ£o sem preenchimento.");
			return false;
	}
	return true;

}
</script>

<form name="formAutosDeInfracao" id="formAutosDeInfracao" action="${linkTo[AutoDeInfracaoController].salvar}" method="post" cssClass="form">
	<div class="gt-content-box gt-form clearfix">
		<div class="coluna margemDireitaG">
	       	<label for="autoDeInfracao.dataHora" class= "obrigatorio">Data e Hora</label>
			<input type="text" id="dataHora" name="autoDeInfracao.dataHora" size="16" class="dataHora" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${autoDeInfracao.dataHora.time}"/>"/> 
			<label for="autoDeInfracao.veiculo.id" class= "obrigatorio">Ve&iacute;culo</label>
	       	<select name="autoDeInfracao.veiculo.id">
	       		<c:forEach items="${veiculos}" var="veiculo">
	       			<option value="${veiculo.id}">${veiculo.dadosParaExibicao}</option>
	       		</c:forEach>
	       	</select>

			<label for="autoDeInfracao.condutor.id" class= "obrigatorio">Condutor</label>
	       	<select name="autoDeInfracao.condutor.id">
	       		<c:forEach items="${condutores}" var="condutor">
	       			<option value="${condutor.id}">${condutor.dadosParaExibicao}</option>
	       		</c:forEach>
	       	</select>
			<label for="autoDeInfracao.local" class= "obrigatorio">Local</label>
			<input type="text" id="local" name="autoDeInfracao.local" size="46" value="${autoDeInfracao.local}"/>
		</div>
		<div class="coluna margemDireitaG">
			<c:if test="${tipoNotificacao.toString().contains('AUTUACAO')}">
				<label for="autoDeInfracao.codigoDaAutuacao" class= "obrigatorio">C&oacute;d. da Autua&ccedil;&atilde;o</label>
				<input type="text" id="codigoDaAutuacao" name="autoDeInfracao.codigoDaAutuacao" value="${autoDeInfracao.codigoDaAutuacao}" />
				<input type="hidden" id="codigoDaPenalidade" name="autoDeInfracao.codigoDaPenalidade" value="0" />
			</c:if>
			<c:if test="${tipoNotificacao.toString().contains('PENALIDADE')}">
				<label for="autoDeInfracao.codigoDaPenalidade" class= "obrigatorio">C&oacute;d. da Penalidade</label>
				<input type="text" id="codigoDaPenalidade" name="autoDeInfracao.codigoDaPenalidade" value="${autoDeInfracao.codigoDaPenalidade}" />
				<input type="hidden" id="codigoDaAutuacao" name="autoDeInfracao.codigoDaAutuacao" value="0" />
			</c:if>
			<label for="autoDeInfracao.descricao" class= "obrigatorio">Descri&ccedil;&atilde;o</label>
			<input type="text" id="descricao" name="autoDeInfracao.descricao" size="46" value="${autoDeInfracao.descricao}" />
			<label for="autoDeInfracao.enquadramento" class= "obrigatorio">Enquadramento</label>
			<input type="text" id="enquadramento" name="autoDeInfracao.enquadramento" value="${autoDeInfracao.enquadramento}" />
			<label for="autoDeInfracao.gravidade" class= "obrigatorio">Gravidade</label>
	       	<select name="autoDeInfracao.gravidade">
	       		<c:forEach items="${autoDeInfracao.gravidade.values()}" var="gravidade">
	       			<option value="${gravidade}">${gravidade}</option>
	       		</c:forEach>
	       	</select>
			<label for="autoDeInfracao.quantidadeDePontos" class= "obrigatorio">Qtd. de Pontos</label>
			<input type="text" id="quantidadeDePontos" name="autoDeInfracao.quantidadeDePontos" size="1" value="${autoDeInfracao.quantidadeDePontos}" />
		</div>
		<div class="coluna">
			<label for="autoDeInfracao.recebido" class= "obrigatorio">Recebido?</label>
	       	<select name="autoDeInfracao.recebido">
	       		<c:forEach items="${autoDeInfracao.foiRecebido.values()}" var="foiRecebido">
	       			<option value="${foiRecebido}">${foiRecebido.descricao}</option>
	       		</c:forEach>
	       	</select>
			<label for="autoDeInfracao.numeroDoProcesso">N&uacute;mero do Processo</label>
			<input type="text" id="numeroDoProcesso" name="autoDeInfracao.numeroDoProcesso" value="${autoDeInfracao.numeroDoProcesso}" />			
			<label for="autoDeInfracao.dataDeVencimento" class= "obrigatorio">Vencimento</label>
			<input type="text" id="dataDeVencimento" name="autoDeInfracao.dataDeVencimento" size="8" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${autoDeInfracao.dataDeVencimento.time}"/>" class="datePicker"/>
			<label for="autoDeInfracao.valor" class= "obrigatorio">Valor</label>
			<input type="text" id="valor" name="autoDeInfracao.valor" value="${autoDeInfracao.valor}" class="decimal"/>
			<label for="autoDeInfracao.valorComDesconto">Valor c/ Desconto</label>
			<input type="text" name="autoDeInfracao.valorComDesconto" value="${autoDeInfracao.valorComDesconto}" class="decimal"/>
			<label for="autoDeInfracao.dataDePagamento">Pagamento</label>
			<input type="text" name="autoDeInfracao.dataDePagamento" size="8" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${autoDeInfracao.dataDePagamento.time}"/>" class="datePicker"/>
		
			<input type="hidden" name="autoDeInfracao.id" value="${autoDeInfracao.id}"/>
		</div>
	</div>
	<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio"/></span>
	<div class="gt-table-buttons">
		<input type="submit" value="<fmt:message key="views.botoes.salvar"/>" class="gt-btn-medium gt-btn-left" />
		<input type="button" value="<fmt:message key="views.botoes.cancelar"/>" class="gt-btn-medium gt-btn-left" onClick="javascript:window.location='${linkTo[AutoDeInfracaoController].listar}'" />
	</div>
</form>