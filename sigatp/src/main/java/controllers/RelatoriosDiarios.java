package controllers;

import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.RelatorioDiario;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class RelatoriosDiarios extends Controller {

	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<RelatorioDiario> relatoriosDiarios = RelatorioDiario.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.RELATORIOSDIARIOS);
		render(relatoriosDiarios, veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void incluir(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		RelatorioDiario relatorioDiario = new RelatorioDiario();
		relatorioDiario.setVeiculo(veiculo);
		render(relatorioDiario);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id) throws Exception {
		RelatorioDiario relatorioDiario = RelatorioDiario.AR.findById(id);
		render(relatorioDiario);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(@Valid RelatorioDiario relatorioDiario) throws Exception {
		if (Validation.hasErrors()) {
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			String template;
			template = relatorioDiario.getId() > 0 ? "@editar" : "@incluir";
			renderTemplate(template, relatorioDiario, veiculos);
		} else {
			relatorioDiario.save();
			listarPorVeiculo(relatorioDiario.getVeiculo().getId());
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id) throws Exception {
		RelatorioDiario relatorioDiario = RelatorioDiario.AR.findById(id);
		relatorioDiario.delete();
		listarPorVeiculo(relatorioDiario.getVeiculo().getId());
	}

}
