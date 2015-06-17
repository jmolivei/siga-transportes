<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>
<%@ taglib prefix="tptags" uri="/WEB-INF/tpTags.tld"%>

<script src="${pageContext.request.contextPath}/public/javascripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/stylesheets/servicoVeiculo.css">
<jsp:include page="../tags/calendario.jsp" />


<script type="text/javascript">

	function verificarSituacao() {
		var situacao = $('#situacaoServico').val();
		
		if (${avarias.size() > 0}) {
			$("#trAvarias").show(250);

			$("[name='chk']").each(function() {
				if (situacao != 'REALIZADO') {
					if ($(this).attr("checked")) {
						$(this).prop( "checked", false );
					}
					$(this).attr("disabled","true");
				}
				else {
			    	$(this).removeAttr("disabled");
				}
			});
		}
		else {
			$("#trAvarias").hide(250);
		}

		if (situacao == 'INICIADO') {
			$("#trDataHoraInicio").show(250);
			$("#trDataHoraFim").hide(250);
	    	$('#trMotivoCancelamento').hide(250);
		}
		else if (situacao == 'REALIZADO') {
			$("#trDataHoraInicio").show(250);
			$("#trDataHoraFim").show(250);
	    	$('#trMotivoCancelamento').hide(250);
		}
		else if (situacao == 'CANCELADO') {
			$("#trDataHoraInicio").hide(250);
			$("#trDataHoraFim").hide(250);
	    	$('#trMotivoCancelamento').show(250);
	    }
		else if (situacao == 'AGENDADO') {
			$("#trDataHoraInicio").hide(250);
			$("#trDataHoraFim").hide(250);
	    	$('#trMotivoCancelamento').hide(250);
		}
		
		if ($("#servicoVeiculoId").val() == '0') {
			$("#lstVeiculos").removeAttr("disabled");
			$("#lstTiposDeServico").removeAttr("disabled");
		}
		else {
			$("#lstVeiculos").attr("disabled","true");
			$("#lstTiposDeServico").attr("disabled","true");
		}
	}

	$(document).ready(function() {
		verificarSituacao();
		verificarAvarias();
		
		$("#situacaoServico").change(function(event) {
			event.preventDefault();
			verificarSituacao();
		});
	});

	function move(MenuOrigem, MenuDestino){
	    var arrMenuOrigem = new Array();
	    var arrMenuDestino = new Array();
	    var arrLookup = new Array();
	    var i;
	    for (i = 0; i < MenuDestino.options.length; i++){
	        arrLookup[MenuDestino.options[i].text] = MenuDestino.options[i].value;
	        arrMenuDestino[i] = MenuDestino.options[i].text;
	    }
	    var fLength = 0;
	    var tLength = arrMenuDestino.length;
	    for(i = 0; i < MenuOrigem.options.length; i++){
	        arrLookup[MenuOrigem.options[i].text] = MenuOrigem.options[i].value;
	        if (MenuOrigem.options[i].selected && MenuOrigem.options[i].value != ""){
	            arrMenuDestino[tLength] = MenuOrigem.options[i].text;
	            tLength++;
	        }
	        else{
	            arrMenuOrigem[fLength] = MenuOrigem.options[i].text;
	            fLength++;
	        }
	    }
	    arrMenuOrigem.sort();
	    arrMenuDestino.sort();
	    MenuOrigem.length = 0;
	    MenuDestino.length = 0;
	    var c;
	    for(c = 0; c < arrMenuOrigem.length; c++){
	        var no = new Option();
	        no.value = arrLookup[arrMenuOrigem[c]];
	        no.text = arrMenuOrigem[c];
	        MenuOrigem[c] = no;
	    }
	    for(c = 0; c < arrMenuDestino.length; c++){
	        var no = new Option();
	        no.value = arrLookup[arrMenuDestino[c]];
	        no.text = arrMenuDestino[c];
	        MenuDestino[c] = no;
	   }
	}

	function verificarAvarias() {
		var x = 0;
		$('.container input[type=checkbox]').each(function() {
			var checkbox = $(this);
			if(checkbox.is(':checked')) {
				var _id = 'avarias[' + x + ']';
				var _valor = checkbox.attr("id").replace('avaria_','');
				var input = $('<input type="hidden">').attr('id',_id).attr('name',_id).val(_valor).appendTo('#frmServicos') ;
				x ++;
			}
		});
	}

	function submitForm(acao) {
		$("#frmServicos").attr("action",acao);
		verificarAvarias();
		$("#frmServicos").submit();
	}
</script>

<form id="frmServicos" method="post" enctype="multipart/form-data">
<input type="hidden" id="servicoVeiculoId" name="servico" value="${servico.id}" />
<input type="hidden" name="servico.dataHora" value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.dataHora.time}" />" class="dataHora" />
	<div class="gt-content-box gt-form clearfix" >
		<div class="coluna margemDireitaG">
			<sigatp:erros />
		    <c:if test="${servico.ultimaAlteracao != null}">
				<label for="servico.ultimaAlteracao">&Uacute;ltima Altera&ccedil;&atilde;o</label>
				<input  type="text" name="servico.ultimaAlteracao" 
						value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.ultimaAlteracao.time}" />"
						disabled="disabled" size="20" class="dataHora" />
			</c:if>
				<label for="servico.tiposDeServico" class= "obrigatorio">Tipo de Servi&ccedil;o</label>
				
				<select name="servico.tiposDeServico" >
					<c:forEach items="${tiposDeServico}" var="tipoDeServico">
						<option value="${tipoDeServico}" ${tipoDeServico == servico.tiposDeServico ? 'selected' : ''} >${tipoDeServico.descricao}</option>
					</c:forEach>
				</select>
								
				
				<label for="servico.veiculo" class= "obrigatorio">Ve&iacute;culo</label>				
				<siga:select name="servico.veiculo" list="veiculos" listKey="id" listValue="dadosParaExibicao" value="${servico.veiculo.id}"/>
				<label for="servico.dataHoraInicioPrevisto" class= "obrigatorio">In&iacute;cio Previsto</label>
				<input  type="text"
						name="servico.dataHoraInicioPrevisto"
						value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.dataHoraInicioPrevisto.time}" />"
						size="16" class="dataHora" />
				<label for="servico.dataHoraFimPrevisto" class= "obrigatorio">T&eacute;rmino Previsto</label>
				<input  type="text" name="servico.dataHoraFimPrevisto"
						value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.dataHoraFimPrevisto.time}" />"
						size="16" class="dataHora" />
				<label for="servico.descricao"" class="obrigatorio">Descri&ccedil;&atilde;o</label>
				<textarea   name="servico.descricao"
							rows="6" cols="68">${servico.descricao}</textarea>
				<label for="situacaoServico" class= "obrigatorio">	Situa&ccedil;&atilde;o</label>
				<select id="situacaoServico" name="servico.situacaoServico" >
					<c:forEach items="${estadosServico}" var="estadoServico">
						<option value="${estadoServico}" ${estadoServico == servico.situacaoServico ? 'selected' : ''} >${estadoServico.descricao}</option>
					</c:forEach>
				</select>
		</div>
		<div class="coluna">
			<c:if test="${servico.id != 0}">
			
				<div id="trMotivoCancelamento" class="clearfix">				
					<label for="motivoCancelamento" class= "obrigatorio">Motivo do Cancelamento:</label>
					<textarea   id="motivoCancelamento" name="servico.motivoCancelamento" 
							rows="8" cols="68">${servico.motivoCancelamento}</textarea>
				</div>
				<div id="trDataHoraInicio" class="clearfix">				
					<label for="dataHoraInicio">In&iacute;cio:</label>
					<input  type="text" id="dataHoraInicio" name="servico.dataHoraInicio"
						value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.dataHoraInicio.time}" />" 
						size="16" class="dataHora" />
				</div>
				<div id="trDataHoraFim" class="clearfix">				
					<label for="dataHoraFim">Fim:</label>
					<input  type="text" id="dataHoraFim" name="servico.dataHoraFim"
					    value="<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${servico.dataHoraFim.time}" />" 
						size="16" class="dataHora" />
				</div>
			</c:if>	
			<div id="trAvarias" class="clearfix">  
				<label for="lstAvarias">Selecione as Avarias finalizadas:</label>
				<div class="container" id="lstAvarias">
					<c:forEach items="${avarias}" var="avaria">
						<input type="checkbox" name="chk" id="avaria_${avaria.id}" />
						<tptags:link texto="${avaria.descricao}" parteTextoLink="${avaria.descricao}" comando="${linkTo[AvariaController].editar[avaria.id][false]}"></tptags:link>
		    			<br/>
					</c:forEach>
				</div>
			</div>			
		</div>
	</div>
	<span><fmt:message key="views.erro.preenchimentoObrigatorio"></fmt:message></span> 
	<div class="gt-table-buttons">	
		<input id="btnSalvar" type="button" value="<fmt:message key="views.botoes.salvar"/>" onClick="submitForm('${linkTo[ServicoVeiculoController].salvar}')" class="gt-btn-medium gt-btn-left" />
		<input id="btnCancelar" type="button" value="<fmt:message key="views.botoes.cancelar"/>" onClick="javascript:location.href='${linkTo[ServicoVeiculoController].listar}'" class="gt-btn-medium gt-btn-left" />
	</div>
</form>
