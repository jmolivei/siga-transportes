package br.gov.jfrj.siga.tp.vraptor;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.cp.model.DpLotacaoSelecao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Grupo;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.TipoDeCombustivel;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.Combo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.tp.util.Situacao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.common.base.Optional;

@Resource
@Path("/app/veiculos/")
public class VeiculoController extends TpController {

	public VeiculoController(HttpServletRequest request, Result result, Localization localization, Validator validator, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, validator, so, dadosAutorizacao, em);
	}

	@Path("/listar")
	public void listar() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = getTitular().getOrgaoUsuario();
		result.include("veiculos", Veiculo.listarTodos(cpOrgaoUsuario));
	}

//	@RoleAdmin
//	@RoleAdminFrota
	@Path("/salvar")
	public void salvar(@Valid final Veiculo veiculo) throws Exception {
		validarAntesDeSalvar(veiculo);
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
		result.redirectTo(this).listar();
	}

//	@RoleAdmin
//	@RoleAdminFrota
	@Path("/incluir")
	public void incluir() throws Exception {
		preencherResultComDadosPadrao(null);
		result.include("mostrarCampoOdometro", Boolean.FALSE);
		result.include("veiculo", new Veiculo(new DpLotacao()));
	}

//	@RoleAdmin
//	@RoleAdminFrota
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.configurarLotacaoAtual();
		preencherResultComDadosPadrao(id);
		result.include("veiculo", veiculo);
		result.include("mostrarCampoOdometro", Boolean.FALSE);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.delete();
		listar();
	}

	@Path("/ler/{id}")
	public void ler(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.configurarLotacaoAtual();
		veiculo.configurarOdometroParaMudancaDeLotacao();
		preencherResultComDadosPadrao(id);
		result.include("veiculo", veiculo);
	}

	@Path("/{idVeiculo}/avarias")
	public void listarAvarias(Long idVeiculo) throws Exception {
		result.redirectTo(AvariasController.class).listarPorVeiculo(idVeiculo);
	}

	private void preencherResultComDadosPadrao(Long id) throws Exception {
		Combo.montar(result, Combo.Cor, Combo.Fornecedor);
		
		result.include(Combo.Grupo.getDescricao(), Grupo.listarTodos());
		result.include("dpLotacoes", buscarDpLotacoes());
		result.include("situacoes", Situacao.values());
		result.include("respostasSimNao", PerguntaSimNao.values());
		result.include("lotacaoSel", new DpLotacaoSelecao());
		result.include("tiposDeCombustivel", TipoDeCombustivel.values());
		result.include("categoriasCNH", CategoriaCNH.values());

		MenuMontador
			.instance(result)
			.recuperarMenuVeiculos(id, ItemMenu.DADOSCADASTRAIS);
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

	private void validarAntesDeSalvar(Veiculo veiculo) {
		if (veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			final Double odometroAnterior = veiculo.getUltimoOdometroDeLotacao();
			final Double odometroEmKmAtual = Optional.fromNullable(veiculo.getOdometroEmKmAtual()).or(0D);

			error(odometroAnterior > odometroEmKmAtual, "odometroEmKmAtual", "veiculo.odometroEmKmAtual.maiorAnterior.validation");
			error(odometroEmKmAtual.equals(new Double(0)), "odometroEmKmAtual", "veiculo.odometroEmKmAtual.maiorAnterior.validation");
		}
	}

	private void redirecionarSeErroAoSalvar(Veiculo veiculo) throws Exception {
		if (validator.hasErrors()) {
			preencherResultComDadosPadrao(veiculo.getId());
			result.include("veiculo", veiculo);
			result.include("mostrarCampoOdometro", deveMostrarCampoOdometro(veiculo));

			if (veiculo.ehNovo()) {
				validator.onErrorUse(Results.page()).of(VeiculoController.class).incluir();
			} else {
				validator.onErrorUse(Results.page()).of(VeiculoController.class).editar(veiculo.getId());
			}
		}
	}

	private boolean deveMostrarCampoOdometro(Veiculo veiculo) {
		return veiculoNaoTemLotacaoCadastrada(veiculo) || veiculo.getLotacoes().isEmpty() || (!veiculo.getLotacoes().get(0).getLotacao().equivale(veiculo.getLotacaoAtual()));
	}
}
