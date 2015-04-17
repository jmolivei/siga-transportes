package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
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
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Uf;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/fornecedor")
public class FornecedorController extends TpController {

	public FornecedorController(HttpServletRequest request, Result result, CpDao dao, Validator validator, SigaObjects so, /*AutorizacaoGIAntigo dadosAutorizacao,*/ EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listar")
	public void listar() {
		result.include("fornecedores", getFornecedores());
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao	
	@RoleAdminGabinete
	@RoleGabinete
	@Path("/incluir")
	public void incluir() {
		Fornecedor fornecedor = new Fornecedor();
		result.include("fornecedor", fornecedor);
		result.include("listaUF", Uf.listarTodos());
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@RoleAdminGabinete
	@RoleGabinete
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		result.include("fornecedor", Fornecedor.AR.findById(id));
		result.include("listaUF", Uf.listarTodos());
	}
	
	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@RoleAdminGabinete
	@RoleGabinete
	@Path("/salvar")
	public void salvar(@Valid Fornecedor fornecedor) {
		if (validator.hasErrors()) {
			String template = fornecedor.getId() > 0 ? "/app/fornecedor/editar" : "/app/fornecedor/incluir";
			
			result.include("listaUF", Uf.listarTodos());
			result.include("template", template);
			result.include("fornecedor", fornecedor);
			
			validator.onErrorUse(Results.page()).of(FornecedorController.class).incluir();
		} else {
			fornecedor.save();
			result.redirectTo(this).listar();
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		Fornecedor fornecedor = Fornecedor.AR.findById(id);
		fornecedor.delete();
		result.redirectTo(this).listar();
	}

	private List<Fornecedor> getFornecedores() {
		try {
			return Fornecedor.listarTodos();
		} catch (Exception ignore) {
			return new ArrayList<Fornecedor>();
		}
	}
}
