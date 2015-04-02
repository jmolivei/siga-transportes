package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import controllers.AutorizacaoGI;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;
import play.data.validation.Validation;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

//@With(AutorizacaoGI.class)
@Resource
public class FinalidadeController extends TpController {
	
	private static final String ACTION = "action";
	private static final String ACTION_EDITAR = "Editar";
	private static final String ACTION_INCLUIR = "Incluir";

	public FinalidadeController(HttpServletRequest request, Result result, SigaObjects so, EntityManager em) {
		super(request, result, so, em);
	}

	@Path("/app/finalidade/listar")
	public void listar(String mensagem) {
    	MenuMontador.instance(result).recuperarMenuFinalidades(true);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos(getTitular().getOrgaoUsuario());
   		
    	if(mensagem != null){
    		Validation.addError("finalidade", mensagem);
    		result.include("erros", mensagem);
    	}
    	
    	result.include("finalidades", finalidades);
    }
	
	@Path("/app/finalidade/listarTodas")
	public void listarTodas() {
    	MenuMontador.instance(result).recuperarMenuFinalidades(false);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos();
   		
    	result.include("finalidades", finalidades);
    }
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/incluir")
	public FinalidadeRequisicao incluir() {
    	FinalidadeRequisicao finalidade = new FinalidadeRequisicao();
    	result.include(ACTION, ACTION_INCLUIR);

     	return finalidade;
    }
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/editar/{id}")
	public FinalidadeRequisicao editar(Long id) throws Exception {
    	FinalidadeRequisicao finalidade = FinalidadeRequisicao.AR.findById(id);
    	finalidade.checarProprietario(getTitular().getOrgaoUsuario());
    	result.include(ACTION, ACTION_EDITAR);
    	
    	return finalidade;
    }
}
