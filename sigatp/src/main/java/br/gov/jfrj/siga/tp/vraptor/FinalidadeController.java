package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.data.validation.Validation;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

//@With(AutorizacaoGI.class)
@Resource
public class FinalidadeController extends TpController {
	
	public FinalidadeController(HttpServletRequest request, Result result, Localization localization, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, CpDao.getInstance(), localization, so, dadosAutorizacao, em);
	}

	private static final String ACTION = "action";
	private static final String ACTION_EDITAR = "Editar";
	private static final String ACTION_INCLUIR = "Incluir";

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
	
	public void listar() {
		listar(null);
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
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/salvar/{finalidade}")
	public void salvar(FinalidadeRequisicao finalidade) throws Exception {
		if(!finalidade.getId().equals(new Long(0))) {
			finalidade.checarProprietario(getTitular().getOrgaoUsuario());
		}
		
    	//validation.valid(finalidade);
    	
    	finalidade.setCpOrgaoOrigem(getTitular().getOrgaoUsuario());
		
    	if(Validation.hasErrors()) {
			result.include(ACTION, (finalidade.getId() == 0 ? ACTION_INCLUIR : ACTION_EDITAR));
			result.include("finalidade", finalidade);
			return;
		}

	 	finalidade.save();
		listar();
    }
}
