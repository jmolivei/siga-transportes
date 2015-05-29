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
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.Parametro;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/parametro")
public class ParametroController extends TpController {

	public ParametroController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listar")
	public void listar() throws Exception {
		List<Parametro> parametros = Parametro.listarTodos();
		result.include("parametros", parametros);
	}

	// @RoleAdmin
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		Parametro parametro = Parametro.buscar(id);
		carregarDadosPerifericos();

		result.include("parametro", parametro);
	}

	// @RoleAdmin
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Parametro parametro = Parametro.buscar(id);

		parametro.delete();
		result.redirectTo(ParametroController.class).listar();
	}

	// @RoleAdmin
	public void incluir() throws Exception {
		Parametro parametro = new Parametro();
		carregarDadosPerifericos();

		result.include("parametro", parametro);
	}
	
	// @RoleAdmin
	public void salvar(@Valid Parametro parametro) throws Exception {
		if (validator.hasErrors()) {
			carregarDadosPerifericos();
			
			validator.onErrorUse(Results.page()).of(ParametroController.class).editar(parametro.getId());
			//TODO  HD mudar
//			renderTemplate((parametro.id == 0 ? Parametros.ACTION_INCLUIR
//					: Parametros.ACTION_EDITAR), parametro);
			return;
		}

		parametro.save();

		result.redirectTo(ParametroController.class).listar();
	}

	private void carregarDadosPerifericos() {
		List<CpOrgaoUsuario> cpOrgaoUsuarios = CpOrgaoUsuario.AR.findAll();
		List<CpComplexo> cpComplexos = CpComplexo.AR.findAll();

		result.include("cpOrgaoUsuarios", cpOrgaoUsuarios);
		result.include("cpComplexos", cpComplexos);
		// RenderArgs.current().put("cpOrgaoUsuarios", cpOrgaoUsuarios);
		// RenderArgs.current().put("cpComplexos", cpComplexos);
	}
	
	// TODO OSI22 - No merge das versoes houve problemas, procurar solucao.
	// public static Calendar formatarDataParametro(String stringCron) throws
	// ParseException {
	// String stringData = Parametro.buscarConfigSistemaEmVigor(stringCron);
	// String[] data = stringData.split("/");
	// Calendar cal = Calendar.getInstance();
	// cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(data[0]));
	// cal.set(Calendar.MONTH, Integer.parseInt(data[1]));
	// cal.set(Calendar.YEAR, Integer.parseInt(data[2]));
	// return cal;
	// }
}
