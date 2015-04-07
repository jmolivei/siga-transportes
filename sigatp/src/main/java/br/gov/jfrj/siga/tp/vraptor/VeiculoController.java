package br.gov.jfrj.siga.tp.vraptor;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Scope.RenderArgs;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Grupo;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.TpModel;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.Combo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/veiculos/")
public class VeiculoController extends TpController {

	public VeiculoController(HttpServletRequest request, Result result, Localization localization, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, so, dadosAutorizacao, em);
	}

	@Path("/listar")
	public void listar() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		result.include("veiculos", Veiculo.listarTodos(cpOrgaoUsuario));
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/salvar")
	public void salvar(Veiculo veiculo) throws Exception {
		montarCombos();

		if (veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			Double odometroAnterior = veiculo.getUltimoOdometroDeLotacao();
			if (odometroAnterior > veiculo.getOdometroEmKmAtual()) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.maiorAnterior.validation");
			}
			if (veiculo.getOdometroEmKmAtual().equals(new Double(0))) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.zero.validation");
			}
		}

		// TODO: chamar validacao
		// validation.valid(veiculo);
		redirecionarSeErroAoSalvar(veiculo);

		veiculo.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		veiculo.save();

		if (lotacaoDoVeiculoMudou(veiculo)) {
			LotacaoVeiculo.atualizarDataFimLotacaoAnterior(veiculo);
		}

		if (veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			LotacaoVeiculo novalotacao = new LotacaoVeiculo(null, veiculo, veiculo.getLotacaoAtual(), Calendar.getInstance(), null, veiculo.getOdometroEmKmAtual());
			novalotacao.save();
		}
		listar();
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/incluir")
	public void incluir() throws Exception {
		Veiculo veiculo = new Veiculo();
		veiculo.setLotacaoAtual(new DpLotacao());
		MenuMontador.instance(result).recuperarMenuVeiculos(TpModel.VAZIO, ItemMenu.DADOSCADASTRAIS);
		montarCombos();
		// render(veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.configurarLotacaoAtual();
		// veiculo.configurarOdometroParaMudancaDeLotacao();
		MenuMontador.instance(result).recuperarMenuVeiculos(id, ItemMenu.DADOSCADASTRAIS);
		montarCombos();
		// render(veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.delete();
		listar();
	}

	// public void buscarPeloId(Long Id) throws Exception {
	// Veiculo veiculo = Veiculo.AR.findById(Id);
	// veiculo.configurarLotacaoAtual();
	// veiculo.configurarOdometroParaMudancaDeLotacao();
	// montarCombos();
	// MenuMontador.instance(result).recuperarMenuVeiculos(Id, ItemMenu.DADOSCADASTRAIS);
	// renderTemplate("@ler", veiculo);
	// }

	public void listarAvarias(Long idVeiculo) {
		// Avarias.montarListaDeAvariasPorVeiculo(idVeiculo);
		// render("@Avarias.listarPorVeiculo");
	}

	@Before(priority = 200, only = { "incluir", "editar", "salvar" })
	private void montarCombos() throws Exception {
		Combo.montar(result, Combo.Cor, Combo.Fornecedor);
		result.include(Combo.Grupo.getDescricao(), Grupo.listarTodos());
		result.include("dpLotacoes", buscarDpLotacoes());
	}

	private List<DpLotacao> buscarDpLotacoes() {
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		List<DpLotacao> dpLotacoes = DpLotacao.AR.find("orgaoUsuario = ? and DATA_FIM_LOT is null order by NOME_LOTACAO", cpOrgaoUsuario).fetch();
		return dpLotacoes;
	}

	private boolean veiculoNaoTemLotacaoCadastrada(Veiculo veiculo) {
		return veiculo.getLotacoes() == null;
	}

	private boolean lotacaoDoVeiculoMudou(Veiculo veiculo) {
		if (veiculo.getLotacoes() == null) {
			return true;
		}
		return (veiculo.getLotacoes().size() > 0) && (!veiculo.getLotacoes().get(0).getLotacao().equivale(veiculo.getLotacaoAtual()));
	}

	private void redirecionarSeErroAoSalvar(Veiculo veiculo) throws Exception {
		if (Validation.hasErrors()) {
			montarCombos();
			MenuMontador.instance(result).recuperarMenuVeiculos(veiculo.getId(), ItemMenu.DADOSCADASTRAIS);
			String template = veiculo.getId() > 0 ? "@editar" : "@incluir";
			if (veiculoNaoTemLotacaoCadastrada(veiculo) || veiculo.getLotacoes().isEmpty() || (!veiculo.getLotacoes().get(0).getLotacao().equivale(veiculo.getLotacaoAtual()))) {
				RenderArgs.current().put("mostrarCampoOdometro", true);
			}
			// renderTemplate(template, veiculo);
		}
	}

	// public void emdesenvolvimento() {
	// render();
	// }
}
