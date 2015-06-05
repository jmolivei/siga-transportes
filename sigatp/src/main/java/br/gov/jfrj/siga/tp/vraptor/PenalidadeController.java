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
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.Penalidade;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/penalidade/")
public class PenalidadeController extends TpController {
	public PenalidadeController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
		super(request, result, dao, validator, so, em);		
	}

	
	@Path("/listar")
	public void listar() throws Exception {
   		List<Penalidade> penalidades = Penalidade.AR.findAll();
   		result.include("penalidades", penalidades);
    }

	@Path("/editar/{id}")
	public  void editar(Long id) throws Exception {
		Penalidade penalidade = Penalidade.AR.findById(id);
		result.include("penalidade", penalidade);
	}

	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
        Penalidade penalidade = Penalidade.AR.findById(id);

		penalidade.delete();
		result.redirectTo(this).listar();

	}

	@Path("/incluir")
	public void incluir() throws Exception {
		Penalidade penalidade = new Penalidade();

     	result.include("penalidade",penalidade);
	}

	@RoleAdmin
	@RoleAdminGabinete
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAdminFrota
	@RoleGabinete
	@Path("/salvar")
	public void salvar(@Valid Penalidade penalidade) throws Exception {
    	if(validator.hasErrors())
		{
			result.include("penalidade",penalidade);
			validator.onErrorUse(Results.page()).of(PenalidadeController.class).editar(penalidade.getId());
		}

	 	penalidade.save();

	 	result.redirectTo(this).listar();
    }

}
