package controllers;

import java.util.List;

import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Grupo;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.Combo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class Veiculos extends Controller {

	public static void listar() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		List<Veiculo> veiculos = Veiculo.listarTodos(cpOrgaoUsuario);
		render(veiculos);
	}

	public static void listarAvarias(Long idVeiculo) throws Exception {
		Avarias.montarListaDeAvariasPorVeiculo(idVeiculo);
		render("@Avarias.listarPorVeiculo");
	}

	@Before(priority = 200, only = { "incluir", "editar", "salvar" })
	protected static void montarCombos() throws Exception {
		// TODO: comentei
		// renderArgs = Combo.montar(renderArgs, Combo.Cor, Combo.Fornecedor);

		List<Grupo> grupos = Grupo.listarTodos();
		renderArgs.put(Combo.Grupo.getDescricao(), grupos);

		CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGIAntigo.titular().getOrgaoUsuario();
		List<DpLotacao> dpLotacoes = DpLotacao.AR.find("orgaoUsuario = ? and DATA_FIM_LOT is null order by NOME_LOTACAO", cpOrgaoUsuario).fetch();
		renderArgs.put("dpLotacoes", dpLotacoes);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(Veiculo veiculo) throws Exception {

		if (veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			Double odometroAnterior = veiculo.getUltimoOdometroDeLotacao();
			if (odometroAnterior > veiculo.getOdometroEmKmAtual()) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.maiorAnterior.validation");
			}
			if (veiculo.getOdometroEmKmAtual().equals(new Double(0))) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.zero.validation");
			}
		}

		validation.valid(veiculo);
		redirecionarSeErroAoSalvar(veiculo);

		veiculo.setCpOrgaoUsuario(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		veiculo.save();

		if (lotacaoDoVeiculoMudou(veiculo)) {
			LotacaoVeiculo.atualizarDataFimLotacaoAnterior(veiculo);
		}

		if (veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			// LotacaoVeiculo novalotacao = new LotacaoVeiculo(null, veiculo, veiculo.getLotacaoAtual(), Calendar.getInstance(), null, veiculo.getOdometroEmKmAtual()); TODO: metodo refatorado
			// novalotacao.save();
		}

		listar();
	}

	private static boolean veiculoNaoTemLotacaoCadastrada(Veiculo veiculo) {
		return veiculo.getLotacoes() == null;
	}

	private static boolean lotacaoDoVeiculoMudou(Veiculo veiculo) {
		// TODO: metodo refatorado
		// if (veiculo.getLotacoes() == null) {
		return true;
		// }
		// return (veiculo.getLotacoes().size() > 0) && (!veiculo.getLotacoes().get(0).getLotacao().equivale(veiculo.getLotacaoAtual()));
	}

	private static void redirecionarSeErroAoSalvar(Veiculo veiculo) throws Exception {
		if (Validation.hasErrors()) {
			montarCombos();
			MenuMontador.instance().recuperarMenuVeiculos(veiculo.getId(), ItemMenu.DADOSCADASTRAIS);
			String template = veiculo.getId() > 0 ? "@editar" : "@incluir";
			// TODO: metodo refatorado
			// if (veiculoNaoTemLotacaoCadastrada(veiculo) || veiculo.getLotacoes().isEmpty() || (!veiculo.getLotacoes().get(0).getLotacao().equivale(veiculo.getLotacaoAtual()))) {
			// RenderArgs.current().put("mostrarCampoOdometro", true);
		}
		// renderTemplate(template, veiculo);
		// }
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void incluir() {
		Veiculo veiculo = new Veiculo();
		// veiculo.setLotacaoAtual(new DpLotacao()); TODO: metodo refatorado
		MenuMontador.instance().recuperarMenuVeiculos(new Long(0), ItemMenu.DADOSCADASTRAIS);
		render(veiculo);
	}

	public static void buscarPeloId(Long Id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(Id);
		// veiculo.configurarLotacaoAtual(); TODO: metodo refatorado
		// veiculo.configurarOdometroParaMudancaDeLotacao();
		montarCombos();
		MenuMontador.instance().recuperarMenuVeiculos(Id, ItemMenu.DADOSCADASTRAIS);
		renderTemplate("@ler", veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		// veiculo.configurarLotacaoAtual(); // TODO: metodo refatorado
		// veiculo.configurarOdometroParaMudancaDeLotacao();
		MenuMontador.instance().recuperarMenuVeiculos(id, ItemMenu.DADOSCADASTRAIS);
		render(veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(id);
		veiculo.delete();
		listar();
	}

	public static void emdesenvolvimento() {
		render();
	}
}
