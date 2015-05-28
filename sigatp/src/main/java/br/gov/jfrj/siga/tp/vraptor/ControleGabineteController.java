package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ControleGabinete;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/controleGabinete")
public class ControleGabineteController extends TpController {

	private AutorizacaoGI autorizacaoGI;
	
	public ControleGabineteController(HttpServletRequest request, Result result,
			CpDao dao, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI) {
		super(request, result, dao, validator, so, em);
		this.autorizacaoGI = autorizacaoGI;
	}
	
//	@RoleGabinete
//	@RoleAdminGabinete
	@Path("/listar")
	public void listar() {
		List<ControleGabinete> controlesGabinete;
		if (autorizacaoGI.ehAdminGabinete()) 
			controlesGabinete = ControleGabinete.listarTodos();
		else 
			controlesGabinete = ControleGabinete.listarPorCondutor(Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario()));

		result.include("controlesGabinete", controlesGabinete);
	}

	// Verificar se o MenuMontador é realmente utilizado
//	@RoleGabinete
//	@RoleAdminGabinete
	@Path("/listarPorVeiculo/{idVeiculo}")
	public void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<ControleGabinete> controlesGabinete = ControleGabinete.buscarTodosPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.DADOSCADASTRAIS);
		
		result.include("controlesGabinete", controlesGabinete);
		result.include("veiculo", veiculo);
	}

//	@RoleGabinete
//	@RoleAdminGabinete
	@Path("/incluir")
	public void incluir() throws Exception {
		List<Veiculo> veiculos = recuperarListaDeVeiculos();
		List<Condutor> condutores = recuperarListaDeCondutores();

		ControleGabinete controleGabinete = new ControleGabinete();
		result.include("controleGabinete", controleGabinete);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
	}

	private List<Veiculo> recuperarListaDeVeiculos() throws Exception {
		return Veiculo.listarFiltradoPor(getTitular().getOrgaoUsuario(), getTitular().getLotacao());
	}

	private List<Condutor> recuperarListaDeCondutores() throws Exception {
		List<Condutor> condutores;
		if (autorizacaoGI.ehAdminGabinete())
			condutores = Condutor.listarFiltradoPor(getTitular().getOrgaoUsuario(), getTitular().getLotacao());
		else {
			condutores = new ArrayList<Condutor>();
			condutores.add(Condutor.recuperarLogado(getTitular(), getTitular().getOrgaoUsuario()));
		}
		return condutores;
	}

//	@RoleGabinete
//	@RoleAdminGabinete
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		ControleGabinete controleGabinete = ControleGabinete.AR.findById(id);
		verificarAcesso(controleGabinete);
		List<Veiculo> veiculos = recuperarListaDeVeiculos();
		List<Condutor> condutores = recuperarListaDeCondutores();
		
		result.include("controleGabinete", controleGabinete);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
	}

	private void verificarAcesso(ControleGabinete controleGabinete) throws Exception {
		if (!(autorizacaoGI.ehAdminGabinete() && getTitular().getLotacao().equivale(controleGabinete.getTitular().getLotacao()))
				&& !controleGabinete.getTitular().equivale(getTitular()))
			throw new Exception(new I18nMessage("", "controlesGabinete.verificarAcesso.exception").getMessage());
	}

	private void verificarOdometrosSaidaRetorno(ControleGabinete controleGabinete) {
		if (controleGabinete.getOdometroEmKmSaida() > controleGabinete.getOdometroEmKmRetorno()) 
			validator.add(new I18nMessage("odometroEmKmRetorno", "controlesGabinete.odometroEmKmRetorno.validation"));
	}

	private void verificarDatasInicialFinal(ControleGabinete controleGabinete) throws Exception {
		if (controleGabinete.getDataHoraSaida() == null || controleGabinete.getDataHoraRetorno() == null)  {
			validator.add(new I18nMessage("dataHoraSaida", "controlesGabinete.dataHoraSaida.validation"));
			return;
		}
			
		Boolean dataSaidaAntesDeDataRetorno = controleGabinete.getDataHoraSaida().before(controleGabinete.getDataHoraRetorno());
		if (!dataSaidaAntesDeDataRetorno) 
			validator.add(new I18nMessage("dataHoraRetorno", "controlesGabinete.dataSaidaAntesDeDataRetorno.validation"));
	}

	private void verificarOdometroRetornoControleAnterior(ControleGabinete controleGabinete) {
		double ultimoOdometroDesteVeiculo = ControleGabinete.buscarUltimoOdometroPorVeiculo(controleGabinete.getVeiculo(), controleGabinete);
		if (controleGabinete.getOdometroEmKmSaida() < ultimoOdometroDesteVeiculo) 
			validator.add(new I18nMessage("odometroEmKmSaida", "controlesGabinete.odometroEmKmSaida.validation"));
	} 

//	@RoleGabinete
//	@RoleAdminGabinete
	public void salvar(@Valid ControleGabinete controleGabinete) throws Exception {
		if (!controleGabinete.getId().equals(new Long(0)))
			verificarAcesso(controleGabinete);
		
		verificarOdometroRetornoControleAnterior(controleGabinete);
		verificarOdometrosSaidaRetorno(controleGabinete);
		verificarDatasInicialFinal(controleGabinete);

		if (validator.hasErrors()) {
			List<Veiculo> veiculos = recuperarListaDeVeiculos();
			List<Condutor> condutores = recuperarListaDeCondutores();
			
			result.include("controleGabinete", controleGabinete);
			result.include("veiculos", veiculos);
			result.include("condutores", condutores);
			
			if(controleGabinete.getId() > 0)
				validator.onErrorUse(Results.page()).of(ControleGabineteController.class).editar(controleGabinete.getId());
			else
				validator.onErrorUse(Results.page()).of(ControleGabineteController.class).incluir();
				
		} else {
			if (controleGabinete.getId() == 0)
				controleGabinete.setDataHora(Calendar.getInstance());

			controleGabinete.setSolicitante(getCadastrante());
			controleGabinete.setTitular(getTitular());

			controleGabinete.save();
			result.redirectTo(ControleGabineteController.class).listar();
		}
	}

//	@RoleGabinete
//	@RoleAdminGabinete
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		ControleGabinete controleGabinete = ControleGabinete.AR.findById(id);
		verificarAcesso(controleGabinete);
		controleGabinete.delete();
		
		result.redirectTo(ControleGabineteController.class).listar();
	}
}
