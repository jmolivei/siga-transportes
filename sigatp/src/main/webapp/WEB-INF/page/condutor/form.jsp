<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

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
			<siga:select name="condutor.categoriaCNH" list="condutor.categoriaCNH.values()"
				listKey="condutor.categoriaCNH" id="condutor.categoriaCNH"
				headerValue="[Indefinido]" headerKey="0"
				listValue="condutor?.categoriaCNH" theme="simple" />
				
			<label for="condutor.dataVencimentoCNH" class="obrigatorio">Data de Vencimento CNH:</label>
		    <input type="text" name="condutor.dataVencimentoCNH" class="datePicker" 
		    	value="<fmt:formatDate pattern="dd/MM/yyyy" value="${condutor.dataVencimentoCNH.time}" />" />
		</div>
	</div>

</div>




