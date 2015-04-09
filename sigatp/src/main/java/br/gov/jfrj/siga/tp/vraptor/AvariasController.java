package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.data.validation.Validation;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

// TODO: ainda nao implementado. Criado apenas para utilizar o metodo necessario na controller de veiculo
@Resource
@Path("/app/avarias")
public class AvariasController extends SigaController {
	private static final String ACTION_EDITAR = "@editar";

	public AvariasController(HttpServletRequest request, Result result, CpDao dao, SigaObjects so, EntityManager em) {
		super(request, result, dao, so, em);
	}

	public void listar() {
		List<Avaria> avarias = Avaria.listarTodos();
		// render(avarias); TODO:
	}
	
	@Path("/listarPorVeiculo/{idVeiculo}")
	public void listarPorVeiculo(Long idVeiculo) throws Exception {
		montarListaDeAvariasPorVeiculo(idVeiculo);
		// render(); TODO:
	}

	protected void montarListaDeAvariasPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		// renderArgs.put("veiculo", veiculo); TODO:
		// renderArgs.put("avarias", Avaria.buscarTodasPorVeiculo(veiculo)); TODO:
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.AVARIAS);
	}

	@RoleAdmin
	@RoleAdminFrota
	public void incluir(Long idVeiculo) throws Exception {
		Avaria avaria = new Avaria();
		boolean fixarVeiculo = false;
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		if (idVeiculo != null) {
			avaria.veiculo = Veiculo.AR.findById(idVeiculo);
			fixarVeiculo = true;
			MenuMontador.instance().recuperarMenuVeiculos(avaria.veiculo.getId(), ItemMenu.AVARIAS);
		}
		// render(avaria, veiculos, fixarVeiculo); TODO:
	}

	@RoleAdmin
	@RoleAdminFrota
	public void editar(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria = Avaria.findById(id);
		Veiculo veiculo = null;
		if (fixarVeiculo) {
			veiculo = avaria.veiculo;
		}
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		MenuMontador.instance().recuperarMenuVeiculos(avaria.veiculo.getId(), ItemMenu.AVARIAS);
		// render(avaria, veiculos, veiculo, fixarVeiculo); TODO:
	}

	@RoleAdmin
	@RoleAdminFrota
	public void salvar(Avaria avaria, boolean fixarVeiculo) throws Exception {
		// validation.valid(avaria);
		if (Validation.hasErrors()) {
			MenuMontador.instance().recuperarMenuVeiculos(avaria.veiculo.getId(), ItemMenu.AVARIAS);
			List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
			Veiculo veiculo = Veiculo.AR.findById(avaria.veiculo.getId());
			// renderTemplate(ACTION_EDITAR, avaria, veiculos, veiculo, fixarVeiculo); TODO:
		}

		if (avaria.podeCircular.equals(PerguntaSimNao.NAO)) {
			List<Missao> missoes = Missao.retornarMissoes("veiculo.getId()", avaria.veiculo.getId(), avaria.veiculo.getCpOrgaoUsuario().getId(), avaria.dataDeRegistro, avaria.dataDeSolucao);
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
			// renderTemplate(template, avaria); TODO:
		} else {
			avaria.save();

			if (fixarVeiculo) {
				listarPorVeiculo(avaria.veiculo.getId());
			} else {
				listar();
			}
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	public void excluir(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria;
		avaria = Avaria.findById(id);
		Veiculo veiculo = avaria.veiculo;
		avaria.delete();
		if (fixarVeiculo) {
			listarPorVeiculo(veiculo.getId());
		} else {
			listar();
		}
	}
}