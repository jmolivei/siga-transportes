package br.gov.jfrj.siga.tp.util;

import play.mvc.Scope.RenderArgs;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.tp.model.EstadoMissao;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.EstadoServico;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import controllers.AutorizacaoGIAntigo;

public class MenuMontador {

	private Result result;

	private MenuMontador(Result result) {
		this.result = result;
	}
//
	public void recuperarMenuVeiculos(Long id, ItemMenu menuVeiculos) {
		RenderArgs.current().put("idVeiculo", id);
		RenderArgs.current().put("menuVeiculosIncluir", (id == 0));
		RenderArgs.current().put("menuVeiculosEditar", (id != 0) && (menuVeiculos != ItemMenu.DADOSCADASTRAIS));
		RenderArgs.current().put("menuAvarias", (id != 0) && (menuVeiculos != ItemMenu.AVARIAS));
		RenderArgs.current().put("menuRelatoriosdiarios", (id != 0) && (menuVeiculos != ItemMenu.RELATORIOSDIARIOS));
		RenderArgs.current().put("menuAgenda", (id != 0) && (menuVeiculos != ItemMenu.AGENDA));
		RenderArgs.current().put("menuAbastecimentos", (id != 0) && (menuVeiculos != ItemMenu.ABASTECIMENTOS));
		RenderArgs.current().put("menuAutosdeinfracoes", (id != 0) && (menuVeiculos != ItemMenu.INFRACOES));
		RenderArgs.current().put("menuLotacoes", (id != 0) && (menuVeiculos != ItemMenu.LOTACOES));
	}

	public void recuperarMenuCondutores(Long id, ItemMenu menuCondutor) {
		RenderArgs.current().put("idCondutor", id);
		RenderArgs.current().put("menuCondutoresIncluir", (id == 0));
		RenderArgs.current().put("menuCondutoresEditar", (id != 0) && (menuCondutor != ItemMenu.DADOSCADASTRAIS));
		RenderArgs.current().put("menuPlantoes", (id != 0) && (menuCondutor != ItemMenu.PLANTOES));
		RenderArgs.current().put("menuAfastamentos", (id != 0) && (menuCondutor != ItemMenu.AFASTAMENTOS));
		RenderArgs.current().put("menuEscalasDeTrabalho", (id != 0) && (menuCondutor != ItemMenu.ESCALASDETRABALHO));
		RenderArgs.current().put("menuAgenda", (id != 0) && (menuCondutor != ItemMenu.AGENDA));
		RenderArgs.current().put("menuInfracoes", (id != 0) && (menuCondutor != ItemMenu.INFRACOES));
	}

	public void recuperarMenuRequisicoes(Long id, boolean popUp, boolean mostrarBotaoRequisicao) {
		RenderArgs.current().put("idRequisicao", id);
		RenderArgs.current().put("popUp", popUp);
		if (!popUp) {
			RenderArgs.current().put("menuRequisicoesIncluir", (id == null));
			RenderArgs.current().put("menuRequisicoesEditar", (id != null));
			RenderArgs.current().put("menuRequisicoesCancelar", (id != null));
		}
		if (mostrarBotaoRequisicao) {
			RenderArgs.current().put("menuRequisicoesMostrarRequisicao", (id != null));
		} else {
			RenderArgs.current().put("menuRequisicoesListarAndamentos", (id != null));
		}

	}

	public RenderArgs recuperarMenuMissoes(EstadoMissao estado) {
		RenderArgs.current().put("menuMissoesMostrarVoltar", false);
		RenderArgs.current().put("menuMissoesMostrarTodas", (estado != null));
		RenderArgs.current().put("menuMissoesMostrarFinalizadas", (estado != EstadoMissao.FINALIZADA));
		RenderArgs.current().put("menuMissoesMostrarIniciadas", (estado != EstadoMissao.INICIADA));
		RenderArgs.current().put("menuMissoesMostrarProgramadas", (estado != EstadoMissao.PROGRAMADA));
		RenderArgs.current().put("menuMissoesMostrarCanceladas", (estado != EstadoMissao.CANCELADA));

		RenderArgs.current().put("menuMissoesMostrarFiltrarPorCondutor", true);

		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuMissoesPorCondutor() {
		RenderArgs.current().put("menuMissoesMostrarVoltar", true);

		RenderArgs.current().put("menuMissoesMostrarTodas", false);
		RenderArgs.current().put("menuMissoesMostrarFinalizadas", false);
		RenderArgs.current().put("menuMissoesMostrarIniciadas", false);
		RenderArgs.current().put("menuMissoesMostrarProgramadas", false);
		RenderArgs.current().put("menuMissoesMostrarCanceladas", false);

		RenderArgs.current().put("menuMissoesMostrarFiltrarPorCondutor", true);

		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuFinalidades(boolean mostrarBotaoTodas) {
		RenderArgs.current().put("menuFinalidadesMostrarVoltar", !mostrarBotaoTodas);
		RenderArgs.current().put("menuFinalidadesMostrarTodas", mostrarBotaoTodas);

		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuListarRequisicoes(EstadoRequisicao estado) {
		return recuperarMenuListarRequisicoes(estado, estado);
	}

	public RenderArgs recuperarMenuListarPAprovarRequisicoes(EstadoRequisicao estado) {
		RenderArgs.current().put("menuRequisicoesMostrarTodas", (estado != null));
		RenderArgs.current().put("menuRequisicoesMostrarAbertas", (estado != EstadoRequisicao.ABERTA));
		RenderArgs.current().put("menuRequisicoesMostrarAutorizadas", (estado != EstadoRequisicao.AUTORIZADA));
		RenderArgs.current().put("menuRequisicoesMostrarRejeitadas", (estado != EstadoRequisicao.REJEITADA));

		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuMissao(Long id, EstadoMissao estado) {
		RenderArgs.current().put("idMissao", id);
		RenderArgs.current().put("menuMissaoEditar", ((estado == EstadoMissao.PROGRAMADA) || (estado == EstadoMissao.INICIADA)));
		if (AutorizacaoGIAntigo.ehAdministrador()) {
			RenderArgs.current().put("menuMissaoCancelar", (estado == EstadoMissao.PROGRAMADA));
		} else {
			RenderArgs.current().put("menuMissaoCancelar", false);
		}
		RenderArgs.current().put("menuMissaoIniciar", (estado == EstadoMissao.PROGRAMADA));
		RenderArgs.current().put("menuMissaoFinalizar", (estado == EstadoMissao.INICIADA));
		return RenderArgs.current();
	}

	public static MenuMontador instance(Result result) {
		return new MenuMontador(result);
	}

	public static MenuMontador instance() {
		return new MenuMontador(null);
	}

	public RenderArgs recuperarMenuServicoVeiculo(Long id, EstadoServico estado) {
		RenderArgs.current().put("idServico", id);
		RenderArgs.current().put("menuServicoVeiculoEditar", (estado == EstadoServico.AGENDADO) || (estado == EstadoServico.INICIADO));
		RenderArgs.current().put("menuServicoVeiculoExcluir", (estado == EstadoServico.AGENDADO));
		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuServicosVeiculo(EstadoServico estado) {
		RenderArgs.current().put("menuServicosVeiculoMostrarTodos", (estado != null));
		RenderArgs.current().put("menuServicosVeiculoMostrarRealizados", (estado != EstadoServico.REALIZADO));
		RenderArgs.current().put("menuServicosVeiculoMostrarIniciados", (estado != EstadoServico.INICIADO));
		RenderArgs.current().put("menuServicosVeiculoMostrarAgendados", (estado != EstadoServico.AGENDADO));
		RenderArgs.current().put("menuServicosVeiculoMostrarCancelados", (estado != EstadoServico.CANCELADO));

		return RenderArgs.current();
	}

	public RenderArgs recuperarMenuListarRequisicoes(EstadoRequisicao estadoRequisicao, EstadoRequisicao estadoRequisicaoP) {
		RenderArgs.current().put("menuRequisicoesMostrarTodas", (estadoRequisicao != null && estadoRequisicaoP != null));
		RenderArgs.current().put("menuRequisicoesMostrarAutorizadasENaoAtendidas", (estadoRequisicao != EstadoRequisicao.AUTORIZADA && estadoRequisicaoP != EstadoRequisicao.NAOATENDIDA));
		RenderArgs.current().put("menuRequisicoesMostrarAbertas", (estadoRequisicao != EstadoRequisicao.ABERTA && estadoRequisicaoP != EstadoRequisicao.ABERTA));
		RenderArgs.current().put("menuRequisicoesMostrarAutorizadas", (estadoRequisicao != EstadoRequisicao.AUTORIZADA && estadoRequisicaoP != EstadoRequisicao.AUTORIZADA));
		RenderArgs.current().put("menuRequisicoesMostrarRejeitadas", (estadoRequisicao != EstadoRequisicao.REJEITADA && estadoRequisicaoP != EstadoRequisicao.REJEITADA));
		RenderArgs.current().put("menuRequisicoesMostrarProgramadas", (estadoRequisicao != EstadoRequisicao.PROGRAMADA && estadoRequisicaoP != EstadoRequisicao.PROGRAMADA));
		RenderArgs.current().put("menuRequisicoesMostrarEmAtendimento", (estadoRequisicao != EstadoRequisicao.EMATENDIMENTO && estadoRequisicaoP != EstadoRequisicao.EMATENDIMENTO));
		RenderArgs.current().put("menuRequisicoesMostrarAtendidas", (estadoRequisicao != EstadoRequisicao.ATENDIDA && estadoRequisicaoP != EstadoRequisicao.ATENDIDA));
		RenderArgs.current().put("menuRequisicoesMostrarNaoAtendidas", (estadoRequisicao != EstadoRequisicao.NAOATENDIDA && estadoRequisicaoP != EstadoRequisicao.NAOATENDIDA));
		RenderArgs.current().put("menuRequisicoesMostrarCanceladas", (estadoRequisicao != EstadoRequisicao.CANCELADA && estadoRequisicaoP != EstadoRequisicao.CANCELADA));

		return RenderArgs.current();
	}
}
