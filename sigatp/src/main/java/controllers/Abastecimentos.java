package controllers;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.LogMotivo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class Abastecimentos extends Controller {

	public static void listar() {
		List<Abastecimento> abastecimentos = null;
		if (AutorizacaoGIAntigo.ehGabinete()) {
			Condutor condutor = Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			abastecimentos = Abastecimento.listarAbastecimentosDoCondutor(condutor);
		} else if (AutorizacaoGIAntigo.ehAdminGabinete()) {
			abastecimentos = Abastecimento.listarParaAdminGabinete(AutorizacaoGIAntigo.titular());
		} else  if (AutorizacaoGIAntigo.ehAgente()) { 
			abastecimentos = Abastecimento.listarParaAgente(AutorizacaoGIAntigo.titular());
		} else { //eh admin
			abastecimentos = Abastecimento.listarTodos(AutorizacaoGIAntigo.titular());
		}
		render(abastecimentos);
	}	

	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<Abastecimento> abastecimentos = Abastecimento.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.ABASTECIMENTOS);
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
		if (! AutorizacaoGIAntigo.ehAdministrador()) {
			return Veiculo.listarFiltradoPor(AutorizacaoGIAntigo.titular().getOrgaoUsuario(),AutorizacaoGIAntigo.titular().getLotacao());
		} else {
			return Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		}
	}
	
	private static List<Condutor> listarCondutores() throws Exception {
		if(AutorizacaoGIAntigo.ehGabinete()) {
			List<Condutor> retorno = new ArrayList<Condutor>();
			retorno.add(Condutor.recuperarLogado(AutorizacaoGIAntigo.titular(), AutorizacaoGIAntigo.titular().getOrgaoUsuario()));
			return retorno;
		}
		
		if (! AutorizacaoGIAntigo.ehAdministrador()) {
			return Condutor.listarFiltradoPor(AutorizacaoGIAntigo.titular().getOrgaoUsuario(),AutorizacaoGIAntigo.titular().getLotacao());
		} else {
			return Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		}
	}

	@RoleAdmin
	@RoleAdminGabinete
	@RoleAdminMissao	
	@RoleAdminFrota
	@RoleAdminMissaoComplexo
	@RoleGabinete
	public static void editar(Long id) throws Exception{
		Abastecimento abastecimento = Abastecimento.AR.findById(id);
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
			
			abastecimento.setTitular(AutorizacaoGIAntigo.titular());
			abastecimento.setSolicitante(AutorizacaoGIAntigo.cadastrante());
			if(abastecimento.getId().equals(new Long(0))) { // somente na inclusao
				abastecimento.setOrgao(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
			}
			
			abastecimento.save();
			listar();
		}	
	}
	
	private static void verificarAcesso(Abastecimento abastecimento) throws Exception {
		if(AutorizacaoGIAntigo.ehAdminGabinete() || AutorizacaoGIAntigo.ehGabinete()) {
			if(!(AutorizacaoGIAntigo.ehAdminGabinete() && AutorizacaoGIAntigo.titular().getLotacao().equivale(abastecimento.getTitular().getLotacao())) && !abastecimento.getTitular().equivale(AutorizacaoGIAntigo.titular())) {
				throw new Exception(Messages.get("abastecimentos.verificarAcesso.exception"));
			}
		} else if(!((AutorizacaoGIAntigo.ehAdministrador() || AutorizacaoGIAntigo.ehAdministradorFrota() ||  AutorizacaoGIAntigo.ehAdministradorMissao() || AutorizacaoGIAntigo.ehAdministradorMissaoPorComplexo())  && AutorizacaoGIAntigo.titular().getLotacao().equivale(abastecimento.getTitular().getLotacao())) && !abastecimento.getTitular().equivale(AutorizacaoGIAntigo.titular())) {

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
		Abastecimento abastecimento = Abastecimento.AR.findById(id);
		verificarAcesso(abastecimento);
		abastecimento.delete();
		listar();		
	}
}