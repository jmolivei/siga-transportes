<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="/sigatp/public/stylesheets/andamentos.css" type="text/css" media="screen"/>

<p class="gt-table-action-list">
	<c:if test="${menuRequisicoesMostrarTodas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_T" id="menuRequisicoesAprovarMostrarTodas" href="${linkTo[RequisicaoController].listarPAprovar}">+</a>
		<a href="${linkTo[RequisicaoController].listarPAprovar}">Todas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAbertas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_B" id="menuRequisicoesAprovarMostrarAbertas" href="${linkTo[RequisicaoController].listarPAprovarFiltrado['ABERTA']}">B</a>
		<a href="${linkTo[RequisicaoController].listarPAprovarFiltrado['ABERTA']}">A<U>b</U>ertas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAutorizadas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_U" id="menuRequisicoesAprovarMostrarAutorizadas" href="${linkTo[RequisicaoController].listarPAprovarFiltrado['AUTORIZADA']}">U</a>
		<a href="${linkTo[RequisicaoController].listarPAprovarFiltrado['AUTORIZADA']}">A<U>u</U>torizadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarRejeitadas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_R" id="menuRequisicoesAprovarMostrarRejeitadas" href="${linkTo[RequisicaoController].listarPAprovarFiltrado['REJEITADA']}">R</a>
		<a href="${linkTo[RequisicaoController].listarPAprovarFiltrado['REJEITADA']}"><U>R</U>ejeitadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
</p>