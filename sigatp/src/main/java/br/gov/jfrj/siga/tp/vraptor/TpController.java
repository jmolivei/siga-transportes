package br.gov.jfrj.siga.tp.vraptor;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.Autorizacoes;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class TpController extends SigaController {

	protected Validator validator;
	protected Localization localization;
	private AutorizacaoGI dadosAutorizacao;

	public TpController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, dao, so, em);
		this.validator = validator;
		this.dadosAutorizacao = dadosAutorizacao;
		this.localization = localization;
		this.preencherDadosPadrao();
	}

	private void preencherDadosPadrao() throws Exception {
		this.preencherDadosAutorizacoes();
		this.result.include("currentTimeMillis", new Date().getTime());
	}

	private void preencherDadosAutorizacoes() {
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR, dadosAutorizacao.ehAdministrador());
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR_FROTA, dadosAutorizacao.ehAdministradorFrota());
		result.include(Autorizacoes.EXIBIR_MENU_ADMINISTRAR_MISSAO, dadosAutorizacao.ehAdministradorMissao());
		result.include(Autorizacoes.EXIBIR_MENU_APROVADOR, dadosAutorizacao.ehAprovador());
		result.include(Autorizacoes.EXIBIR_MENU_GABINETE, dadosAutorizacao.ehGabinete());
		result.include(Autorizacoes.EXIBIR_MENU_ADMIN_GABINETE, dadosAutorizacao.ehAdminGabinete());
		result.include(Autorizacoes.EXIBIR_MENU_AGENTE, dadosAutorizacao.ehAgente());
		result.include(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO, dadosAutorizacao.deveExibirMenuAdmissaoComplexo());
	}

	protected void error(boolean condition, String category, String message) {
		if (condition) {
			validator.add(new I18nMessage(category, message));
		}
	}
}