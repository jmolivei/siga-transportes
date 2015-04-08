package controllers;

import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.Uf;

@With(AutorizacaoGIAntigo.class)
public class Fornecedores extends Controller {

	public static void listar() {
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		render(fornecedores);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao	
	@RoleAdminGabinete
	@RoleGabinete
	public static void incluir() {
		Fornecedor fornecedor = new Fornecedor();
		render(fornecedor);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@RoleAdminGabinete
	@RoleGabinete
	public static void editar(Long id) {
		Fornecedor fornecedor = Fornecedor.findById(id);
		render(fornecedor);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@RoleAdminGabinete
	@RoleGabinete
	public static void salvar(@Valid Fornecedor fornecedor) {
		if (Validation.hasErrors()) {
			String template = fornecedor.getId() > 0 ? "Fornecedores/editar.html"
					: "Fornecedores/incluir.html";
			renderTemplate(template, fornecedor);
		} else {
			fornecedor.save();
			listar();
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	public static void excluir(Long id) {
		Fornecedor fornecedor = Fornecedor.findById(id);
		fornecedor.delete();
		listar();
	}

	@Before(priority = 200, only = { "incluir", "editar", "salvar" })
	private static void listarUf() {
		List<Uf> listaUF = Uf.listarTodos();
		renderArgs.put("listaUF", listaUF);
	}
}
