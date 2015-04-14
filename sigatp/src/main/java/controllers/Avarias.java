package controllers;

import java.util.List;

import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;

@With(AutorizacaoGIAntigo.class)
public class Avarias extends Controller {
	private static final String ACTION_EDITAR = "@editar";

	public static void listar() {
		List<Avaria> avarias = Avaria.listarTodos();
		render(avarias);
	}

	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		montarListaDeAvariasPorVeiculo(idVeiculo);
		render();
	}

	protected static void montarListaDeAvariasPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		renderArgs.put("veiculo", veiculo);
		renderArgs.put("avarias", Avaria.buscarTodasPorVeiculo(veiculo));
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.AVARIAS);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void incluir(Long idVeiculo) throws Exception {
		Avaria avaria = new Avaria();
		boolean fixarVeiculo = false;
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		if (idVeiculo != null) {
			avaria.setVeiculo(Veiculo.AR.findById(idVeiculo));;
			fixarVeiculo = true;
			MenuMontador.instance().recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
		}
		render(avaria, veiculos, fixarVeiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria = Avaria.AR.findById(id);
		Veiculo veiculo = null;
		if (fixarVeiculo) {
			veiculo = avaria.getVeiculo();
		}
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		MenuMontador.instance().recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
		render(avaria, veiculos, veiculo, fixarVeiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(Avaria avaria, boolean fixarVeiculo) throws Exception {
		validation.valid(avaria);
		if (Validation.hasErrors()) {
			MenuMontador.instance().recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			Veiculo veiculo = Veiculo.AR.findById(avaria.getVeiculo().getId());
			renderTemplate(ACTION_EDITAR, avaria, veiculos, veiculo, fixarVeiculo);
		}

		if (avaria.podeCircular.equals(PerguntaSimNao.NAO)) {
			List<Missao> missoes = Missao.retornarMissoes("veiculo.getId()", avaria.getVeiculo().getId(), avaria.getVeiculo().getCpOrgaoUsuario().getId(), avaria.getDataDeRegistro(), avaria.getDataDeSolucao());
			String listaMissoes = "";
			String delimitador = "";

			for (Missao item : missoes) {
				listaMissoes += delimitador;
				listaMissoes += item.getSequence();
				delimitador = ",";
			}

			if (missoes.size() > 0) {
				Validation.addError("LinkErroVeiculo", listaMissoes);
			}
		}

		if (Validation.hasErrors()) {
			String template = avaria.getId() > 0 ? "Avarias/editar.html" : "Avarias/incluir.html";
			renderTemplate(template, avaria);
		} else {
			avaria.save();

			if (fixarVeiculo) {
				listarPorVeiculo(avaria.getVeiculo().getId());
			} else {
				listar();
			}
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria;
		avaria = Avaria.AR.findById(id);
		Veiculo veiculo = avaria.getVeiculo();
		avaria.delete();
		if (fixarVeiculo) {
			listarPorVeiculo(veiculo.getId());
		} else {
			listar();
		}
	}
}