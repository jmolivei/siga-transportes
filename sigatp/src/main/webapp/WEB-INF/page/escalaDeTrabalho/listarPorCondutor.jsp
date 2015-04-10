<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<jsp:include page="../tags/calendario.jsp" />

<script type="text/javascript">
		var urlSalvar = '<c:out value="${linkTo[EscalaDeTrabalhoController].salvar}" />';
		var urlFinalizar = '<c:out value="${linkTo[EscalaDeTrabalhoController].finalizar}" />';
		var urlCancelar = '<c:out value="${linkTo[CondutorController].lista}" />';
</script>

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${condutor.dadosParaExibicao}</h2>
			<h3>Escalas de Trabalho</h3>
			<sigatp:erros/>
			<jsp:include page="../condutor/menu.jsp"></jsp:include>
			<form id="formEscalasDeTrabalho" method="post" onsubmit="return false;" enctype="multipart/form-data">
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
											<c:forEach items="${diaSemana.values()}" var="dia">
												<option value="${dia}" ${dia == diaDeTrabalho.getDiaEntrada() ? 'selected' : ''} ${dia == diaDeTrabalho.getDiaEntrada() ? 'class=\"selecionado \"' : ''}>${dia}</option>
											</c:forEach>
										</select>
										<c:out value=""></c:out>
										<input type="text" name="horaEntrada" value='<fmt:formatDate pattern="HH:mm" value="${diaDeTrabalho.getHoraEntrada().time}"/>' size="8" class="hora selecionado" />
										at&eacute;
										<select name="diaSaida">
											<c:forEach items="${diaSemana.values()}" var="dia">
												<option value="${dia}" ${dia == diaDeTrabalho.getDiaEntrada() ? 'selected' : ''} ${dia == diaDeTrabalho.getDiaEntrada() ? 'class=\"selecionado \"' : ''}>${dia}</option>
											</c:forEach>
										</select>
										<input type="text" name="horaSaida" value='<fmt:formatDate pattern="HH:mm" value="${diaDeTrabalho.horaSaida.time}"/>' size="8" class="hora selecionado" />
										<input type="hidden" name="id" class="selecionado" value="${diaDeTrabalho.id}" />
									</td>
									<td width="8%"><a class="linkExcluir" name="linkExcluirSelecionados" onclick="escalas.apagaLinha(this)" style="display: inline" href="#"><fmt:message key="views.botoes.excluir" /></a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div id="btngridDiasDeTrabalho" class="gt-table-buttons">
						<input type="button" id="btn-Incluir-DiasDeTrabalho" value='<fmt:message key="views.botoes.incluirNovoDia" />' class="gt-btn-medium gt-btn-left btnSelecao" />
					</div>
					<div id="rowDiasDeTrabalho" style="display: none">
						<select name="diaEntrada">
							<c:forEach items="${diaSemana.values()}" var="dia">
								<option value="${dia}" class="naoSelecionado">${dia}</option>
							</c:forEach>
						</select>
						<input type="text" name="horaEntrada" value="11:00" size="8" class="hora naoSelecionado" /> 
						at&eacute;
						<select name="diaSaida">
							<c:forEach items="${diaSemana.values()}" var="dia">
								<option value="${dia}" class="naoSelecionado">${dia}</option>
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
					<input type="button" id="salvar" value='<fmt:message key="views.botoes.salvar" />' onclick="escalas.submitForm(urlSalvar)" class="gt-btn-medium gt-btn-left" />
					<input type="button" id="finalizar" value='<fmt:message key="views.botoes.finalizar" />' onclick="escalas.submitForm(urlFinalizar)" class="gt-btn-medium gt-btn-left" />
					<input type="button" id="cancelar" value='<fmt:message key="views.botoes.cancelar" />' onclick="javascript:window.location = urlCancelar" class="gt-btn-medium gt-btn-left" />
				</div>
			</form>

			<c:choose>
				<c:when test="${escalas.size() > 0}">
					<div class="gt-content-box gt-for-table">    
					    <h3>&nbsp;&nbsp;Hist&oacute;rico das Escalas de Trabalho</h3> 
					 	<table id="htmlgrid" class="gt-table">
					 		<thead>
						    	<tr style="font-weight: bold;">
						    	    <th>In&iacute;cio Vig&ecirc;ncia</th>
							   		<th>Fim Vig&ecirc;ncia</th>
							   		<th>Escala</th>
							   		<th width="5"></th>
							   		<th width="5"></th>
								</tr>
					 		</thead>
					 		<tbody>
								<c:forEach items="${escalas}" var="escala">
								   	<tr>
							    	    <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${escala.dataVigenciaInicio.time}" /></td>
							    	    <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${escala.dataVigenciaFim.time}" /></td>
							    		<td>${escala.escalaParaExibicao}</td>
									</tr>
								</c:forEach>
					 		</tbody>
					     </table>   
					</div>
				</c:when>
				<c:otherwise>
					<br/>
					<h3>N&atilde;o existem escalas cadastradas para este condutor.</h3>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</siga:pagina>

<script type="text/javascript" src="../../../public/javascripts/escalasDeTrabalho/listarPorCondutor.js"></script>