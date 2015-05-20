<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<sigatp:erros />

<form name="formAbastecimentos" id="formAbastecimentos"
	action="${linkTo[AbastecimentoController].salvar}" method="post"
	cssClass="form" enctype="multipart/form-data"> 

	<input type="hidden" name="abastecimento.id" value="${abastecimento.id}" />
	<input type="hidden" name="abastecimento.titular" value="${abastecimento.titular.id}" />
	<input type="hidden" name="abastecimento.solicitante" value="${abastecimento.solicitante.id}" />

	<div class="gt-content-box gt-form clearfix">
		<div class="coluna margemDireitaG">
			<label for="abastecimento.dataHora" class="obrigatorio">Data e Hora</label> 
			<input type="text" name="abastecimento.dataHora"
				value="<fmt:formatDate pattern="dd/MM/yyyy" value="${abastecimento.dataHora.time}" />"
				size="16" class="dataHora" /> 

			<label for="abastecimento.condutor.id" class="obrigatorio">Condutor</label>
			<siga:select name="abastecimento.condutor.id" list="condutores"
				listKey="id" listValue="dadosParaExibicao"
				value="${abastecimento.condutor.id}" headerKey="0" headerValue=" " />

			<label for= "abastecimento.veiculo.id" class= "obrigatorio">Ve&iacute;culo</label>
			<siga:select name="abastecimento.veiculo.id" list="veiculos"
				listKey="id" listValue="dadosParaExibicao"
				value="${abastecimento.veiculo.id}" headerKey="0" headerValue=" " />

			<label for="abastecimento.odometroEmKm" class= "obrigatorio">Od&ocirc;metro (Km)</label>
	       	<input type="text" name="abastecimento.odometroEmKm" value="${abastecimento.odometroEmKm}" class="valor_numerico decimal"/>
		</div>
		<div class="coluna margemDireitaG">
 	       	<label for="abastecimento.tipoDeCombustivel" class= "obrigatorio">Tipo de Combust&iacute;vel</label>
 	       	<siga:select name="abastecimento.tipoDeCombustivel" list="tiposCombustivelParaAbastecimento"
				listKey="descricao" listValue="descricao"
				value="${abastecimento.tipoDeCombustivel}" headerKey="0" headerValue=" " />

			<label for="abastecimento.fornecedor.id" class= "obrigatorio">Fornecedor</label>
			<siga:select name="abastecimento.fornecedor.id" list="fornecedores"
				listKey="id" listValue="razaoSocial"
				value="${abastecimento.fornecedor.id}" headerKey="0" headerValue=" " />

			<label for="abastecimento.precoPorLitro" class="obrigatorio">Pre&ccedil;o por litro (R$)</label> 
			<input type="text"
				name="abastecimento.precoPorLitro"
				value="${abastecimento.formataValorExponencialParaDecimal(abastecimento.precoPorLitro)}"
				class="valor_numerico decimal" /> 

			<label for="abastecimento.quantidadeEmLitros" class="obrigatorio">Quantidade(litros)</label> 
			<input type="text"
				name="abastecimento.quantidadeEmLitros"
				value="${abastecimento.formataValorExponencialParaDecimal(abastecimento.quantidadeEmLitros)}"
				class="valor_numerico decimal" /> 

			<input type="hidden" name="abastecimento.nivelDeCombustivel" value="A" />
 	    </div>
 	    <div class="coluna">
	       	<label for="abastecimento.valorTotalDaNotaFiscal" class= "obrigatorio">Valor da Nota Fiscal (R$) </label>
	       	<input type="text" name="abastecimento.valorTotalDaNotaFiscal" value="${abastecimento.formataValorExponencialParaDecimal(abastecimento.valorTotalDaNotaFiscal)}" class="valor_numerico decimal" />
	      	<label for="abastecimento.numeroDaNotaFiscal" class= "obrigatorio">N&uacute;mero da Nota Fiscal</label>
	       	<input type="text" name="abastecimento.numeroDaNotaFiscal" value="${abastecimento.numeroDaNotaFiscal}" class="valor_numerico""/>
		</div>
	</div>
	<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio"/></span>
	<div class="gt-table-buttons">
		<input type="submit" value="<fmt:message key="views.botoes.salvar"/>" class="gt-btn-medium gt-btn-left" />
		<input type="button" value="<fmt:message key="views.botoes.cancelar"/>" 
			onClick="javascript:location.href='${linkTo[AbastecimentoController].listar}'" class="gt-btn-medium gt-btn-left" />
	</div>

</form>