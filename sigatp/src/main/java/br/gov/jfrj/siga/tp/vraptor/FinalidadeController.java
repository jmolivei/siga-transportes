package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import controllers.AutorizacaoGIAntigo;
import controllers.AutorizacaoGIAntigo.RoleAdmin;
import controllers.AutorizacaoGIAntigo.RoleAdminMissao;
import controllers.AutorizacaoGIAntigo.RoleAdminMissaoComplexo;
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
   		
    	if(mensagem != null){
    		Validation.addError("finalidade", mensagem);
    		result.include("erros", mensagem);
    	}
    	
    	result.include("finalidades", finalidades);
    }
	
	private void listar() {
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
    	FinalidadeRequisicao finalidade = new FinalidadeRequisicao();
    	
    	if(id > 0) {
    		finalidade = FinalidadeRequisicao.AR.findById(id);
    		finalidade.checarProprietario(getTitular().getOrgaoUsuario());
    		result.include(ACTION, ACTION_EDITAR);
    	} else {
    		result.include(ACTION, ACTION_INCLUIR);
    	}
    	
    	result.include("finalidade", finalidade);
    }

	@Path("/app/finalidade/incluir")
	public void incluir() throws Exception {
		result.redirectTo(this).editar(new Long(0));
	}
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/app/finalidade/salvar")
	public void salvar(FinalidadeRequisicao finalidade) throws Exception {
		
		if(null != finalidade.getId() && !finalidade.getId().equals(new Long(0))) {
			finalidade.checarProprietario(getTitular().getOrgaoUsuario());
		}
		
    	//validation.valid(finalidade);
    	
    	finalidade.setCpOrgaoOrigem(getTitular().getOrgaoUsuario());
		
    	if(Validation.hasErrors()) {
    		result.include("finalidade", finalidade);
			result.include(ACTION, (finalidade.getId() == 0 ? ACTION_INCLUIR : ACTION_EDITAR));
			return;
		}

	 	finalidade.save();
	 	listar();
    }
	
	//	@RoleAdmin
	//@RoleAdminMissao
	//@RoleAdminMissaoComplexo
	@Path("/app/finalidade/excluir/{id}")
    public void excluir(Long id) throws Exception  { 
        FinalidadeRequisicao finalidade = FinalidadeRequisicao.AR.findById(id);
        finalidade.checarProprietario(getTitular().getOrgaoUsuario());
		EntityTransaction tx = FinalidadeRequisicao.AR.em().getTransaction();  
		
		if (! tx.isActive()) {
			tx.begin();
		}

		try {
		    finalidade.delete();    
			tx.commit();
			
		} catch(PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("o de integridade")) {
				result.redirectTo(this).listar("finalidadeRequisicao.vinculada.requisicao");
			} else {
				result.redirectTo(this).listar(ex.getMessage());
			}
		} catch(Exception ex) {
			tx.rollback();
			result.redirectTo(this).listar(ex.getMessage());
		}

		listar();
	}
}
