package controllers;

import java.util.List;

import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(AutorizacaoGIAntigo.class)
public class Gabinetes extends Controller {

	public static void listar() {
		List<Abastecimento> abastecimentos = Abastecimento.listarTodos();
		render(abastecimentos);
	}	

	//Verificar se o MenuMontador é realmente utilizado
	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<Abastecimento> abastecimentos = Abastecimento.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.DADOSCADASTRAIS);
		render(abastecimentos, veiculo);
	}

	@AutorizacaoGIAntigo.RoleAdmin
	public static void incluir() throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		Abastecimento abastecimento = new Abastecimento();
		render(abastecimento, veiculos, condutores, fornecedores);
	}

	@AutorizacaoGIAntigo.RoleAdmin
	public static void editar(Long id) throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		Abastecimento abastecimento = Abastecimento.findById(id);
		render(abastecimento, veiculos, condutores, fornecedores);
	}


	@AutorizacaoGIAntigo.RoleAdmin
	public static void salvar(@Valid Abastecimento abastecimento) throws Exception{
		if(Validation.hasErrors()){
			List<Fornecedor> fornecedores = Fornecedor.listarTodos();
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			String template;
			template = abastecimento.getId() > 0 ? "Abastecimentos/editar.html" : "Abastecimentos/incluir.html";
			renderTemplate(template, abastecimento, fornecedores, veiculos);
		}
		else{
			abastecimento.save();
			listar();
		}	
	}		

	@AutorizacaoGIAntigo.RoleAdmin
	public static void excluir(Long id){
		Abastecimento abastecimento = Abastecimento.findById(id);
		abastecimento.delete();
		listar();		
	}

}
