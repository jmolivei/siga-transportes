<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:include page="../tags/calendario.jsp" />

<form name="formCondutor" id="formCondutor"
	action="${linkTo[CondutorController].salvar}" method="post"
	cssClass="form">
	<div class="gt-content-box gt-form">
		<c:choose>
			<c:when test="${condutor.id == 0}">
				<div id="divItem">
					<jsp:include page="exibirDadosDpPessoa.jsp" />
				</div>

				<label for="condutor.dpPessoa.id">Servidor: </label>

				<siga:selecao tipo="pessoa" propriedade="pessoa" tema="simple" modulo="siga" idInicial="0"/>

				<jsp:include page="ae.jsp"/>
				<input type="hidden" name="condutor.dpPessoa.id" value="" />
			</c:when>
			<c:otherwise>
				<input type="hidden" name="condutor.dpPessoa.id" value="${condutor.dpPessoa.id}" />
			</c:otherwise>
		</c:choose>

		<input type="hidden" name="condutor.id" value="${condutor.id}"/>
		
		<div class="clearfix">
			<div class="coluna margemDireitaG">
				<label for="condutor.dtNascimento">Data de nascimento:</label> 
				<input type="text" readonly="readonly" name="condutor.dtNascimento"
					value="<fmt:formatDate pattern="dd/MM/yyyy" value="${condutor.dpPessoa.getDataNascimento()}" />" />

				<label for="condutor.numeroCNH" class="obrigatorio">N&uacute;mero da CNH:</label> 
				<input type="text" name="condutor.numeroCNH" value="${condutor.numeroCNH}" /> 
					
				<label for="categoriaCNH" class="obrigatorio">Categoria CNH:</label>
				<siga:select name="condutor.categoriaCNH" list="listCategorias" listKey="descricao" listValue="descricao" value="${condutor.categoriaCNH}"/>

				<label for="condutor.dataVencimentoCNH" class="obrigatorio">Data de Vencimento CNH:</label> 
				<input type="text" name="condutor.dataVencimentoCNH" class="datePicker"
					value="<fmt:formatDate pattern="dd/MM/yyyy" value="${condutor.dataVencimentoCNH.time}" />" />
			</div>
			<div class="coluna margemDireitaG">
				<label for="condutor.lotacao">Lota&ccedil;&atilde;o:</label> <input
					type="text" name="condutor.lotacao" size="46" readonly="readonly"
					value="${condutor.dpPessoa.getLotacao() == null ? '' : condutor.dpPessoa.getLotacao().getDescricao()}" />

				<label for="condutor.endereco">Endere&ccedil;o:</label> <input
					type="text" size="46" name="condutor.endereco"
					value="${condutor.endereco}" /> <label for="condutor.emailPessoal">Email
					pessoal: </label> <input type="text" name="condutor.emailPessoal"
					value="${condutor.emailPessoal}" /> <label
					for="condutor.emailInstitucional">Email institucional: </label> <input
					type="text" readonly="readonly" name="condutor.emailInstitucional"
					value="${condutor.dpPessoa.getEmailPessoa()}" />
			</div>
			<div class="coluna">
				<label for="condutor.telefoneInstitucional" class="obrigatorio">Telefone
					fixo institucional:</label> <input type="text" class="telefone"
					name="condutor.telefoneInstitucional"
					value="${condutor.telefoneInstitucional}" /> <label
					for="condutor.celularInstitucional">Telefone celular
					institucional:</label> <input type="text" class="celular"
					name="condutor.celularInstitucional"
					value="${condutor.celularInstitucional}" /> <label
					for="condutor.telefonePessoal">Telefone fixo pessoal: </label> <input
					type="text" class="telefone" name="condutor.telefonePessoal"
					value="${condutor.telefonePessoal}" /> <label
					for="condutor.celularPessoal">Telefone celular pessoal: </label> <input
					type="text" class="celular" name="condutor.celularPessoal"
					value="${condutor.celularPessoal}" />
			</div>
		</div>

		<input type="hidden" id="situacaoImagem"
			name="condutor.situacaoImagem" value="${condutor.situacaoImagem}" />
		<label for="condutor.observacao">Observa&ccedil;&atilde;o: </label>
		<textarea name="condutor.observacao" rows="4" cols="60">${condutor.observacao}</textarea>

		<label>Anexar arquivo: </label> 
		<input type="file" name="condutor.arquivo" size="30" id="arquivo" /> 
		<img id="imgArquivo" class="thumb" src="${imgArquivo}" /> 
		<br /> 
		<input type="button" class="botaoImagem" id="exibirImagem" value="<fmt:message key="views.botoes.exibir"/>" /> 
		<input type="button" class="botaoImagem" id="excluirImagem" onclick="removerArquivo();" value="<fmt:message key="views.botoes.excluir"/>" />

	</div>

	<span class="alerta menor"><fmt:message
			key="views.erro.preenchimentoObrigatorio" /></span>
	<div class="gt-table-buttons">
		<input type="submit" value="<fmt:message key="views.botoes.salvar"/>"
			class="gt-btn-medium gt-btn-left" /> <input type="button"
			value="<fmt:message key="views.botoes.cancelar"/>"
			class="gt-btn-medium gt-btn-left"
			onclick="javascript:window.location = '${linkTo[CondutorController].listar}';" />
	</div>
<script type="text/javascript">

// 	$( "#formulario_pessoa_pessoaSel_sigla" ).load(function() {
// 		document.getElementById("formulario_pessoa_pessoaSel_id").addEventListener("change", function(){
// 			carregarDadosDpPessoa();
// 		})
// 	});
	
	$(document).ready(function() {
		$('#imgArquivo').css('display','none');

// 		$('#formulario_pessoa_pessoaSel_id').on('change', function() {
// 			carregarDadosDpPessoa();
// 			});		
		$('#exibirImagem').click(function() {
			var url = '';
			
			if($('#imgArquivo').css('display') == 'block') {
				if ($('#situacaoImagem').val() == "nobanco") {
						url = "@{Condutores.exibirImagem(condutor.id)}";
				}
				else if ($('#situacaoImagem').val() == "imagemnova") {
					url = $('#arquivo').val();
				}

				var newwin = window.open(url,'miniwin','toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=0,width=800,height=800,top=100,left=100');
				//newwin.document.close();
			}
		});
		
		$('#arquivo').change(function(evt) {
			var arquivo; 
			
			if($('#arquivo').val() != "") {
	        	if ( $.browser.msie ) {
		        	$('#imgArquivo').attr('src',this.value);
	                $('#exibirImagem').attr('disabled',false);
				}
				else { //chrome, mozilla
					  var f = evt.target.files[0];
				      var reader = new FileReader();
				      reader.onload = (function(theFile) {
				       	return function(e) {
					        	$('#imgArquivo').attr('src',e.target.result);
					        };
				      })(f);
				      reader.readAsDataURL(f);
						  $('#exibirImagem').attr('disabled',false);
				}

				if ($('#imgArquivo').css('display') == 'none') {
					$('#imgArquivo').css('display','block');
					$('#excluirImagem').attr('disabled',false);
					$('#situacaoImagem').val('imagemnova');
				}
			}
		});

		if ($('#imgArquivo').css('display') == 'none') {
			$('#excluirImagem').attr('disabled',true);
		}

		else if($('#imgArquivo').css('display') == 'block') {
			$('#excluirImagem').attr('disabled',false);
		}
		});

	function removerArquivo() {
			$('#imgArquivo').removeAttr('src');
		$('#excluirImagem').attr('disabled',true);
		$('#imgArquivo').css('display','none');

		if ( $.browser.msie ) {
			$("#arquivo").replaceWith($("#arquivo").clone(true));
       	}
		else { //chrome, mozilla
			$('#arquivo').val("");
		}
		
		$('#situacaoImagem').val('semimagem');
		    $('#exibirImagem').attr('disabled',true);
	 	 }

// 	$(window).load(function() {
// // 		var exibirImgArquivo = jsAction @Condutores.exibirImgArquivo(':id')
// 		var exibirImgArquivo = 'fuck';
// 	  	    $.get(
// 	  	    	   exibirImgArquivo({id: $("input[name='condutor.id']").val()}), 
// 		           function(carregouImagem) {
//     					if (carregouImagem == "true") {   
//     						$('#imgArquivo').css('display','block');
//     						$('#excluirImagem').attr('disabled',false);
//     						$('#exibirImagem').attr('disabled',false);
//     						$('#situacaoImagem').val('nobanco');
//     					}
//     					else {
//     						$('#imgArquivo').css('display','none');
//         					$('#excluirImagem').attr('disabled',true);
//         					$('#exibirImagem').attr('disabled',true);
//     						$('#situacaoImagem').val('semimagem');
//     					}
//     			   }
// 	  	);
// 	});
</script>
</form>




