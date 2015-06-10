<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="/sigatp/public/stylesheets/andamentos.css" type="text/css" media="screen"/>
<script type="text/javascript">
	function trocaExibicaoCaixaBuscaAvancada() {
		$(".caixa_busca_avancado").toggle();
	}
</script>

<p class="gt-table-action-list">
	<c:if test="${menuMissoesMostrarVoltar}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_Voltar" href="${linkTo[MissaoController].listarFiltrado['PROGRAMADA']}"></a>
		<a href="${linkTo[MissaoController].listarFiltrado['PROGRAMADA']}">Voltar</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuMissoesMostrarTodas}">
		<c:choose>
			<c:when test="${exibirMenuAdministrar  || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo}">
	 	   		<img src="/sigatp/public/images/filter-icon.png"/>
	 	   		<a class="filtro_T" href="${linkTo[MissaoController].listar}"></a>
	 	   		<a href="${linkTo[MissaoController].listar}"><U>T</U>odas</a>&nbsp;&nbsp;&nbsp;
			</c:when>
			<c:otherwise>
 	   			<img src="/sigatp/public/images/filter-icon.png"/>
 	   			<a class="filtro_T" href="${linkTo[MissaoController].listarPorCondutorLogado}"></a>
 	   			<a href="${linkTo[MissaoController].listarPorCondutorLogado}"><U>T</U>odas</a>&nbsp;&nbsp;&nbsp;
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${menuMissoesMostrarProgramadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_P" href="${linkTo[MissaoController].listarFiltrado['PROGRAMADA']}"></a>
		<a href="${linkTo[MissaoController].listarFiltrado['PROGRAMADA']}"><U>P</U>rogramadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuMissoesMostrarIniciadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_I" href="${linkTo[MissaoController].listarFiltrado['INICIADA']}"></a>
		<a href="${linkTo[MissaoController].listarFiltrado['INICIADA']}"><U>I</U>niciadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuMissoesMostrarFinalizadas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_F" href="${linkTo[MissaoController].listarFiltrado['FINALIZADA']}"></a>
		<a href="${linkTo[MissaoController].listarFiltrado['FINALIZADA']}"><U>F</U>inalizadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuMissoesMostrarCanceladas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_C" href="${linkTo[MissaoController].listarFiltrado['CANCELADA']}"></a>
		<a href="${linkTo[MissaoController].listarFiltrado['CANCELADA']}"><U>C</U>anceladas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuMissoesMostrarFiltrarPorCondutor && ((condutorEscalado == null) || (condutorEscalado.id == 0))}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_Avancado" href="#"></a>
		<a href="#" onclick="javascript:trocaExibicaoCaixaBuscaAvancada();">Avan&ccedil;ado</a>&nbsp;&nbsp;&nbsp;
	</c:if>
</p>
<p>
	<div class="caixa_busca_avancado">
		<form method="post" action="${linkTo[MissaoController].listarPorCondutor}" enctype="multipart/form-data">
				<label for="servico.veiculo.id" >Selecione o condutor:</label>
				<select id="lstCondutores" name="condutorEscalado">
					<c:forEach items="${condutoresEscalados}" var="condutor">
						<option value="${condutor.id}" ${condutor.id == condutorEscalado.id ? 'selected' : ''}>${condutor.dadosParaExibicao}</option>
					</c:forEach>
				</select>
				<div id="btnAcoes" class="gt-table-buttons">
					<input id="btnFiltrar" type="submit" value="<fmt:message key='views.botoes.filtrar' />" class="gt-btn-medium gt-btn-left" />
				</div>
		</form>
	</div>
</p>
<c:if test="${(condutorEscalado != null) && (condutorEscalado.id != 0)}">
	<script type="text/javascript">trocaExibicaoCaixaBuscaAvancada();</script>
</c:if>
