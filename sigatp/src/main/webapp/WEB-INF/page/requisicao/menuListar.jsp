<%@ page import="br.gov.jfrj.siga.tp.model.EstadoRequisicao" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" href="/public/stylesheets/andamentos.css" type="text/css" media="screen"/>
<p class="gt-table-action-list">
	<c:if test="${menuRequisicoesMostrarTodas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_T" id="menuRequisicoesMostrarTodas" href="${linkTo[RequisicaoController].listar}"></a>
   		<a href="${linkTo[RequisicaoController].listar}"><U>T</U>odas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAutorizadasENaoAtendidas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_na_A" id="menuRequisicoesMostrarAutorizadasENaoAtendidas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.AUTORIZADA][EstadoRequisicao.NAOATENDIDA]}">AN</a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.AUTORIZADA][EstadoRequisicao.NAOATENDIDA]}">A<U>u</U>torizadas/<U>N</U>&atilde;o Atendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAbertas}">
		<img src="/sigatp/public/images/filter-icon.png"/>
		<a class="filtro_B" id="menuRequisicoesMostrarAbertas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.ABERTA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.ABERTA]}">A<U>b</U>ertas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAutorizadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_U" id="menuRequisicoesMostrarAutorizadas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.AUTORIZADA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.AUTORIZADA]}">A<U>u</U>torizadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarRejeitadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_R" id="menuRequisicoesMostrarRejeitadas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.REJEITADA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.REJEITADA]}"><U>R</U>ejeitadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarProgramadas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_P" id="menuRequisicoesMostrarProgramadas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.PROGRAMADA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.PROGRAMADA]}"><U>P</U>rogramadas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarEmAtendimento}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_E" id=menuRequisicoesMostrarEmAtendimento href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.EMATENDIMENTO]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.EMATENDIMENTO]}"><U>E</U>m Atendimento</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarAtendidas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_A" id="menuRequisicoesMostrarAtendidas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.ATENDIDA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.ATENDIDA]}"><U>A</U>tendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarNaoAtendidas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_N" id=menuRequisicoesMostrarNaoAtendidas href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.NAOATENDIDA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.NAOATENDIDA]}"><U>N</U>&atilde;o Atendidas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRequisicoesMostrarCanceladas}">
   		<img src="/sigatp/public/images/filter-icon.png"/>
   		<a class="filtro_C" id="menuRequisicoesMostrarCanceladas" href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.CANCELADA]}"></a>
		<a href="${linkTo[RequisicaoController].listarFiltrado[EstadoRequisicao.CANCELADA]}"><U>C</U>anceladas</a>&nbsp;&nbsp;&nbsp;
	</c:if>
</p>