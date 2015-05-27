package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/gabinete")
public class GabineteController extends TpController {
	
	public GabineteController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, dao, validator, so, em);
	}

	@Path("/listar")
	public void listar() {
		List<Abastecimento> abastecimentos = Abastecimento.listarTodos();
		
		result.include("abastecimentos", abastecimentos);
	}

	//Verificar se o MenuMontador é realmente utilizado
	@Path("/listarPorVeiculo/{idVeiculo}")
	public void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<Abastecimento> abastecimentos = Abastecimento.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.DADOSCADASTRAIS);
		result.include("abastecimentos", abastecimentos);
		result.include("veiculo", veiculo);
	}

	@RoleAdmin
	@Path("/incluir")
	public void incluir() throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(getOrgaoUsuario());
		Abastecimento abastecimento = new Abastecimento();
		
		result.include("abastecimento", abastecimento);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
		result.include("fornecedores", fornecedores);
	}

	@RoleAdmin
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = Veiculo.listarTodos(getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(getOrgaoUsuario());
		Abastecimento abastecimento = Abastecimento.AR.findById(id);
		
		result.include("abastecimento", abastecimento);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
		result.include("fornecedores", fornecedores);
	}


	@RoleAdmin
	public void salvar(@Valid Abastecimento abastecimento) throws Exception{
		if(validator.hasErrors()){
			List<Fornecedor> fornecedores = Fornecedor.listarTodos();
			List<Veiculo> veiculos = Veiculo.listarTodos(getOrgaoUsuario());
			
			result.include("abastecimento", abastecimento);
			result.include("fornecedores", fornecedores);
			result.include("veiculos", veiculos);
			
			if(isEdicao(abastecimento))
				validator.onErrorUse(Results.page()).of(AbastecimentoController.class).editar(abastecimento.getId());
			else
				validator.onErrorUse(Results.page()).of(AbastecimentoController.class).incluir();
		}
		else
			abastecimento.save();
			listar();
	}

	@RoleAdmin
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception{
		Abastecimento abastecimento = Abastecimento.AR.findById(id);
		abastecimento.delete();
		listar();
	}
	
	private CpOrgaoUsuario getOrgaoUsuario() {
		return getTitular().getOrgaoUsuario();
	}


	private boolean isEdicao(Abastecimento abastecimento) {
		return abastecimento.getId() > 0;
	}
}
