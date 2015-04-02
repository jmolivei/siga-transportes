package controllers;

import java.util.List;

import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.RelatorioDiario;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGIAntigo.RoleAdmin;
import controllers.AutorizacaoGIAntigo.RoleAdminFrota;

@With(AutorizacaoGIAntigo.class)
public class RelatoriosDiarios extends Controller {
	
	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<RelatorioDiario> relatoriosDiarios = RelatorioDiario.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.RELATORIOSDIARIOS);
		render(relatoriosDiarios, veiculo);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void incluir(Long idVeiculo){
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		RelatorioDiario relatorioDiario = new RelatorioDiario();
		relatorioDiario.veiculo = veiculo;
		render(relatorioDiario);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id){
		RelatorioDiario relatorioDiario = RelatorioDiario.findById(id);
		render(relatorioDiario);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(@Valid RelatorioDiario relatorioDiario) throws Exception{
		if(Validation.hasErrors()){
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			String template;
			template = relatorioDiario.id > 0 ? "@editar" : "@incluir";
			renderTemplate(template, relatorioDiario,  veiculos);
		}
		else{
			relatorioDiario.save();
			listarPorVeiculo(relatorioDiario.veiculo.id);
		}	
	}	
	
	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id){
		RelatorioDiario relatorioDiario = RelatorioDiario.findById(id);
		relatorioDiario.delete();
		listarPorVeiculo(relatorioDiario.veiculo.id);		
	}


}
