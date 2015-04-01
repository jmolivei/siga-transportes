package br.gov.jfrj.siga.tp.vraptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class AutorizacaoGIController extends TpController implements Interceptor {

	public AutorizacaoGIController(HttpServletRequest request, Result result,
			CpDao dao, SigaObjects so, EntityManager em) {
		super(request, result, dao, so, em);
	}

	private static final String EXIBIR_MENU_AGENTE = "exibirMenuAgente";
	private static final String EXIBIR_MENU_ADMIN_GABINETE = "exibirMenuAdminGabinete";
	private static final String EXIBIR_MENU_APROVADOR = "exibirMenuAprovador";
	private static final String EXIBIR_MENU_ADMINISTRAR_MISSAO = "exibirMenuAdministrarMissao";
	private static final String EXIBIR_MENU_ADMINISTRAR_FROTA = "exibirMenuAdministrarFrota";
	private static final String EXIBIR_MENU_ADMINISTRAR = "exibirMenuAdministrar";
	private static final String EXIBIR_MENU_GABINETE = "exibirMenuGabinete";
	
	private static final String AGN_AGENTE = "AGN:Agente";
	private static final String ADMGAB_ADMIN_GABINETE = "ADMGAB:AdminGabinete";
	private static final String APR_APROVADOR = "APR:Aprovador";
	private static final String ADMMISSAO_ADMINISTRAR_MISSAO = "ADMMISSAO:AdministrarMissao";
	private static final String ADMFROTA_ADMINISTRAR_FROTA = "ADMFROTA:AdministrarFrota";
	private static final String ADM_ADMINISTRAR = "ADM:Administrar";
	private static final String GAB_GABINETE = "GAB:Gabinete";
	
	private static final Logger LOGGER = Logger.getLogger(AutorizacaoGIController.class);


	// @Path("app/main")
	public static void addDefaultsAlways() throws Exception {
		// TODO  Heidi verificar necessidade
//		Logger.info("Configuracao : " + Play.id);
		prepararSessao();
		Cp.getInstance().getConf().limparCacheSeNecessario();
	}

	// TODO  Heidi anotation
//	@Before(priority=51)
	public void addDefaults() throws Exception {

		try {
			obterCabecalhoEUsuario("rgb(235, 235, 232)");
			// TODO  Heidi renderArgs
			// Incluindo Cadastrante e titulart novamente no cabecalho
//			String cabecalhoPreMenu = (String) RenderArgs.current().get(
//					"_cabecalho_pre_menu");
			//TODO  Heidi Mudarr!!!!!!!!
			String cabecalhoPreMenu = "aaa";
			@SuppressWarnings("deprecation")
			String nomeServidor = StringUtils.capitaliseAllWords(getCadastrante()
					.getNomePessoa().toLowerCase());
			String siglaLotacaoServidor = getCadastrante().getLotacao().getSigla();
			@SuppressWarnings("deprecation")
			String nomeServidorASubstituir = StringUtils
					.capitaliseAllWords(getTitular().getNomePessoa().toLowerCase());
			String cabecalhoaSerInserido = "<!-- utility box -->"
					+ "<div class=\"gt-util-box\">"
					+ "<div class=\"gt-util-box-inner\" style=\"padding-top: 10px; font-size: 100%;\">"
					+ "<p style=\"text-align: right;\">"

					+ "Ol&aacute;, <strong>" + nomeServidor + " - "
					+ siglaLotacaoServidor + "</strong> "
					+ "<span class=\"gt-util-separator\">|</span> "
					+ "<a href=\"/siga/?GLO=true\">sair</a>" + "</p>";
			if (!(nomeServidorASubstituir.equals(nomeServidor) || nomeServidorASubstituir
					.isEmpty())) {
				cabecalhoaSerInserido = cabecalhoaSerInserido
						+ "<p style=\"text-align: right; padding-top: 10px;\">"
						+ "Substituindo: <strong>"
						+ nomeServidorASubstituir
						+ "</strong>"
						+ "<span class=\"gt-util-separator\">|</span>"
						+ "<a href=\"/siga/substituicao/finalizar.action\">finalizar</a>"
						+ "</p>";
			}

			cabecalhoaSerInserido = cabecalhoaSerInserido + "</div>" + "</div>";
			String novoCabecalho = cabecalhoPreMenu.replace(
					"<!-- utility box -->", cabecalhoaSerInserido);
			result.include("_cabecalho_pre_menu", novoCabecalho);
			assertAcesso("");
		} catch (Exception e) {
			tratarExcecoes(e);
		}
		
		result.include(EXIBIR_MENU_ADMINISTRAR, verificaPermissaoMenu(ADM_ADMINISTRAR));
		result.include(EXIBIR_MENU_ADMINISTRAR_FROTA, verificaPermissaoMenu(ADMFROTA_ADMINISTRAR_FROTA));
		result.include(EXIBIR_MENU_ADMINISTRAR_MISSAO, verificaPermissaoMenu(ADMMISSAO_ADMINISTRAR_MISSAO));
		result.include(EXIBIR_MENU_APROVADOR, verificaPermissaoMenu(APR_APROVADOR));
		result.include(EXIBIR_MENU_GABINETE, verificaPermissaoMenu(GAB_GABINETE));
		result.include(EXIBIR_MENU_ADMIN_GABINETE, verificaPermissaoMenu(ADMGAB_ADMIN_GABINETE));
		result.include(EXIBIR_MENU_AGENTE, verificaPermissaoMenu(AGN_AGENTE));

		try {
			// assertAcesso("ADMMISSAOCOMPLEXO:AdministrarMissaoporComplexo");
			CpComplexo cpComplexoAdministrador = recuperarComplexoAdministrador();
			if (cpComplexoAdministrador != null) {
				result.include("exibirMenuAdministrarMissaoComplexo", true);
				result.include("exibirMenuAdministrarMissaoComplexo", true);
			} else {
				result.include("exibirMenuAdministrarMissaoComplexo", false);
			}
		} catch (Exception e) {
			result.include("exibirMenuAdministrarMissaoComplexo", false);
		}
	}

	private boolean verificaPermissaoMenu(String contexto) {
		boolean exibirMenu = false;
		try {
			assertAcesso(contexto);
			exibirMenu = true;
		} catch (Exception e) {
			exibirMenu = false;
		}
		return exibirMenu;
	}

	/**
	 * Recupera na configuração do GI o complexo padrão para usuário logado
	 * verificando Órgao e Lotação e o tipo de configurção "Utilizar Complexo"
	 * 
	 * @return
	 * @throws Exception
	 */
	protected CpComplexo recuperarComplexoPadrao() throws Exception {
		return recuperarComplexoPadrao(getTitular());
	}

	public static CpComplexo recuperarComplexoPadrao(DpPessoa dpPessoa)
			throws Exception {
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf = CpRepository.findById(
				CpTipoConfiguracao.class, TIPO_CONFIG_COMPLEXO_PADRAO);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpRepository
				.findById(CpSituacaoConfiguracao.class, 1L);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPadrao = CpRepository
				.findById(CpSituacaoConfiguracao.class, 5L);
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// Recuperando Configuração Pode para uma lotação específica
		Object[] parametros = { dpPessoa.getLotacao().getIdLotacaoIni(),
				cpSituacaoConfiguracaoPode, dpPessoa.getOrgaoUsuario(), tpConf };
		configuracoes = CpRepository
				.find(CpConfiguracao.class,
						"((lotacao.idLotacaoIni = ? and cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )",
						parametros).fetch();
		if (configuracoes != null && configuracoes.size() > 0) {
			cpComplexo = configuracoes.get(0).getComplexo();
		} else {
			// Recuperando Configuração default para um Órgão específico
			Object[] parametros1 = { cpSituacaoConfiguracaoPadrao,
					dpPessoa.getOrgaoUsuario(), tpConf };
			configuracoes = CpRepository
					.find(CpConfiguracao.class,
							"((cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )",
							parametros1).fetch();
			if (configuracoes != null && configuracoes.size() > 0)
				cpComplexo = configuracoes.get(0).getComplexo();

		}

		if (cpComplexo == null)
			throw new Exception("cpComplexo.null.exception");
		// TODO  Heidi Message Mudar!
//		throw new Exception(Messages.get("cpComplexo.null.exception"));

		return cpComplexo;
	}

	/**
	 * Recupera na configuração do GI o complexo do perfil
	 * AdministradorPorComplexo para usuário logado verificando Órgao e Lotação
	 * e o tipo de configurção "Utilizar Complexo"
	 * 
	 * @return
	 * @throws Exception
	 */
	protected CpComplexo recuperarComplexoAdministrador()
			throws Exception {
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = CpRepository.find(CpServico.class,
				"siglaServico", SERVICO_COMPLEXO_ADMINISTRADOR).first();
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = CpRepository
				.findById(CpSituacaoConfiguracao.class, 1L);
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// and dtHistDtFim IS NOT NULL
		Object[] parametros = { getTitular().getIdPessoaIni(),
				cpSituacaoConfiguracaoPode, cpServico };
		configuracoes = CpRepository
				.find(CpConfiguracao.class,
						"(dpPessoa.idPessoaIni = ? and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )",
						parametros).fetch();
		if (configuracoes != null)
			cpComplexo = configuracoes.get(0).getComplexo();

		return cpComplexo;
	}

//	@Catch()
	public void tratarExcecoes(Exception e) {
		EntityManager em = ContextoPersistencia.em();
		if (getCadastrante() != null)
			LOGGER.error("Erro Siga-TP; Pessoa: " + getCadastrante().getSigla()
					+ "; Lotação: " + getLotaTitular().getSigla(), e);
		if (em != null && em.getTransaction() != null
				&& em.getTransaction().isActive())
			em.getTransaction().rollback();
		e.printStackTrace();
		LOGGER.error(e.getMessage(), e);
	}

//	@Before(priority = 100)
	private void checkAuthorization(ResourceMethod method) {
		boolean adminAnnotation = method.containsAnnotation(RoleAdmin.class);
		boolean aprovadorAnnotation = method.containsAnnotation(RoleAprovador.class);
		boolean gabineteAnnotation = method.containsAnnotation(RoleGabinete.class);
		boolean adminGabineteAnnotation = method.containsAnnotation(RoleAdminGabinete.class);
		boolean agenteAnnotation = method.containsAnnotation(RoleAgente.class);
		boolean admFrotaAnnotation = method.containsAnnotation(RoleAdminFrota.class);
		boolean admMissaoAnnotation = method.containsAnnotation(RoleAdminMissao.class);
		boolean admMissaoComplexoAnnotation = method.containsAnnotation(RoleAdminMissaoComplexo.class);

		if (adminAnnotation) {
			if (!ehAdministrador() && !aprovadorAnnotation
					&& !gabineteAnnotation
					&& !adminGabineteAnnotation
					&& !agenteAnnotation && !admFrotaAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("adminAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("adminAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAdministrador())
				adminAnnotation = false;
		}

		if (aprovadorAnnotation) {
			if (!ehAprovador() && !adminAnnotation
					&& !gabineteAnnotation
					&& !adminGabineteAnnotation
					&& !agenteAnnotation && !admFrotaAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("aprovadorAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("aprovadorAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAprovador())
				aprovadorAnnotation = false;
		}

		if (agenteAnnotation) {
			if (!ehAgente() && !adminAnnotation
					&& !gabineteAnnotation
					&& !adminGabineteAnnotation 
					&& !aprovadorAnnotation
					&& !admFrotaAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("agenteAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("agenteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAgente())
				agenteAnnotation = false;
		}

		if (gabineteAnnotation) {
			if (!ehGabinete() && !adminAnnotation
					&& !aprovadorAnnotation
					&& !adminGabineteAnnotation
					&& !agenteAnnotation && !admFrotaAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("gabineteAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("gabineteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehGabinete())
				gabineteAnnotation = false;
		}

		if (adminGabineteAnnotation) {
			if (!ehAdminGabinete() && !adminAnnotation
					&& !gabineteAnnotation
					&& !aprovadorAnnotation && !agenteAnnotation
					&& !admFrotaAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("adminGabineteAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("adminGabineteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAdminGabinete())
				adminGabineteAnnotation = false;
		}

		if (admFrotaAnnotation) {
			if (!ehAdministradorFrota() && !adminAnnotation 
					&& !gabineteAnnotation
					&& !aprovadorAnnotation && !agenteAnnotation
					&& !adminGabineteAnnotation
					&& !admMissaoAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("admFrotaAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("admFrotaAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAdministradorFrota())
				admFrotaAnnotation = false;
		}

		if (admMissaoAnnotation) {
			if (!ehAdministradorMissao() && !adminAnnotation
					&& !gabineteAnnotation
					&& !aprovadorAnnotation && !agenteAnnotation
					&& !adminGabineteAnnotation
					&& !admFrotaAnnotation
					&& !admMissaoComplexoAnnotation) {
				try {
					throw new Exception("admMissaoAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("admMissaoAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAdministradorMissao())
				admMissaoAnnotation = false;
		}

		if (admMissaoComplexoAnnotation) {
			if (!ehAdministradorMissaoPorComplexo() && !adminAnnotation
					&& !gabineteAnnotation
					&& !aprovadorAnnotation && !agenteAnnotation
					&& !adminGabineteAnnotation 
					&& !admFrotaAnnotation
					&& !admMissaoAnnotation) {
				try {
					throw new Exception("admMissaoComplexoAnnotation.exception");
					// TODO  Heidi Message Mudar!
//					throw new Exception(Messages.get("admMissaoComplexoAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}

			if (!ehAdministradorMissaoPorComplexo())
				admMissaoComplexoAnnotation = false;
		}
	}

	/**
	 * Funciona junto a tag MotivoLog nos formulários Abastecimentos/listar e
	 * ControlesGabinete/listar, método de exclusão. Necessário incluir uma tag
	 * <form> com id="formulario". Incluir também a tag #{motivoLog /} antes de
	 * </form>
	 */
//	@Before(priority = 100)
	void motivoLog() throws Exception {
		// TODO  Heidi mudar!
//		LogMotivo motivoLogAnnotation = getActionAnnotation(LogMotivo.class);
//		if (motivoLogAnnotation != null) {
//			String motivoLog = param("motivoLog");
//			if (motivoLog == null) {
//				throw new Exception("motivoLogAnnotation.exception");
//				// TODO  Heidi Message Mudar!
////				throw new Exception(Messages.get("motivoLogAnnotation.exception"));
//			}
//			result.include("motivoLog", motivoLog);
//		}
	}

	// TODO  Heidi renderArgs SUBSTITUIRRRR!
	public static Boolean ehAdministrador() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_ADMINISTRAR);
		return true;
	}

	public static Boolean ehAdministradorFrota() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_ADMINISTRAR_FROTA);
		return true;
	}

	public static Boolean ehAdministradorMissao() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_ADMINISTRAR_MISSAO);
		return true;
	}

	public static Boolean ehAdministradorMissaoPorComplexo() {
//		return (Boolean) renderArgs.get("exibirMenuAdministrarMissaoComplexo");
		return true;
	}

	public static Boolean ehAprovador() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_APROVADOR);
		return true;
	}

	public static Boolean ehGabinete() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_GABINETE);
		return true;
	}

	public static Boolean ehAdminGabinete() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_ADMIN_GABINETE);
		return true;
	}

	public static Boolean ehAgente() {
//		return (Boolean) renderArgs.get(EXIBIR_MENU_AGENTE);
		return true;
	}

	public static String getMotivoLog() {
//		return (String) renderArgs.get("motivoLog");
		return "teste";
	}

	public static CpComplexo getComplexoAdministrado() {
//		return (CpComplexo) renderArgs.get("cpComplexoAdministrador");
		return new CpComplexo();
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return false;
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAdmin {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAdminFrota {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAdminMissao {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAprovador {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleGabinete {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAdminGabinete {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAgente {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RoleAdminMissaoComplexo {
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface LogMotivo {
	}
}
