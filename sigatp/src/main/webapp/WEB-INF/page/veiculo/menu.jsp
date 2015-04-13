<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<p class="gt-table-action-list">
	<c:if test="${menuVeiculosIncluir}"> <a class="once"
		href="${linkTo[VeiculoController].incluir}"> <img
		src="/sigatp/public/images/editaricon.png"
		style="margin-right: 5px;">Dados Cadastrais</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuVeiculosEditar}"> <a class="once"
		href="@{Veiculos.editar(idVeiculo)}"> <img
		src="/sigatp/public/images/editaricon.png"
		style="margin-right: 5px;">Dados Cadastrais</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<!--  	href="@{Veiculos.listarAvarias(idVeiculo)}"> -->
	<c:if test="${menuAvarias}"> <a class="once"
		href="@{Avarias.listarPorVeiculo(idVeiculo)}"> <img 
		src="/sigatp/public/images/avariasicon.png"
		style="margin-right: 5px;">Avarias</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuRelatoriosdiarios}"> <a class="once"
		href="@{RelatoriosDiarios.listarPorVeiculo(idVeiculo)}"> <img
		src="/sigatp/public/images/relatoriosicon.png"
		style="margin-right: 5px;">Relat&oacute;rios Di&aacute;rios</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuAgenda}"> <a class="once"
		href="@{Agendas.listarPorVeiculo(idVeiculo)}"> <img
		src="/sigatp/public/images/agendaicon.png"
		style="margin-right: 5px;">Agenda</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuAbastecimentos}"> <a class="once"
		href="@{Abastecimentos.listarPorVeiculo(idVeiculo)}"> <img
		src="/sigatp/public/images/abastecimentoicon.png"
		style="margin-right: 5px;">Abastecimentos</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${menuAutosdeinfracoes}"> <a class="once"
		href="${linkTo[AutoDeInfracaoController].listarPorVeiculo[idVeiculo]}"> <img
		src="/sigatp/public/images/infracoesicon.png"
		style="margin-right: 5px;">Acess&oacute;rios</a>&nbsp;&nbsp;&nbsp;
	</c:if>	
	<c:if test="${menuLotacoes}"> <a class="once"
		href="@{LotacoesVeiculo.listarPorVeiculo(idVeiculo)}"> <img
		src="/sigatp/public/images/lotacoesicon.png"
		style="margin-right: 5px;">Lota&ccedil;&otilde;es</a>&nbsp;&nbsp;&nbsp;
	</c:if>	
</p>