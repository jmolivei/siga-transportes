package controllers;

import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import play.mvc.With;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Parametro;

@With(AutorizacaoGIAntigo.class)
public class Parametros extends Controller {
	@SuppressWarnings("unused")
	private static final String ACTION_LISTAR = "@listar";
	private static final String ACTION_EDITAR = "@editar";
	private static final String ACTION_INCLUIR = "@incluir";

	public static void listar() throws Exception {
   		List<Parametro> parametros = Parametro.findAll();
		render(parametros);
    }
	
	@RoleAdmin
	public static void editar(Long id) throws Exception {
		Parametro parametro = Parametro.findById(id);
		carregarDadosPerifericos();
    	render(parametro);
	}

	@RoleAdmin
	public static void excluir(Long id) throws Exception {
        Parametro parametro = Parametro.findById(id);	
        
		parametro.delete();
		listar();
		
	}
	
	@RoleAdmin
	public static void incluir() throws Exception {
		Parametro parametro = new Parametro();
		carregarDadosPerifericos();
     	render(parametro);
	}

	private static void carregarDadosPerifericos() {
		List<CpOrgaoUsuario> cpOrgaoUsuarios = TpDao.findAll(CpOrgaoUsuario.class);
		List<CpComplexo> cpComplexos = TpDao.findAll(CpComplexo.class);
		RenderArgs.current().put("cpOrgaoUsuarios", cpOrgaoUsuarios);
		RenderArgs.current().put("cpComplexos", cpComplexos);
	}

	@RoleAdmin
	public static void salvar(@Valid Parametro parametro) throws Exception {	
    		if(Validation.hasErrors()) {
        		carregarDadosPerifericos();
    			renderTemplate((parametro.id == 0? Parametros.ACTION_INCLUIR : Parametros.ACTION_EDITAR), parametro);
    			return;
    		}
	
	 	parametro.save();
   
		listar();
    }
	
}
