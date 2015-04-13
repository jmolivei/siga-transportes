package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.mvc.With;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.CpUF;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Uf;
import br.gov.jfrj.siga.vraptor.SigaObjects;
import controllers.AutorizacaoGIAntigo;
//import controllers.AutorizacaoGIAntigo.RoleAdmin;
//import controllers.AutorizacaoGIAntigo.RoleAdminFrota;
//import controllers.AutorizacaoGIAntigo.RoleAdminMissao;

@With(AutorizacaoGIAntigo.class)
@Resource
public class FornecedorController extends TpController {

	public FornecedorController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, /*AutorizacaoGIAntigo dadosAutorizacao,*/ EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, /*dadosAutorizacao,*/ em);
	}

	@Path("/app/fornecedor/listar")
	public void lista() {
		result.include("fornecedores", getFornecedores());
	}

//	@RoleAdmin
//	@RoleAdminFrota
//	@RoleAdminMissao	
//	@AutorizacaoGIAntigo.RoleAdminGabinete
//	@AutorizacaoGIAntigo.RoleGabinete
	@Path("/app/fornecedor/incluir")
	public void inclui() {
		Fornecedor fornecedor = new Fornecedor();
		List<CpUF> listaUf = Uf.listarTodos();
		result.include("fornecedor", fornecedor);
		result.include("listaUF", listaUf);
	}

//	@RoleAdmin
//	@RoleAdminFrota
//	@RoleAdminMissao
//	@AutorizacaoGIAntigo.RoleAdminGabinete
//	@AutorizacaoGIAntigo.RoleGabinete
	@Path("/app/fornecedor/editar/{id}")
	public void edita(Long id) throws Exception {
		Fornecedor fornecedor = Fornecedor.AR.findById(id);
		List<CpUF> listaUf = Uf.listarTodos();
		result.include("fornecedor", fornecedor);
		result.include("listaUF", listaUf);
	}
	
//	@RoleAdmin
//	@RoleAdminFrota
//	@RoleAdminMissao
////	@AutorizacaoGI.RoleAdminGabinete
////	@AutorizacaoGI.RoleGabinete
	public void salvar(Fornecedor fornecedor) {
		if (validator.hasErrors()) {
			String template = fornecedor.getId() > 0 ? "/app/fornecedor/editar"
					: "/app/fornecedor/incluir";
			result.include("template", template);
			result.include("fornecedor", fornecedor);
		} else {
			fornecedor.save();
			result.redirectTo(this).lista();
		}
	}

//	@RoleAdmin
//	@RoleAdminFrota
//	@RoleAdminMissao
	@Path("/app/fornecedor/excluir/{id}")
	public void exclui(Long id) throws Exception {
		Fornecedor fornecedor = Fornecedor.AR.findById(id);
		fornecedor.delete();
		result.redirectTo(this).lista();
	}

	private List<Fornecedor> getFornecedores() {
		try {
			List<Fornecedor> fornecedores = Fornecedor.listarTodos(); 
			return fornecedores;

		} catch (Exception ignore) {
			return null;
		}
	}
}
