package br.gov.jfrj.siga.tp.vraptor;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.security.SecurityContextAssociation;

import play.i18n.Messages;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.Usuario;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.Autorizacoes;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class TpController extends SigaController {

	private static final Logger LOGGER = Logger.getLogger(TpController.class);
	private AutorizacaoGI dadosAutorizacao;

	public TpController(HttpServletRequest request, Result result, CpDao dao, SigaObjects so, AutorizacaoGI dadosAutorizacao, EntityManager em) throws Exception {
		super(request, result, dao, so, em);
		this.dadosAutorizacao = dadosAutorizacao;
		this.preencherDadosPadrao();
	}

	private void preencherDadosPadrao() throws Exception {
		// this.preencherDadosCabecalho();
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

	private void preencherDadosCabecalho() {
		try {
			obterCabecalhoEUsuario("rgb(235, 235, 232)");
			// TODO Heidi renderArgs
			// Incluindo Cadastrante e titulart novamente no cabecalho
			// String cabecalhoPreMenu = (String) RenderArgs.current().get(
			// "_cabecalho_pre_menu");
			// TODO Heidi Mudarr!!!!!!!!
			String cabecalhoPreMenu = "aaa";
			@SuppressWarnings("deprecation")
			String nomeServidor = StringUtils.capitaliseAllWords(getCadastrante().getNomePessoa().toLowerCase());
			String siglaLotacaoServidor = getCadastrante().getLotacao().getSigla();
			@SuppressWarnings("deprecation")
			String nomeServidorASubstituir = StringUtils.capitaliseAllWords(getTitular().getNomePessoa().toLowerCase());
			String cabecalhoaSerInserido = "<!-- utility box -->" + "<div class=\"gt-util-box\">" + "<div class=\"gt-util-box-inner\" style=\"padding-top: 10px; font-size: 100%;\">"
					+ "<p style=\"text-align: right;\">" + "Ol&aacute;, <strong>" + nomeServidor + " - " + siglaLotacaoServidor + "</strong> " + "<span class=\"gt-util-separator\">|</span> "
					+ "<a href=\"/siga/?GLO=true\">sair</a>" + "</p>";
			if (!(nomeServidorASubstituir.equals(nomeServidor) || nomeServidorASubstituir.isEmpty())) {
				cabecalhoaSerInserido = cabecalhoaSerInserido + "<p style=\"text-align: right; padding-top: 10px;\">" + "Substituindo: <strong>" + nomeServidorASubstituir + "</strong>"
						+ "<span class=\"gt-util-separator\">|</span>" + "<a href=\"/siga/substituicao/finalizar.action\">finalizar</a>" + "</p>";
			}
			cabecalhoaSerInserido = cabecalhoaSerInserido + "</div>" + "</div>";
			String novoCabecalho = cabecalhoPreMenu.replace("<!-- utility box -->", cabecalhoaSerInserido);
			result.include("_cabecalho_pre_menu", novoCabecalho);
			assertAcesso("");
		} catch (Exception e) {
			// tratarExcecoes(e);
		}
	}

	private void obterCabecalhoEUsuario(String backgroundColor) throws Exception {
		// TODO: conferir se esta OK
//		SigaHTTP http = new SigaHTTP();
//		
//		try {
//			String popup = param("popup");
//			if (popup == null || (!popup.equals("true") && !popup.equals("false")))
//				popup = "false";
//
//			String url = getBaseSiga() + "/pagina_vazia.action?popup=" + popup;
//			String paginaVazia = http.get(url, null, getRequest().getSession().getId());
//			if (!paginaVazia.contains("/sigaidp")) {
//				String[] pageText = paginaVazia.split("<!-- insert body -->");
//				String[] cabecalho = pageText[0].split("<!-- insert menu -->");
//
//				if (backgroundColor != null)
//					cabecalho[0] = cabecalho[0].replace("<html>", "<html style=\"background-color: " + backgroundColor + " !important;\">");
//
//				String[] cabecalho_pre = cabecalho[0].split("</head>");
//
//				String cabecalhoPreHead = cabecalho_pre[0];
//				String cabecalhoPreMenu = "</head>" + cabecalho_pre[1];
//				String cabecalhoPos = cabecalho.length > 1 ? cabecalho[1] : null;
//				String rodape = pageText[1];
//
//				if (cabecalhoPos == null) {
//					cabecalhoPos = cabecalhoPreMenu;
//					cabecalhoPreMenu = null;
//				}
//
//				result.include("_cabecalho_pre_head", cabecalhoPreHead);
//				result.include("_cabecalho_pre_menu", cabecalhoPreMenu);
//				result.include("_cabecalho_pos", cabecalhoPos);
//				result.include("_rodape", rodape);
//			}
//			
//			String user = SecurityContextAssociation.getPrincipal().getName();
//			Usuario usuario = new Usuario();
//			CpDao dao = CpDao.getInstance();
//			CpIdentidade id = dao.consultaIdentidadeCadastrante(user, true);
			// TODO: verificar necessidade
//			UsuarioAutenticado.carregarUsuario(id, usuario);

//			result.include("cadastrante", getCadastrante());
//			result.include("lotaCadastrante", getLotaCadastrante());
//			result.include("titular", getTitular());
//			result.include("lotaTitular", getLotaTitular());
//			result.include("identidadeCadastrante", getIdentidadeCadastrante());
//			 }

//		} catch (ArrayIndexOutOfBoundsException aioob) {
			// Edson: Quando as informações não puderam ser obtidas do Siga,
			// manda para a página de login. Se não for esse o erro, joga
			// exceção pra cima.
			// TODO Heidi Mudar!
			// redirect("/siga/redirect.action?uri=" + getRequest().getRequestURI());
//		}
	}

	/**
	 * Recupera na configuração do GI o complexo padrão para usuário logado verificando Órgao e Lotação e o tipo de configurção "Utilizar Complexo"
	 * 
	 * @return
	 * @throws Exception
	 */
	protected CpComplexo recuperarComplexoPadrao() throws Exception {
		return recuperarComplexoPadrao(getTitular());
	}

	protected CpComplexo recuperarComplexoPadrao(DpPessoa dpPessoa) throws Exception {
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf = CpRepository.findById(CpTipoConfiguracao.class, TIPO_CONFIG_COMPLEXO_PADRAO);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpRepository.findById(CpSituacaoConfiguracao.class, 1L);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPadrao = CpRepository.findById(CpSituacaoConfiguracao.class, 5L);
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// Recuperando Configuração Pode para uma lotação específica
		Object[] parametros = { dpPessoa.getLotacao().getIdLotacaoIni(), cpSituacaoConfiguracaoPode, dpPessoa.getOrgaoUsuario(), tpConf };
		configuracoes = CpRepository.find(CpConfiguracao.class, "((lotacao.idLotacaoIni = ? and cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )",
				parametros).fetch();
		if (configuracoes != null && configuracoes.size() > 0) {
			cpComplexo = configuracoes.get(0).getComplexo();
		} else {
			// Recuperando Configuração default para um Órgão específico
			Object[] parametros1 = { cpSituacaoConfiguracaoPadrao, dpPessoa.getOrgaoUsuario(), tpConf };
			configuracoes = CpRepository.find(CpConfiguracao.class, "((cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
			if (configuracoes != null && configuracoes.size() > 0) {
				cpComplexo = configuracoes.get(0).getComplexo();
			}
		}
		if (cpComplexo == null) {
			throw new Exception(Messages.get("cpComplexo.null.exception"));
		}
		return cpComplexo;
	}

	// TODO: adicionar tratamento de excecao generico em algum lugar
	public void tratarExcecoes(Exception e) {
		EntityManager em = ContextoPersistencia.em();

		if (getCadastrante() != null)
			LOGGER.error(MessageFormat.format("Erro Siga-TP; Pessoa: {0}; Lotação: {1}", getCadastrante().getSigla(), getLotaTitular().getSigla()), e);

		if (em != null && em.getTransaction() != null && em.getTransaction().isActive())
			em.getTransaction().rollback();
		e.printStackTrace();
		LOGGER.error(e.getMessage(), e);
	}

	protected String getBaseSiga() {
		return MessageFormat.format("http://{0}:{1}/siga", getRequest().getServerName(), String.valueOf(getRequest().getServerPort()));
	}
}