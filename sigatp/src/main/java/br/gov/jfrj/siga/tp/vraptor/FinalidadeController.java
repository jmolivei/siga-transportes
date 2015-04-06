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
	private static final String ACTION_EDITAR = "views.botoes.editar";
	private static final String ACTION_INCLUIR = "views.botoes.incluir";

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
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/editar/{id}")
	public void editar(Long id) throws Exception {
    	FinalidadeRequisicao finalidade = buscar(id);
    	
    	if(null != finalidade.getId() && finalidade.getId() > 0) {
    		finalidade.checarProprietario(getTitular().getOrgaoUsuario());
    		result.include(ACTION, ACTION_EDITAR);
    	} else
    		result.include(ACTION, ACTION_INCLUIR);
    	
    	result.include("finalidade", finalidade);
    }
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/salvar/{finalidade}")
	public void salvar(FinalidadeRequisicao finalidade) throws Exception {
		FinalidadeRequisicao finalidadeEncontrada = buscar(finalidade.getId());
		finalidadeEncontrada.setDescricao(finalidade.getDescricao());
		
		if(null != finalidadeEncontrada.getId() && finalidadeEncontrada.getId() > 0)
			finalidadeEncontrada.checarProprietario(getTitular().getOrgaoUsuario());
		
    	//validation.valid(finalidade);
    	
    	finalidadeEncontrada.setCpOrgaoOrigem(getTitular().getOrgaoUsuario());
		
    	if(Validation.hasErrors()) {
    		result.include("finalidade", finalidadeEncontrada);
			result.include(ACTION, (finalidadeEncontrada.getId() == 0 ? ACTION_INCLUIR : ACTION_EDITAR));
			return;
		}

	 	finalidadeEncontrada.save();
	 	listar();
    }
	
	//@RoleAdmin
	//@RoleAdminMissao
	//@RoleAdminMissaoComplexo
	@Path("/app/finalidade/excluir/{id}")
    public void excluir(Long id) throws Exception  { 
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
