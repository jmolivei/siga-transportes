package br.gov.jfrj.siga.tp.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.common.base.Optional;

@RequestScoped
@Component
public class AutorizacaoGI {

	private Map<String, Boolean> statusPermissoes = new HashMap<String, Boolean>();
	private SigaObjects so;

	public AutorizacaoGI(SigaObjects so) {
		this.so = so;
		this.statusPermissoes = new HashMap<String, Boolean>();
	}

	/**
	 * Recupera na configuração do GI o complexo do perfil AdministradorPorComplexo para usuário logado verificando Órgao e Lotação e o tipo de configurção "Utilizar Complexo"
	 * 
	 * @return
	 * @throws Exception
	 */
	protected CpComplexo recuperarComplexoAdministrador() throws Exception {
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = CpRepository.find(CpServico.class, "siglaServico", SERVICO_COMPLEXO_ADMINISTRADOR).first();
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpRepository.findById(CpSituacaoConfiguracao.class, 1L);
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// and dtHistDtFim IS NOT NULL
		Object[] parametros = { so.getTitular().getIdPessoaIni(), cpSituacaoConfiguracaoPode, cpServico };
		configuracoes = CpRepository.find(CpConfiguracao.class, "(dpPessoa.idPessoaIni = ? and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )", parametros).fetch();

		if (configuracoes != null)
			cpComplexo = configuracoes.get(0).getComplexo();
		return cpComplexo;
	}

	public void incluir(String nomePermissao) {
	}

	public Boolean getStatusPermissao(String nomePermissao) {
		return Optional.fromNullable(statusPermissoes.get(nomePermissao)).or(Boolean.FALSE);
	}

	public boolean statusAutorizacao(String permissao) {
		try {
			so.assertAcesso("TP:Modulo de Transportes;" + permissao);
			return Boolean.TRUE;
		} catch (Exception e) {
		}
		return Boolean.FALSE;
	}

	public Boolean ehAdministrador() {
		return getStatusPermissao(Autorizacoes.ADM_ADMINISTRAR);
	}

	public Boolean ehAdministradorFrota() {
		return getStatusPermissao(Autorizacoes.ADMFROTA_ADMINISTRAR_FROTA);
	}

	public Boolean ehAdministradorMissao() {
		return getStatusPermissao(Autorizacoes.ADMMISSAO_ADMINISTRAR_MISSAO);
	}

	public Boolean ehAdministradorMissaoPorComplexo() {
		return getStatusPermissao(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO);
	}

	public Boolean ehAprovador() {
		return getStatusPermissao(Autorizacoes.APR_APROVADOR);
	}

	public Boolean ehGabinete() {
		return getStatusPermissao(Autorizacoes.GAB_GABINETE);
	}

	public Boolean ehAdminGabinete() {
		return getStatusPermissao(Autorizacoes.ADMGAB_ADMIN_GABINETE);
	}

	public Boolean ehAgente() {
		return getStatusPermissao(Autorizacoes.AGN_AGENTE);
	}

	public void incluirAdministrarMissaoComplexo() {
		try {
			this.statusPermissoes.put(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO, recuperarComplexoAdministrador() != null);
		} catch (Exception e) {
			this.statusPermissoes.put(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO, Boolean.FALSE);
		}
	}

	public Boolean deveExibirMenuAdmissaoComplexo() {
		return getStatusPermissao(Autorizacoes.EXIBIR_MENU_ADMMISSAO_ADMINISTRAR_MISSAO_COMPLEXO);
	}
}