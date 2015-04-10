package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.AutoDeInfracao;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.TipoDeNotificacao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class AutoDeInfracaoController extends TpController{

	public AutoDeInfracaoController(HttpServletRequest request, Result result,
			CpDao dao, Localization localization, Validator validator,
			SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, validator, so, em);
	}

	@Path("/app/autoDeInfracao/listarPorVeiculo/{idVeiculo}")
	public void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao
				.buscarAutosDeInfracaoPorVeiculo(veiculo);
		MenuMontador.instance(result).recuperarMenuVeiculos(idVeiculo,
				ItemMenu.INFRACOES);

		result.include("autosDeInfracao", autosDeInfracao);
		result.include("veiculo", veiculo);
	}

	@Path("/app/autoDeInfracao/listarPorCondutor/{idCondutor}")
	public void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao
				.buscarAutosDeInfracaoPorCondutor(condutor);
		MenuMontador.instance(result).recuperarMenuCondutores(idCondutor,
				ItemMenu.INFRACOES);
		result.include("autosDeInfracao", autosDeInfracao);
		result.include("condutor", condutor);
	}

	@Path("/app/autoDeInfracao/listar")
	public void listar() {
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.listarOrdenado();
		result.include("autosDeInfracao", autosDeInfracao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/autoDeInfracao/incluir/{notificacao}")
	public void incluir(String notificacao) throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(getTitular().getOrgaoUsuario());
		AutoDeInfracao autoDeInfracao = new AutoDeInfracao();
		TipoDeNotificacao tipoNotificacao = TipoDeNotificacao.valueOf(notificacao);
		
		result.include("autoDeInfracao", autoDeInfracao);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
		result.include("tipoNotificacao", tipoNotificacao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/autoDeInfracao/editar/{id}")
	public void editar(Long id) throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		List<Condutor> condutores = Condutor.listarTodos(getTitular().getOrgaoUsuario());
		AutoDeInfracao autoDeInfracao = AutoDeInfracao.AR.findById(id);
		TipoDeNotificacao tipoNotificacao = autoDeInfracao.codigoDaAutuacao != null ? TipoDeNotificacao.AUTUACAO
				: TipoDeNotificacao.PENALIDADE;
		
		result.include("autoDeInfracao", autoDeInfracao);
		result.include("veiculos", veiculos);
		result.include("condutores", condutores);
		result.include("tipoNotificacao", tipoNotificacao);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public void salvar(@Valid AutoDeInfracao autoDeInfracao) throws Exception {
		autoDeInfracao.setCondutor(Condutor.AR.findById(autoDeInfracao.getCondutor().getId()));
		autoDeInfracao.setVeiculo(Veiculo.AR.findById(autoDeInfracao.getVeiculo().getId()));
		
		TipoDeNotificacao tipoNotificacao = autoDeInfracao.codigoDaAutuacao != null ? 
				TipoDeNotificacao.AUTUACAO : TipoDeNotificacao.PENALIDADE;

 		error(autoDeInfracao.dataDePagamento != null && autoDeInfracao.dataPosteriorDataCorrente(autoDeInfracao.dataDePagamento)
 				, "dataPagamento", "veiculo.autosDeInfracao.dataDePagamento.validation");

		if (validator.hasErrors()) {
			List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
			List<Condutor> condutores = Condutor.listarTodos(getTitular().getOrgaoUsuario());
			
			result.include("autoDeInfracao", autoDeInfracao);
			result.include("veiculos", veiculos);
			result.include("condutores", condutores);
			result.include("tipoNotificacao", tipoNotificacao);
				
			if(autoDeInfracao.id  > 0)
				validator.onErrorUse(Results.logic()).forwardTo(AutoDeInfracaoController.class).editar(autoDeInfracao.id);
			else
				validator.onErrorUse(Results.logic()).forwardTo(AutoDeInfracaoController.class).incluir(tipoNotificacao.getDescricao());
			
		} else {
			autoDeInfracao.save();
			result.forwardTo(this).listar();
		}
	}
	
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/autoDeInfracao/excluir/{id}")
	public void excluir(Long id) throws Exception {
		AutoDeInfracao autoDeInfracao = AutoDeInfracao.AR.findById(id);
		autoDeInfracao.delete();
		
		result.forwardTo(this).listar();
	}
}
