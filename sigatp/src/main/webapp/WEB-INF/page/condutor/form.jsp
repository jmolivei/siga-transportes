<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:include page="../tags/calendario.jsp" />

<style>
.thumb {
	height: 100px;
	border: 1px solid #000;
	margin: 10px 5px 0 0;
}

.botaoImagem {
	padding-left: 0.2cm;
	padding-right: 0.2cm;
}
</style>

<script language="javascript">
function carregarDadosDpPessoa() {

// 		ReplaceInnerHTMLFromAjaxResponse('${pageContext.request.contextPath}/app/expediente/doc/carregar_lista_formas?tipoForma='+document.getElementById('tipoForma').value+'&idFormaDoc='+'${idFormaDoc}', null, document.getElementById('comboFormaDiv'));

		
// 		params = 'pessoa.idPessoa' + '=' + escape($('#condutordpPessoa').val());
// 		PassAjaxResponseToFunction('@{Condutores.exibirDadosDpPessoa()}?' + params, 'carregouDadosDpPessoa', null, false, null);
// 	}

// 	function carregouDadosDpPessoa(response, param){
// 		$('#divItem').html(response);
// 		$("input[name='condutor.dtNascimento']").val($("input[name='dataNascimento']").val()); 
// 		$("input[name='condutor.lotacao']").val($("input[name='lotacao']").val()); 
// 		$("input[name='condutor.emailInstitucional']").val($("input[name='emailInstitucional']").val()); 
// 	}
</script>

<div class="gt-content-box gt-form">
	<c:choose>
		<c:when test="${condutor.id == 0}">
			<div id="divItem">
				<jsp:include page="exibeDadosDpPessoa.jsp" />
			</div>

			<label for="condutor.dpPessoa.id">Servidor: </label>
			<siga:select name="idServidor" list="idServidor"
				listKey="condutor.dpPessoa" id="idTpConfiguracao"
				headerValue="[Indefinido]" headerKey="0"
				listValue="condutor.dpPessoa" theme="simple" />

			<%-- 			#{selecao tipo:'pessoa',nome:'condutor.dpPessoa', value:condutor.dpPessoa, onchange:'carregarDadosDpPessoa()' /} --%>
		</c:when>
		<c:otherwise>
			<input type="hidden" name="condutor.dpPessoa.id" value="${condutor.dpPessoa.id}"/>
		</c:otherwise>
	</c:choose>

	<div class="clearfix">
		<div class="coluna margemDireitaG">
			<label for="condutor.dtNascimento">Data de nascimento:</label>
			<input type="text" readonly="readonly" name="condutor.dtNascimento" 
				value="<fmt:formatDate pattern="dd/MM/yyyy" value="${condutor.dpPessoa.getDataNascimento()}" />" />

			<label for="condutor.numeroCNH" class="obrigatorio">N&uacute;mero da CNH:</label>
			<input type="text" name="condutor.numeroCNH" value="${condutor.numeroCNH}" />

			<label for="categoriaCNH" class="obrigatorio">Categoria CNH:</label>

			<siga:select name="categoriaCNH" list="listCategorias"
				listKey="descricao" id="categoriaCNH"
				headerValue="[Indefinido]" headerKey="0"
				listValue="descricao" theme="simple" />
				
			<label for="condutor.dataVencimentoCNH" class="obrigatorio">Data de Vencimento CNH:</label>
		    <input type="text" name="condutor.dataVencimentoCNH" class="datePicker" 
		    	value="<fmt:formatDate pattern="dd/MM/yyyy" value="${condutor.dataVencimentoCNH.time}" />" />
		</div>
		<div class="coluna margemDireitaG">
			<label for="condutor.lotacao">Lota&ccedil;&atilde;o:</label>
			<input type="text" name="condutor.lotacao" size="46" readonly="readonly"
					   value="${condutor.dpPessoa.getLotacao() == null ? '' : condutor.dpPessoa.getLotacao().getDescricao()}" />

			<label for="condutor.endereco">Endere&ccedil;o:</label>
			<input type="text" size="46" name="condutor.endereco" value="${condutor.endereco}" />

	       	<label for="condutor.emailPessoal">Email pessoal: </label>
	       	<input type="text" name="condutor.emailPessoal" value="${condutor.emailPessoal}" />

	       	<label for="condutor.emailInstitucional">Email institucional: </label>
	       	<input type="text" readonly="readonly" name="condutor.emailInstitucional" value="${condutor.dpPessoa.getEmailPessoa()}" />
		</div>
		<div class="coluna">
		   	<label for="condutor.telefoneInstitucional" class="obrigatorio">Telefone fixo institucional:</label>
		   	<input type="text" class="telefone" name="condutor.telefoneInstitucional" value="${condutor.telefoneInstitucional}" />
		   	<label for="condutor.celularInstitucional">Telefone celular institucional:</label>
		  	<input type="text" class="celular" name="condutor.celularInstitucional" value="${condutor.celularInstitucional}" />
		   	<label for="condutor.telefonePessoal">Telefone fixo pessoal: </label>
		   	<input type="text" class="telefone" name="condutor.telefonePessoal" value="${condutor.telefonePessoal}" />
		  	<label for="condutor.celularPessoal">Telefone celular pessoal: </label>
		   	<input type="text" class="celular" name="condutor.celularPessoal" value="${condutor.celularPessoal}" />
		</div>
	</div>

	<input type="hidden" id="situacaoImagem" name="condutor.situacaoImagem" value="${condutor.situacaoImagem}"/>
    <label for="condutor.observacao">Observa&ccedil;&atilde;o: </label>
	<textarea name="condutor.observacao" rows="4" cols="60">${condutor.observacao}</textarea>       

   	<label>Anexar arquivo: </label>
   	<input type="file" name="condutor.arquivo" size="30" id="arquivo"/>
	<img id="imgArquivo" class="thumb" src="${imgArquivo}" />
		
	<br/>
	<input type="button" class="botaoImagem" id="exibirImagem" value="<fmt:message key="views.botoes.exibir"/>" />
	<input type="button" class="botaoImagem" id="excluirImagem" onclick="removerArquivo();" value="<fmt:message key="views.botoes.excluir"/>" />

</div>

<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio"/></span>
<div class="gt-table-buttons">
	<input type="submit" value="<fmt:message key="views.botoes.salvar"/>"
		class="gt-btn-medium gt-btn-left" /> 
	<input type="button"
		value="<fmt:message key="views.botoes.cancelar"/>" class="gt-btn-medium gt-btn-left"
		onclick="javascript:window.location = '${linkTo[CondutorController].lista}';" />
</div>




