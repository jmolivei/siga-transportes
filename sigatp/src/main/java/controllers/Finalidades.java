package controllers;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;



@With(AutorizacaoGI.class)
public class Finalidades extends Controller {
	private static final String _ACTION_LISTAR = "@listar";
	private static final String _ACTION_EDITAR = "@editar";
	private static final String _ACTION_INCLUIR = "@incluir";
	
    public static void listar() throws Exception {
    	MenuMontador.instance().RecuperarMenuFinalidades(true);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
   		render(finalidades);
    }

    public static void listarTodas() throws Exception {
    	MenuMontador.instance().RecuperarMenuFinalidades(false);
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos();
   		render(finalidades);
    }

    public static void listarComMensagem(String mensagem) throws Exception {
    	List<FinalidadeRequisicao> finalidades = FinalidadeRequisicao.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	Validation.addError("finalidade", mensagem);
		renderTemplate(_ACTION_LISTAR, finalidades);
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
    public static void excluir(Long id) throws Exception  { 
        FinalidadeRequisicao finalidade = FinalidadeRequisicao.findById(id);
		
        finalidade.checarProprietario(AutorizacaoGI.titular().getOrgaoUsuario());
		
		EntityTransaction tx = FinalidadeRequisicao.em().getTransaction();  
		if (! tx.isActive()) {
			tx.begin();
		}

		try {
		    finalidade.delete();    
			tx.commit();
			listar();
			
		} catch(PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("o de integridade")) {
				listarComMensagem("Esta finalidade possui requisi&ccedil;&otilde;es vinculadas e n&atilde;o pode ser exclu&iacute;da.");
			}
			else {
				listarComMensagem(ex.getMessage());
			}
		}
		catch(Exception ex) {
			tx.rollback();
			listarComMensagem(ex.getMessage());
		}

		listar();
	}
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) throws Exception {
    	FinalidadeRequisicao finalidade = FinalidadeRequisicao.findById(id);
    	
    	finalidade.checarProprietario(AutorizacaoGI.titular().getOrgaoUsuario());
    	
    	render(finalidade);
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir() {
    	FinalidadeRequisicao finalidade = new FinalidadeRequisicao();
    	
     	render(finalidade);
    }
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(FinalidadeRequisicao finalidade) throws Exception {
		if(!finalidade.id.equals(new Long(0))) {
			finalidade.checarProprietario(AutorizacaoGI.titular().getOrgaoUsuario());
		}
		
    	validation.valid(finalidade);
    	
    	finalidade.cpOrgaoOrigem = AutorizacaoGI.titular().getOrgaoUsuario();
		
    	if(Validation.hasErrors()) 
		{
			renderTemplate((finalidade.id == 0? Finalidades._ACTION_INCLUIR : Finalidades._ACTION_EDITAR), finalidade);
			return;
		}

	 	finalidade.save();
   
		listar();
    }
}