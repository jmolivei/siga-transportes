package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ControleGabinete;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class ControlesGabinete extends Controller {

	@RoleGabinete
	@RoleAdminGabinete
	public static void listar() {
		List<ControleGabinete> controlesGabinete;
		if (AutorizacaoGIAntigo.ehAdminGabinete()) {
			controlesGabinete = ControleGabinete.listarTodos();
		} else {
			controlesGabinete = ControleGabinete.listarPorCondutor(Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario()));
		}

		render(controlesGabinete);
	}

	// Verificar se o MenuMontador é realmente utilizado
	@RoleGabinete
	@RoleAdminGabinete
	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<ControleGabinete> controleaGabinete = ControleGabinete.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.DADOSCADASTRAIS);
		render(controleaGabinete, veiculo);
	}

	@RoleGabinete
	@RoleAdminGabinete
	public static void incluir() throws Exception {
		List<Veiculo> veiculos = recuperarListaDeVeiculos();
		List<Condutor> condutores = recuperarListaDeCondutores();

		ControleGabinete controleGabinete = new ControleGabinete();
		render(controleGabinete, veiculos, condutores);
	}

	private static List<Veiculo> recuperarListaDeVeiculos() throws Exception {
		return Veiculo.listarFiltradoPor(AutorizacaoGIAntigo.titular().getOrgaoUsuario(), AutorizacaoGIAntigo.titular().getLotacao());
	}

	private static List<Condutor> recuperarListaDeCondutores() throws Exception {
		List<Condutor> condutores;
		if (AutorizacaoGIAntigo.ehAdminGabinete()) {
			condutores = Condutor.listarFiltradoPor(AutorizacaoGIAntigo.titular().getOrgaoUsuario(), AutorizacaoGIAntigo.titular().getLotacao());
		} else {
			condutores = new ArrayList<Condutor>();
			condutores.add(Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario()));
		}
		return condutores;
	}

	@RoleGabinete
	@RoleAdminGabinete
	public static void editar(Long id) throws Exception {
		ControleGabinete controleGabinete = ControleGabinete.findById(id);
		verificarAcesso(controleGabinete);
		List<Veiculo> veiculos = recuperarListaDeVeiculos();
		List<Condutor> condutores = recuperarListaDeCondutores();
		render(controleGabinete, veiculos, condutores);
	}

	private static void verificarAcesso(ControleGabinete controleGabinete) throws Exception {
		if (!(AutorizacaoGIAntigo.ehAdminGabinete() && AutorizacaoGIAntigo.titular().getLotacao().equivale(controleGabinete.titular.getLotacao()))
				&& !controleGabinete.titular.equivale(AutorizacaoGIAntigo.titular())) {
			throw new Exception(Messages.get("controlesGabinete.verificarAcesso.exception"));
		}
	}

	private static void verificarOdometrosSaidaRetorno(ControleGabinete controleGabinete) {
		if (controleGabinete.odometroEmKmSaida > controleGabinete.odometroEmKmRetorno) {
			Validation.addError("odometroEmKmRetorno", "controlesGabinete.odometroEmKmRetorno.validation");
		}
	}

	private static void verificarDatasInicialFinal(ControleGabinete controleGabinete) throws Exception {
		if (controleGabinete.dataHoraSaida == null || controleGabinete.dataHoraRetorno == null) {
			Validation.addError("dataHoraSaida", "controlesGabinete.dataHoraSaida.validation");
			return;
		}

		Boolean dataSaidaAntesDeDataRetorno = controleGabinete.dataHoraSaida.before(controleGabinete.dataHoraRetorno);
		if (!dataSaidaAntesDeDataRetorno) {
			Validation.addError("dataHoraRetorno", "controlesGabinete.dataSaidaAntesDeDataRetorno.validation");
		}
	}

	private static void verificarOdometroRetornoControleAnterior(ControleGabinete controleGabinete) {
		double ultimoOdometroDesteVeiculo = ControleGabinete.buscarUltimoOdometroPorVeiculo(controleGabinete.veiculo, controleGabinete);
		if (controleGabinete.odometroEmKmSaida < ultimoOdometroDesteVeiculo) {
			Validation.addError("odometroEmKmSaida", "controlesGabinete.odometroEmKmSaida.validation");
		}
	}

	@RoleGabinete
	@RoleAdminGabinete
	public static void salvar(@Valid ControleGabinete controleGabinete) throws Exception {
		if (!controleGabinete.id.equals(new Long(0))) { // somente na alteracao
			verificarAcesso(controleGabinete);
		}
		verificarOdometroRetornoControleAnterior(controleGabinete);
		verificarOdometrosSaidaRetorno(controleGabinete);
		verificarDatasInicialFinal(controleGabinete);

		if (Validation.hasErrors()) {
			List<Veiculo> veiculos = recuperarListaDeVeiculos();
			List<Condutor> condutores = recuperarListaDeCondutores();
			String template;
			template = controleGabinete.id > 0 ? "ControlesGabinete/editar.html" : "ControlesGabinete/incluir.html";
			renderTemplate(template, controleGabinete, veiculos, condutores);
		} else {
			if (controleGabinete.id == 0) {
				controleGabinete.dataHora = Calendar.getInstance();
			}

			// if(controleGabinete.id.equals(new Long(0))) { // somente na inclusao
			controleGabinete.solicitante = AutorizacaoGIAntigo.cadastrante();
			controleGabinete.titular = AutorizacaoGIAntigo.titular();
			// }

			controleGabinete.save();
			listar();
		}
	}

	@RoleGabinete
	@RoleAdminGabinete
	public static void excluir(Long id) throws Exception {
		ControleGabinete controleGabinete = ControleGabinete.findById(id);
		verificarAcesso(controleGabinete);
		controleGabinete.delete();
		listar();
	}

}
