package controllers;

import java.util.List;

import models.Abastecimento;
import models.Condutor;
import models.Fornecedor;
import models.ItemMenu;
import models.Veiculo;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import uteis.MenuMontador;

@With(AutorizacaoGI.class)
public class Gabinetes extends Controller {

	public static void listar() {
		List<Abastecimento> abastecimentos = Abastecimento.listarTodos();
		render(abastecimentos);
	}	

	//Verificar se o MenuMontador � realmente utilizado
	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<Abastecimento> abastecimentos = Abastecimento.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.DADOSCADASTRAIS);
		render(abastecimentos, veiculo);
	}

	@AutorizacaoGI.RoleAdmin
	public static void incluir() throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		Abastecimento abastecimento = new Abastecimento();
		render(abastecimento, veiculos, condutores, fornecedores);
	}

	@AutorizacaoGI.RoleAdmin
	public static void editar(Long id) throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		Abastecimento abastecimento = Abastecimento.findById(id);
		render(abastecimento, veiculos, condutores, fornecedores);
	}


	@AutorizacaoGI.RoleAdmin
	public static void salvar(@Valid Abastecimento abastecimento) throws Exception{
		if(Validation.hasErrors()){
			List<Fornecedor> fornecedores = Fornecedor.listarTodos();
			List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
			String template;
			template = abastecimento.id > 0 ? "Abastecimentos/editar.html" : "Abastecimentos/incluir.html";
			renderTemplate(template, abastecimento, fornecedores, veiculos);
		}
		else{
			abastecimento.save();
			listar();
		}	
	}		

	@AutorizacaoGI.RoleAdmin
	public static void excluir(Long id){
		Abastecimento abastecimento = Abastecimento.findById(id);
		abastecimento.delete();
		listar();		
	}

}
