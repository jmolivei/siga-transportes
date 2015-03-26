package controllers;

import java.util.List;

import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminFrota;
import controllers.AutorizacaoGI.RoleAdminMissao;
import models.Fornecedor;
import models.Uf;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(AutorizacaoGI.class)
public class Fornecedores extends Controller {

	public static void listar() {
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		render(fornecedores);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao	
	@AutorizacaoGI.RoleAdminGabinete
	@AutorizacaoGI.RoleGabinete
	public static void incluir() {
		Fornecedor fornecedor = new Fornecedor();
		render(fornecedor);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@AutorizacaoGI.RoleAdminGabinete
	@AutorizacaoGI.RoleGabinete
	public static void editar(Long id) {
		Fornecedor fornecedor = Fornecedor.findById(id);
		render(fornecedor);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	@AutorizacaoGI.RoleAdminGabinete
	@AutorizacaoGI.RoleGabinete
	public static void salvar(@Valid Fornecedor fornecedor) {
		if (Validation.hasErrors()) {
			String template = fornecedor.id > 0 ? "Fornecedores/editar.html"
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
