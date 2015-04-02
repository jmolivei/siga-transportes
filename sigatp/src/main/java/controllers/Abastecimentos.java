package controllers;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGI.LogMotivo;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminFrota;
import controllers.AutorizacaoGI.RoleAdminGabinete;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;
import controllers.AutorizacaoGI.RoleGabinete;

@With(AutorizacaoGI.class)
public class Abastecimentos extends Controller {

	public static void listar() {
		List<Abastecimento> abastecimentos = null;
		if (AutorizacaoGI.ehGabinete()) {
			Condutor condutor = Condutor.recuperarLogado(AutorizacaoGI.titular(), AutorizacaoGI.titular().getOrgaoUsuario());
			abastecimentos = Abastecimento.listarAbastecimentosDoCondutor(condutor);
		} else if (AutorizacaoGI.ehAdminGabinete()) {
			abastecimentos = Abastecimento.listarParaAdminGabinete(AutorizacaoGI.titular());
		} else  if (AutorizacaoGI.ehAgente()) { 
			abastecimentos = Abastecimento.listarParaAgente(AutorizacaoGI.titular());
		} else { //eh admin
			abastecimentos = Abastecimento.listarTodos(AutorizacaoGI.titular());
		}
		render(abastecimentos);
	}	

	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<Abastecimento> abastecimentos = Abastecimento.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.ABASTECIMENTOS);
		render(abastecimentos, veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao	
	@RoleAdminMissaoComplexo
	@RoleAdminGabinete
	@RoleGabinete
	public static void incluir() throws Exception{
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = listarVeiculos();
		List<Condutor> condutores = listarCondutores();
		Abastecimento abastecimento = new Abastecimento();
		render(abastecimento, veiculos, condutores, fornecedores);
	}

	private static List<Veiculo> listarVeiculos() throws Exception {
		if (! AutorizacaoGI.ehAdministrador()) {
			return Veiculo.listarFiltradoPor(AutorizacaoGI.titular().getOrgaoUsuario(),AutorizacaoGI.titular().getLotacao());
		} else {
			return Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		}
	}
	
	private static List<Condutor> listarCondutores() throws Exception {
		if(AutorizacaoGI.ehGabinete()) {
			List<Condutor> retorno = new ArrayList<Condutor>();
			retorno.add(Condutor.recuperarLogado(AutorizacaoGI.titular(), AutorizacaoGI.titular().getOrgaoUsuario()));
			return retorno;
		}
		
		if (! AutorizacaoGI.ehAdministrador()) {
			return Condutor.listarFiltradoPor(AutorizacaoGI.titular().getOrgaoUsuario(),AutorizacaoGI.titular().getLotacao());
		} else {
			return Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		}
	}

	@RoleAdmin
	@RoleAdminGabinete
	@RoleAdminMissao	
	@RoleAdminFrota
	@RoleAdminMissaoComplexo
	@RoleGabinete
	public static void editar(Long id) throws Exception{
		Abastecimento abastecimento = Abastecimento.findById(id);
		verificarAcesso(abastecimento);
		
		List<Fornecedor> fornecedores = Fornecedor.listarTodos();
		List<Veiculo> veiculos = listarVeiculos();
		List<Condutor> condutores = listarCondutores();
		render(abastecimento, veiculos, condutores, fornecedores);
	}


	@RoleAdmin
	@RoleAdminGabinete
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAdminFrota
	@RoleGabinete
	public static void salvar(@Valid Abastecimento abastecimento) throws Exception{
		if(!abastecimento.getId().equals(new Long(0))) { // somente na alteracao
			verificarAcesso(abastecimento);
		}
		
		if (abastecimento.getOdometroEmKm() == 0) {
			Validation.addError("odometroEmKm", "abastecimento.odometroEmKm.validation");
		}
		
		if(Validation.hasErrors()){
			List<Fornecedor> fornecedores = Fornecedor.listarTodos();
			List<Veiculo> veiculos = listarVeiculos();
			List<Condutor> condutores = listarCondutores();
			String template;
			template = abastecimento.getId() > 0 ? "Abastecimentos/editar.html" : "Abastecimentos/incluir.html";
			renderTemplate(template, abastecimento, fornecedores, veiculos, condutores);
		}
		else {
			
			abastecimento.setTitular(AutorizacaoGI.titular());
			abastecimento.setSolicitante(AutorizacaoGI.cadastrante());
			if(abastecimento.getId().equals(new Long(0))) { // somente na inclusao
				abastecimento.setOrgao(AutorizacaoGI.titular().getOrgaoUsuario());
			}
			
			abastecimento.save();
			listar();
		}	
	}
	
	private static void verificarAcesso(Abastecimento abastecimento) throws Exception {
		if(AutorizacaoGI.ehAdminGabinete() || AutorizacaoGI.ehGabinete()) {
			if(!(AutorizacaoGI.ehAdminGabinete() && AutorizacaoGI.titular().getLotacao().equivale(abastecimento.getTitular().getLotacao())) && !abastecimento.getTitular().equivale(AutorizacaoGI.titular())) {
				throw new Exception(Messages.get("abastecimentos.verificarAcesso.exception"));
			}
		} else if(!((AutorizacaoGI.ehAdministrador() || AutorizacaoGI.ehAdministradorFrota() ||  AutorizacaoGI.ehAdministradorMissao() || AutorizacaoGI.ehAdministradorMissaoPorComplexo())  && AutorizacaoGI.titular().getLotacao().equivale(abastecimento.getTitular().getLotacao())) && !abastecimento.getTitular().equivale(AutorizacaoGI.titular())) {
			throw new Exception(Messages.get("abastecimentos.verificarAcesso.exception"));
		}

	}

	@LogMotivo
	@RoleAdmin 
	@RoleAdminGabinete
	@RoleAdminMissaoComplexo
	@RoleAdminFrota
	@RoleGabinete
	public static void excluir(Long id) throws Exception{
		Abastecimento abastecimento = Abastecimento.findById(id);
		verificarAcesso(abastecimento);
		abastecimento.delete();
		listar();		
	}
}