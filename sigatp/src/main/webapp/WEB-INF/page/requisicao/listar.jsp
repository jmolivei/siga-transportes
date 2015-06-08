<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>
<%@ taglib prefix="tptags" uri="/WEB-INF/tpTags.tld"%>

<script type="text/javascript" src="/sigatp/public/javascripts/jquery-1.6.4.min.js"></script>

<script type="text/javascript">
       $(function() {

       	$('.missao').css('visibility','hidden');
		$('.bt_missao').css('display','none');

       	$('#missao_criar').click( function() {
       		$('.bt_missao').css('display','block');
       		$('.bt_edicao').css('display','none');
       		$('.missao').css('visibility','visible');
       		$('.edicao').css('visibility','hidden');
    	   });

       	$('#missao_cancelar').click( function() {
       		$('.bt_missao').css('display','none');
       		$('.bt_edicao').css('display','block');
       		$('.missao').css('visibility','hidden');
       		$('.edicao').css('visibility','visible');
    	   });

		$('#missao_incluir').click( function() {
			if($('form').serialize().indexOf("req=") != -1) {
				$('form').submit();
			} else {
				alert('Por favor, selecione ao menos uma requisi&ccedil;&atilde;o.');
			}
		});

       	$('#missao_inicioRapido').click( function() {
			if($('form').serialize().indexOf("req=") != -1) {
				var formulario = document.getElementById('formIncluirMissao');
				var destino = formulario.action;
				formulario.action = destino.replace("incluircomrequisicoes", "incluiriniciorapido"); // /sigatp/missoes/incluircomrequisicoes
				formulario.submit();
			} else {
				alert('Por favor, selecione ao menos uma requisi&ccedil;&atilde;o.');
			}
		});

      });
</script>

<style type="text/css">

.status_N, .status_P, .status_A, .status_E {
	border: 2px solid;
	border-radius: 1em;
	text-transform: uppercase;
	padding: 0 .25em;
	margin-left: .2em;
	font:normal 70% verdana, arial, sans-serif;
	font-weight: 900;
	position: relative;
	top: -.2em;
}
.status_N {
	border-color: #FF2000;
	color: #FF2000;
}
.status_P {
	border-color: #33EE00;
	color: #33EE00;
}
.status_A {
	border-color: #33EE00;
	color: #33EE00;
}
.status_E {
	border-color: #FFFF00;
	color: #FFFF00;
}

</style>

<siga:pagina titulo="SIGA - Transporte">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<c:choose>
				<c:when test="${!menuRequisicoesMostrarTodas}">
					<h2>Lista de Todas as Requisi&ccedil;&otilde;es</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarAutorizadasENaoAtendidas && menuRequisicoesMostrarAutorizadas && menuRequisicoesMostrarNaoAtendidas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Autorizadas e N&atilde;o Atendidas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarAbertas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Abertas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarAutorizadas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Autorizadas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarRejeitadas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Rejeitadas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarProgramadas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Programadas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarEmAtendimento}">
					<h2>Lista de Requisi&ccedil;&otilde;es em Atendimento</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarAtendidas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Atendidas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarNaoAtendidas}">
					<h2>Lista de Requisi&ccedil;&otilde;es N&atilde;o Atendidas</h2>
				</c:when>

				<c:when test="${!menuRequisicoesMostrarCanceladas}">
					<h2>Lista de Requisi&ccedil;&otilde;es Canceladas</h2>
				</c:when>
			</c:choose>

			<form id="formIncluirMissao" action="${linkTo[MissaoController].incluirComRequisicoes}" enctype="multipart/form-data">
				<jsp:include page="menuListar.jsp"></jsp:include>
				<c:choose>
					<c:when test="${requisicoesTransporte.size() > 0}">
						<div class="gt-content-box gt-for-table">
			 				<table id="htmlgrid" class="gt-table" >
			    				<thead>
							    	<tr style="font-weight: bold;">
							    	    <th width="15%">Saida</th>
								   		<th width="15%">Retorno</th>
								   		<th width="30%">Outros Dados</th>
								   		<th width="20%">Miss&otilde;es</th>
								   		<th width="5%"></th>
								   		<th width="5%"></th>
										<th width="5%"></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${requisicoesTransporte}" var="requisicaoTransporte">
									   	<tr id ="row_${requisicaoTransporte.id}">
						 			   	   	<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraSaidaPrevista.time}" /></td>
								    		<td>
								    			<c:choose>
								    				<c:when test="${requisicaoTransporte.dataHoraRetornoPrevisto != null}">
								    					<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraRetornoPrevisto.time}" />
								    				</c:when>
								    				<c:otherwise>
								    					<fmt:message key="no"/>
								    				</c:otherwise>
								    			</c:choose>
								    		</td>
									   	    <td>
									   	    	<tptags:link texto="${requisicaoTransporte.descricaoCompleta}"
									   	    				 parteTextoLink="${requisicaoTransporte.buscarSequence()}"
									   	    				 comando="${linkTo[RequisicaoController].buscarPelaSequence[true][requisicaoTransporte.buscarSequence()]}"
									   	    				 ehEditavel="true">
									   	    	</tptags:link>
											</td>
											<td>
												<c:forEach items="${requisicaoTransporte.missoesOrdenadas}" var="missao">
													<nobr>
														${missao.sequence}
														<span class="status_${requisicaoTransporte.getUltimoEstadoNestaMissao(missao.id).primeiraLetra()}" alt="${requisicaoTransporte.getUltimoEstadoNestaMissao(missao.id)}" title="${requisicaoTransporte.getUltimoEstadoNestaMissao(missao.id)}">
															${requisicaoTransporte.getUltimoEstadoNestaMissao(missao.id).primeiraLetra()}
														</span>
														<a href="#" onclick="javascript:window.open('${linkTo[MissaoController].buscarPelaSequence[true][missao.sequence]}');">
															<img src="/sigatp/public/images/linknovajanelaicon.png" alt="Abrir em uma nova janela" title="Abrir em uma nova janela">
														</a>
													</nobr>
													<br />
												</c:forEach>
												<c:choose>
													<c:when test=""></c:when>
													<c:otherwise>
														N&Atilde;O ALOCADA
													</c:otherwise>
												</c:choose>
											</td>
											<c:if test="${requisicaoTransporte.podeAlterar}">
									    		<td class="edicao"><a href="${linkTo[RequisicaoController].editar[requisicaoTransporte.id]}">Editar</a></td>
											</c:if>
								    		<td class="edicao">
								    			<a onclick="javascript:return confirm('Tem certeza de que deseja excluir esta requisicao ?');" href="${linkTo[RequisicaoController].excluir[requisicaoTransporte.id]}">Excluir</a>
								    		</td>
								    		<td class="missao" valign="middle" colspan="2">
								    			<c:if test="${requisicaoTransporte.podeAgendar}">
								    				<input type="checkbox" name="req" value="${requisicaoTransporte.id}" class="missao reqs">
								    			</c:if>
								    		</td>
										</tr>
									</c:forEach>
				 				</tbody>
			     			</table>
			     		   	<div id="pagination"/>
						</div>
					</c:when>
					<c:otherwise>
						<h2>N&atilde;o existem requisi&ccedil;&otilde;es neste estado.</h2>
					</c:otherwise>
				</c:choose>

				<div class="gt-table-buttons">
					<a href="${linkTo[RequisicaoController].incluir}" class="gt-btn-medium gt-btn-left bt_edicao"><fmt:message key="views.botoes.incluir"/></a>
					<c:if test="${(exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo) && requisicoesTransporte.size() > 0}">
						<a href="#" id="missao_criar" class="gt-btn-medium gt-btn-left bt_edicao"><fmt:message key="views.botoes.criarMissao"/></a>
						<a href="#" id="missao_inicioRapido" class="gt-btn-medium gt-btn-left bt_missao"><fmt:message key="views.botoes.inicioRapido"/></a>
					</c:if>
					<a href="#" id="missao_incluir" class="gt-btn-medium gt-btn-left bt_missao"><fmt:message key="views.botoes.programar"/></a>
					<a href="#" id="missao_cancelar" class="gt-btn-medium gt-btn-left bt_missao"><fmt:message key="views.botoes.cancelar"/></a>
				</div>
			</form>
		</div>
	</div>
</siga:pagina>