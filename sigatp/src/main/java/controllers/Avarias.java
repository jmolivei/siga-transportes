package controllers;

import java.util.List;

import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGIAntigo.RoleAdmin;
import controllers.AutorizacaoGIAntigo.RoleAdminFrota;

@With(AutorizacaoGIAntigo.class)
public class Avarias extends Controller {
	private static final String ACTION_EDITAR = "@editar";

	public static void listar() {
		List<Avaria> avarias = Avaria.listarTodos();
		render(avarias);
	}

	public static void listarPorVeiculo(Long idVeiculo) {
		montarListaDeAvariasPorVeiculo(idVeiculo);
		render();
	}
	
	protected static void montarListaDeAvariasPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		renderArgs.put("veiculo",veiculo);
		renderArgs.put("avarias",Avaria.buscarTodasPorVeiculo(veiculo));
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.AVARIAS);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void incluir(Long idVeiculo) throws Exception {
		Avaria avaria = new Avaria();
		boolean fixarVeiculo = false;
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		if (idVeiculo != null) {
			avaria.veiculo = Veiculo.findById(idVeiculo);
			fixarVeiculo = true;
			MenuMontador.instance().RecuperarMenuVeiculos(avaria.veiculo.id, ItemMenu.AVARIAS);
		}
    	render(avaria, veiculos, fixarVeiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria = Avaria.findById(id);
		Veiculo veiculo = null;
		if (fixarVeiculo) {
			veiculo = avaria.veiculo;
		}
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		MenuMontador.instance().RecuperarMenuVeiculos(avaria.veiculo.id, ItemMenu.AVARIAS);
		render(avaria, veiculos, veiculo, fixarVeiculo);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(Avaria avaria, boolean fixarVeiculo) throws Exception {
		validation.valid(avaria);
		if(Validation.hasErrors()) 
		{
			MenuMontador.instance().RecuperarMenuVeiculos(avaria.veiculo.id, ItemMenu.AVARIAS);
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			Veiculo veiculo = Veiculo.findById(avaria.veiculo.id);
			renderTemplate(ACTION_EDITAR, avaria, veiculos, veiculo, fixarVeiculo);
		}
		
		if (avaria.podeCircular.equals(PerguntaSimNao.NAO)) {
			List<Missao> missoes = Missao.retornarMissoes("veiculo.id", avaria.veiculo.id, 
					avaria.veiculo.cpOrgaoUsuario.getId(), avaria.dataDeRegistro, avaria.dataDeSolucao);
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
			String template = avaria.id > 0 ? "Avarias/editar.html" : "Avarias/incluir.html";
			renderTemplate(template, avaria);
		}
		else {
			avaria.save();

			if (fixarVeiculo) {
				listarPorVeiculo(avaria.veiculo.id);
			} else {
				listar();
			}
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id, boolean fixarVeiculo) {
		Avaria avaria;
		avaria = Avaria.findById(id);
		Veiculo veiculo = avaria.veiculo;
		avaria.delete();
		if (fixarVeiculo) {
			listarPorVeiculo(veiculo.id);
		} else {
			listar();
		}
	}
}