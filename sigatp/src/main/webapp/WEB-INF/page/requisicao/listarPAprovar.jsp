<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="tptags" uri="/WEB-INF/tpTags.tld"%>

<script type="text/javascript" src="/sigatp/public/javascripts/jquery-1.6.4.min.js"></script>

<script type="text/javascript">
       $(function() {

       	$('.complexo').css('visibility','hidden');
		$('.bt_requisicao').css('display','none');

       	$('#requisicao_transferirDeComplexo').click( function() {	
       		$('.bt_requisicao').css('display','block');
       		$('.bt_edicao').css('display','none');
       		$('.complexo').css('visibility','visible');
       		$('.edicao').css('visibility','hidden');
    	   });

       	$('#requisicao_cancelar').click( function() {	
       		$('.bt_requisicao').css('display','none');
       		$('.bt_edicao').css('display','block');
       		$('.complexo').css('visibility','hidden');
       		$('.edicao').css('visibility','visible');
    	   });

		$('#requisicao_salvarNovoComplexo').click( function() {	
			if($('form').serialize().indexOf("req=") != -1) {
				$('form').submit();
			} else {
				alert('Por favor, selecione ao menos uma requisição.');
			}
		});
      });
</script>

<c:set var="numeroAutorizadas" value="${0}"></c:set>

<siga:pagina titulo="Siga - Transporte">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<c:choose>
				<c:when test="${menuRequisicoesMostrarAbertas && menuRequisicoesMostrarAutorizadas && menuRequisicoesMostrarRejeitadas}">
					<h2>Lista de Todas as Requisi&ccedil;&otilde;es</h2>
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
			</c:choose>
			<form id="formSalvarNovoComplexo" action="${linkTo[RequisicaoController].salvarNovoComplexo}" enctype="multipart/form-data">
				<jsp:include page="menuListarPAprovar.jsp"></jsp:include>
				<c:choose>
					<c:when test="${requisicoesTransporte.size() > 0}">
						<div class="gt-content-box gt-for-table">     
						 	<table id="htmlgrid" class="gt-table" >
						 		<thead>
						    		<tr style="font-weight: bold;">
							    		<th width="10%">Saida</th>
								   		<th width="10%">Retorno</th>
								   		<th width="10%">Complexo</th>
								   		<th width="30%">Outros Dados</th>
								   		<th width="10%"></th>
								   		<th width="10%"></th>
								   		<th width="10%"></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${requisicoesTransporte}" var="requisicaoTransporte">
										<tr id ="row_${requisicaoTransporte.id}">
											<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraSaidaPrevista.time}"/></td>
											<td>
												<c:choose>
													<c:when test="${requisicaoTransporte.dataHoraRetornoPrevisto != null}">
														<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraRetornoPrevisto.time}"/></td>
													</c:when>
						    						<c:otherwise>
						    							<fmt:message key="no"/>
						    						</c:otherwise>
												</c:choose>
											</td>
											<td>${null != requisicaoTransporte.cpComplexo ? requisicaoTransporte.cpComplexo.nomeComplexo : ""}</td>
							   	    		<td>
							   	    			<tptags:link texto="${requisicaoTransporte.descricaoCompleta}" 
							   	    						 parteTextoLink="${requisicaoTransporte.buscarSequence()}" 
							   	    						 comando="${linkTo[RequisicaoController].buscarPelaSequence[true][requisicaoTransporte.buscarSequence()]}">
							   	    						</tptags:link>
											</td>		    		
						    				<td class="edicao">
						    					<c:if test="${(exibirMenuAprovador || exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo) && ! requisicaoTransporte.getUltimoAndamento().estadoRequisicao.equals(models.EstadoRequisicao.AUTORIZADA)}">
						    						<a class="once" href="${linkTo[AndamentoController].autorizar[requisicaoTransporte.id]}">
						    							<img src="/sigatp/public/images/approvedicon.png" style="margin-right: 5px;">
						    							<fmt:message key="autorizar"/>
						    						</a>&nbsp;&nbsp;&nbsp;
						    					</c:if>
											</td>
											<td class="edicao">
												<c:if test="${(exibirMenuAprovador || exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo ) && ! requisicaoTransporte.getUltimoAndamento().estadoRequisicao.equals(models.EstadoRequisicao.REJEITADA)}">
													<a class="once" href="${linkTo[AndamentoController].rejeitar[requisicaoTransporte.id]}">
														<img src="/sigatp/public/images/rejectedicon.png" style="margin-right: 5px;">
						    							<fmt:message key="rejeitar"/>
													</a>&nbsp;&nbsp;&nbsp;
												</c:if>
												<td class="complexo" valign="middle" colspan="2">
													<c:if test="${requisicaoTransporte.getUltimoAndamento().estadoRequisicao.equals(models.EstadoRequisicao.AUTORIZADA)}">
														${numeroAutorizadas = numeroAutorizadas + 1}
														<input type="checkbox" name="req" value="${requisicaoTransporte.id}" class="complexo reqs">
													</c:if>
												</td>
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
					<c:if test="${(exibirMenuAdministrar || exibirMenuAdministrarMissao ) && requisicoesTransporte.size() > 0 && numeroAutorizadas > 0}">
						<a href="#" id="requisicao_transferirDeComplexo" class="gt-btn-medium gt-btn-left bt_edicao"><fmt:message key="alterar.complexo"/></a>
					</c:if>
					<div class="clearfix">
						<label class="bt_requisicao" for= "idNovoComplexo">&nbsp;&nbsp;Alterar para: </label>
						<select class="bt_requisicao" name="novoComplexo">
							<c:forEach items="${complexos}" var="complexo">
								<option value="${complexo.idComplexo}">${complexo.nomeComplexo}</option>
							</c:forEach>
						</select>
					</div>
					<a href="#" id="requisicao_salvarNovoComplexo" class="gt-btn-medium gt-btn-left bt_requisicao"><fmt:message key="views.botoes.salvar" /></a>
					<a href="#" id="requisicao_cancelar" class="gt-btn-medium gt-btn-left bt_requisicao"><fmt:message key="views.botoes.cancelar" /></a>
				</div>
			</form>
		</div>
	</div>
</siga:pagina>