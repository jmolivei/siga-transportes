package controllers;

import java.util.List;

import javax.persistence.EntityTransaction;

import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.Penalidade;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminFrota;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(AutorizacaoGI.class)
public class Penalidades extends Controller {
	private static final String _ACTION_LISTAR = "@listar";
	private static final String _ACTION_EDITAR = "@editar";
	private static final String _ACTION_INCLUIR = "@incluir";
	
	public static void listar() throws Exception {
   		List<Penalidade> penalidades = Penalidade.findAll();
		render(penalidades);
    }
	
	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id) throws Exception {
		Penalidade penalidade = Penalidade.findById(id);
    	
    	render(penalidade);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id) throws Exception {
        Penalidade penalidade = Penalidade.findById(id);	
        
		penalidade.delete();
		listar();
		
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void incluir() throws Exception {
		Penalidade penalidade = new Penalidade();
    	
     	render(penalidade);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(@Valid Penalidade penalidade) throws Exception {	
    	if(Validation.hasErrors()) 
		{
    		if(Validation.hasError("codigoInfracao")){
    			Validation.addError("codigoInfracao","");
    		}
			renderTemplate((penalidade.id == 0? Penalidades._ACTION_INCLUIR : Penalidades._ACTION_EDITAR), penalidade);
			return;
		}

	 	penalidade.save();
   
		listar();
    }

}
