<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="/sigatp/public/stylesheets/andamentos.css" type="text/css" media="screen"/>
<p class="gt-table-action-list">
	<c:if test="${menuRequisicoesMostrarTodas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_T" id="menuRequisicoesMostrarTodas" href="${linkTo[RequisicaoController].listar}"></a>
   		<a href="${linkTo[RequisicaoController].listar}"><U>T</U>odas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAutorizadasENaoAtendidas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_na_A" id="menuRequisicoesMostrarAutorizadasENaoAtendidas" href="${linkTo[RequisicaoController].listarFiltrado['AUTORIZADA']['NAOATENDIDA']}">AN</a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['AUTORIZADA']['NAOATENDIDA']}">A<U>u</U>torizadas/<U>N</U>&atilde;o Atendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAbertas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_B" id="menuRequisicoesMostrarAbertas" href="${linkTo[RequisicaoController].listarFiltrado['ABERTA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['ABERTA']}">A<U>b</U>ertas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAutorizadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_U" id="menuRequisicoesMostrarAutorizadas" href="${linkTo[RequisicaoController].listarFiltrado['AUTORIZADA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['AUTORIZADA']}">A<U>u</U>torizadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarRejeitadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_R" id="menuRequisicoesMostrarRejeitadas" href="${linkTo[RequisicaoController].listarFiltrado['REJEITADA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['REJEITADA']}"><U>R</U>ejeitadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarProgramadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_P" id="menuRequisicoesMostrarProgramadas" href="${linkTo[RequisicaoController].listarFiltrado['PROGRAMADA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['PROGRAMADA']}"><U>P</U>rogramadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarEmAtendimento}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_E" id=menuRequisicoesMostrarEmAtendimento href="${linkTo[RequisicaoController].listarFiltrado['EMATENDIMENTO']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['EMATENDIMENTO']}"><U>E</U>m Atendimento</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAtendidas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_A" id="menuRequisicoesMostrarAtendidas" href="${linkTo[RequisicaoController].listarFiltrado['ATENDIDA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['ATENDIDA']}"><U>A</U>tendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarNaoAtendidas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_N" id=menuRequisicoesMostrarNaoAtendidas href="${linkTo[RequisicaoController].listarFiltrado['NAOATENDIDA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['NAOATENDIDA']}"><U>N</U>&atilde;o Atendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarCanceladas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_C" id="menuRequisicoesMostrarCanceladas" href="${linkTo[RequisicaoController].listarFiltrado['CANCELADA']}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado['CANCELADA']}"><U>C</U>anceladas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
</p>