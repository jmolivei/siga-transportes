package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import play.data.validation.Validation;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class FinalidadeController extends TpController {
	
	private static final String ACTION = "action";
	private static final String ACTION_EDITAR = "views.botoes.editar";
	private static final String ACTION_INCLUIR = "views.botoes.incluir";

	public FinalidadeController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/app/finalidade/listar")
	public void listar(String mensagem) {
    	MenuMontador.instance(result).recuperarMenuFinalidades(true);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos(getTitular().getOrgaoUsuario());
   		
    	if(null != mensagem){
    		Validation.addError("finalidade", mensagem);
    		result.include("erros", mensagem);
    	}
    	
    	result.include("finalidades", finalidades);
    }
	
	public void listar() {
		result.redirectTo(this).listar(null);
	}
	
	@Path("/app/finalidade/listarTodas")
	public void listarTodas() {
    	MenuMontador.instance(result).recuperarMenuFinalidades(false);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos();
   		
    	result.include("finalidades", finalidades);
    }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/editar/{id}")
	public void editar(final Long id) throws Exception {
    	FinalidadeRequisicao finalidade = buscar(id);
    	
    	if(isUpdate(finalidade)) {
    		finalidade.checarProprietario(getTitular().getOrgaoUsuario());
    		result.include(ACTION, ACTION_EDITAR);
    	} else
    		result.include(ACTION, ACTION_INCLUIR);
    	
    	result.include("finalidade", finalidade);
    }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/salvar/{finalidade}")
	public void salvar(final FinalidadeRequisicao finalidade) throws Exception {
		
		error(null == finalidade.getDescricao(), "finalidade", "views.erro.campoObrigatorio");
		FinalidadeRequisicao finalidadeBuscada = buscar(finalidade.getId());
		finalidadeBuscada.setDescricao(finalidade.getDescricao());
		
		if(isUpdate(finalidadeBuscada))
			finalidadeBuscada.checarProprietario(getTitular().getOrgaoUsuario());
    	
    	finalidadeBuscada.setCpOrgaoOrigem(getTitular().getOrgaoUsuario());
		
    	if(validator.hasErrors()) {
    		result.include("finalidade", finalidadeBuscada);
			validator.onErrorUse(Results.logic()).forwardTo(FinalidadeController.class).editar(finalidadeBuscada.getId());
		}

	 	finalidadeBuscada.save();
	 	listar();
    }

	private boolean isUpdate(FinalidadeRequisicao finalidade) {
		return null != finalidade.getId() && finalidade.getId() > 0;
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/excluir/{id}")
    public void excluir(final Long id) throws Exception  { 
        FinalidadeRequisicao finalidade = FinalidadeRequisicao.AR.findById(id);
        finalidade.checarProprietario(getTitular().getOrgaoUsuario());
		EntityTransaction tx = FinalidadeRequisicao.AR.em().getTransaction();  
		
		if (! tx.isActive())
			tx.begin();

		try {
		    finalidade.delete();    
			tx.commit();
			
		} catch(PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("o de integridade"))
				result.redirectTo(this).listar("finalidadeRequisicao.vinculada.requisicao");
			else
				result.redirectTo(this).listar(ex.getMessage());
			
		} catch(Exception ex) {
			tx.rollback();
			result.redirectTo(this).listar(ex.getMessage());
		}

		listar();
	}
	
	private FinalidadeRequisicao buscar(final Long id) throws Exception {
		if(null != id && id > 0)
			return FinalidadeRequisicao.AR.findById(id);
		
		return new FinalidadeRequisicao();
	}
}
