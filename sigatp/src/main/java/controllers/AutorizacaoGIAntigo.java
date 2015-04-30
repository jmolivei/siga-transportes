package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Scope.RenderArgs;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.annotation.LogMotivo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminGabinete;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAgente;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAprovador;
import br.gov.jfrj.siga.tp.auth.annotation.RoleGabinete;
import br.gov.jfrj.siga.tp.model.TpDao;


public class AutorizacaoGIAntigo extends SigaApplication {
	
	@Before(priority=50)
	public static void addDefaultsAlways() throws Exception {
		Logger.info("Configuracao : " + Play.id);
		prepararSessao();
		Cp
		.getInstance()
		.getConf().limparCacheSeNecessario();
	}

	@Before(priority=51)
	public static void addDefaults() throws Exception {

		try {
			obterCabecalhoEUsuario("rgb(235, 235, 232)");
			// Incluindo Cadastrante e titulart novamente no cabecalho
			String cabecalhoPreMenu = (String) RenderArgs.current().get("_cabecalho_pre_menu");
			@SuppressWarnings("deprecation")
			String nomeServidor = StringUtils.capitaliseAllWords(AutorizacaoGIAntigo.cadastrante().getNomePessoa().toLowerCase());
			String siglaLotacaoServidor = AutorizacaoGIAntigo.cadastrante().getLotacao().getSigla();
			@SuppressWarnings("deprecation")
			String nomeServidorASubstituir = StringUtils.capitaliseAllWords(AutorizacaoGIAntigo.titular().getNomePessoa().toLowerCase());
			String cabecalhoaSerInserido = 
					"<!-- utility box -->"
						+ "<div class=\"gt-util-box\">"
							+ "<div class=\"gt-util-box-inner\" style=\"padding-top: 10px; font-size: 100%;\">"
								+ "<p style=\"text-align: right;\">"
							      
									+ "Ol&aacute;, <strong>" + nomeServidor + " - " + siglaLotacaoServidor + "</strong> "
									+ "<span class=\"gt-util-separator\">|</span> "
									+ "<a href=\"/siga/?GLO=true\">sair</a>"
								+ "</p>";
									if(!(nomeServidorASubstituir.equals(nomeServidor) || nomeServidorASubstituir.isEmpty())) {
										cabecalhoaSerInserido = cabecalhoaSerInserido + "<p style=\"text-align: right; padding-top: 10px;\">"
												+ "Substituindo: <strong>" + nomeServidorASubstituir + "</strong>"
												+ "<span class=\"gt-util-separator\">|</span>"
												+ "<a href=\"/siga/substituicao/finalizar.action\">finalizar</a>"
											+ "</p>";
									}
								
			cabecalhoaSerInserido = cabecalhoaSerInserido + "</div>"
						+ "</div>";
			String novoCabecalho =  cabecalhoPreMenu.replace("<!-- utility box -->", cabecalhoaSerInserido);
			RenderArgs.current().put("_cabecalho_pre_menu",novoCabecalho);
			assertAcesso("");
		} catch (Exception e) {
			tratarExcecoes(e);
		}

		try {
			assertAcesso("ADM:Administrar");
			renderArgs.put("exibirMenuAdministrar", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAdministrar", false);
		}

		try {
			assertAcesso("ADMFROTA:AdministrarFrota");
			renderArgs.put("exibirMenuAdministrarFrota", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAdministrarFrota", false);
		}

		try {
			assertAcesso("ADMMISSAO:AdministrarMissao");
			renderArgs.put("exibirMenuAdministrarMissao", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAdministrarMissao", false);
		}	

		try {
		//	assertAcesso("ADMMISSAOCOMPLEXO:AdministrarMissaoporComplexo");
			CpComplexo cpComplexoAdministrador = recuperarComplexoAdministrador();
			if (cpComplexoAdministrador != null) {
				renderArgs.put("exibirMenuAdministrarMissaoComplexo", true);
				renderArgs.put("cpComplexoAdministrador", cpComplexoAdministrador);
			} else {
				renderArgs.put("exibirMenuAdministrarMissaoComplexo", false);
			}
		} catch (Exception e) {
			renderArgs.put("exibirMenuAdministrarMissaoComplexo", false);
		}		

		try {
			assertAcesso("APR:Aprovador");
			renderArgs.put("exibirMenuAprovador", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAprovador", false);
		}

		try {
			assertAcesso("GAB:Gabinete");
			renderArgs.put("exibirMenuGabinete", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuGabinete", false);
		}

		try {
			assertAcesso("ADMGAB:AdminGabinete");
			renderArgs.put("exibirMenuAdminGabinete", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAdminGabinete", false);
		}

		try {
			assertAcesso("AGN:Agente");
			renderArgs.put("exibirMenuAgente", true);
		} catch (Exception e) {
			renderArgs.put("exibirMenuAgente", false);
		}

	}

	protected static void assertAcesso(String path) throws Exception {
		SigaApplication.assertAcesso("TP:Modulo de Transportes;" + path);
	}


	/**
	 * Recupera na configura��o do GI o complexo padr�o para usu�rio logado verificando �rgao e Lota��o 
	 * e o tipo de configur��o "Utilizar Complexo"
	 * @return
	 * @throws Exception
	 */
	protected static CpComplexo recuperarComplexoPadrao() throws Exception {
		return recuperarComplexoPadrao(AutorizacaoGIAntigo.titular());
	}   
	
	public static CpComplexo recuperarComplexoPadrao(DpPessoa dpPessoa) throws Exception {
		long TIPO_CONFIG_COMPLEXO_PADRAO = 400;
		CpTipoConfiguracao tpConf = TpDao.findById(CpTipoConfiguracao.class, TIPO_CONFIG_COMPLEXO_PADRAO);
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = TpDao.findById(CpSituacaoConfiguracao.class, 1L); 
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPadrao = TpDao.findById(CpSituacaoConfiguracao.class, 5L); 
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// Recuperando Configura��o Pode para uma lota��o espec�fica
		Object[] parametros =  {dpPessoa.getLotacao().getIdLotacaoIni(), cpSituacaoConfiguracaoPode, dpPessoa.getOrgaoUsuario(),tpConf};
		configuracoes = TpDao.find(CpConfiguracao.class, "((lotacao.idLotacaoIni = ? and cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros).fetch();
		if (configuracoes != null && configuracoes.size() > 0) {
			cpComplexo = configuracoes.get(0).getComplexo();
		} else {
		
		// Recuperando Configura��o default para um �rg�o espec�fico
		Object[] parametros1 =  {cpSituacaoConfiguracaoPadrao, dpPessoa.getOrgaoUsuario(),tpConf};
		configuracoes = TpDao.find(CpConfiguracao.class, "((cpSituacaoConfiguracao = ?) and orgaoUsuario = ?  and cpTipoConfiguracao = ? and hisIdcFim is null  )", parametros1).fetch();
		if (configuracoes != null && configuracoes.size() > 0) {
			cpComplexo = configuracoes.get(0).getComplexo();
		}
		}
		if (cpComplexo == null) {
			throw new Exception(Messages.get("cpComplexo.null.exception"));
		}
		
		return cpComplexo;
	}


	
	/**
	 * Recupera na configura��o do GI o complexo do perfil AdministradorPorComplexo para usu�rio logado verificando �rgao e Lota��o 
	 * e o tipo de configur��o "Utilizar Complexo"
	 * @return
	 * @throws Exception
	 */
	protected static CpComplexo recuperarComplexoAdministrador() throws Exception {
		String SERVICO_COMPLEXO_ADMINISTRADOR = "SIGA-TP-ADMMISSAOCOMPLEXO";
		CpServico cpServico = TpDao.find(CpServico.class, "siglaServico",SERVICO_COMPLEXO_ADMINISTRADOR).first();
		CpSituacaoConfiguracao cpSituacaoConfiguracaoPode = TpDao.findById(CpSituacaoConfiguracao.class, 1L); 
		List<CpConfiguracao> configuracoes = null;
		CpComplexo cpComplexo = null;

		// and dtHistDtFim IS NOT NULL
		Object[] parametros =  {AutorizacaoGIAntigo.titular().getIdPessoaIni(),cpSituacaoConfiguracaoPode, cpServico};
		configuracoes = TpDao.find(CpConfiguracao.class, "(dpPessoa.idPessoaIni = ? and cpSituacaoConfiguracao = ? and cpServico = ? and hisIdcFim is null )", parametros).fetch();
		if (configuracoes != null) {
			cpComplexo = configuracoes.get(0).getComplexo();
		}
		return cpComplexo;
	}   

	@Catch()
	public static void tratarExcecoes(Exception  e) {

		if (cadastrante() != null)
			Logger.error("Erro Siga-TP; Pessoa: " + cadastrante().getSigla()
					+ "; Lota��o: " + lotaTitular().getSigla(), e);
		if (JPA.em() != null && JPA.em().getTransaction() != null && JPA.em().getTransaction().isActive() )
			JPA.em().getTransaction().rollback();
		e.printStackTrace();
		error(e.getMessage());
	}

	@Before(priority=100)
	static void checkAuthorization() {
		RoleAdmin adminAnnotation = getActionAnnotation(RoleAdmin.class);
		RoleAprovador aprovadorAnnotation = getActionAnnotation(RoleAprovador.class);
		RoleGabinete gabineteAnnotation = getActionAnnotation(RoleGabinete.class);
		RoleAdminGabinete adminGabineteAnnotation = getActionAnnotation(RoleAdminGabinete.class);
		RoleAgente agenteAnnotation = getActionAnnotation(RoleAgente.class);		
		RoleAdminFrota admFrotaAnnotation = getActionAnnotation(RoleAdminFrota.class);
		RoleAdminMissao admMissaoAnnotation = getActionAnnotation(RoleAdminMissao.class);
		RoleAdminMissaoComplexo admMissaoComplexoAnnotation = getActionAnnotation(RoleAdminMissaoComplexo.class);
		
		if(adminAnnotation != null) {
			if (! ehAdministrador() && aprovadorAnnotation == null && 
					gabineteAnnotation==null && adminGabineteAnnotation==null &&
					agenteAnnotation==null && admFrotaAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("adminAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAdministrador()) {
				adminAnnotation = null;
			}
		}


		if(aprovadorAnnotation != null) {
			if (! ehAprovador() && adminAnnotation == null && 
					gabineteAnnotation==null && adminGabineteAnnotation==null &&
					agenteAnnotation==null && admFrotaAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("aprovadorAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAprovador()) {
				aprovadorAnnotation = null;
			}
		}

		if(agenteAnnotation != null) {
			if (! ehAgente() && adminAnnotation == null && 
					gabineteAnnotation==null && adminGabineteAnnotation==null &&
					aprovadorAnnotation==null && admFrotaAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("agenteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAgente()) {
				agenteAnnotation = null;
			}
		}		

		if(gabineteAnnotation != null) {
			if (! ehGabinete() && adminAnnotation == null && 
					aprovadorAnnotation==null && adminGabineteAnnotation==null &&
					agenteAnnotation==null && admFrotaAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null)  {
				try {
					throw new Exception(Messages.get("gabineteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehGabinete()) {
				gabineteAnnotation = null;
			}
		}

		if(adminGabineteAnnotation != null) {
			if (! ehAdminGabinete() && adminAnnotation == null && 
					gabineteAnnotation==null && aprovadorAnnotation==null &&
					agenteAnnotation==null && admFrotaAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("adminGabineteAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAdminGabinete()) {
				adminGabineteAnnotation = null;
			}
		}

		if(admFrotaAnnotation != null) {
			if (! ehAdministradorFrota() && adminAnnotation == null && 
					gabineteAnnotation==null && aprovadorAnnotation==null &&
					agenteAnnotation==null && adminGabineteAnnotation==null &&
					admMissaoAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("admFrotaAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAdministradorFrota()) {
				admFrotaAnnotation = null;
			}
		}

		if(admMissaoAnnotation != null) {
			if (! ehAdministradorMissao() && adminAnnotation == null && 
					gabineteAnnotation==null && aprovadorAnnotation==null &&
					agenteAnnotation==null && adminGabineteAnnotation==null &&
					admFrotaAnnotation==null && admMissaoComplexoAnnotation==null) {
				try {
					throw new Exception(Messages.get("admMissaoAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAdministradorMissao()) {
				admMissaoAnnotation = null;
			}
		}
		
		if(admMissaoComplexoAnnotation != null) {
			if (! ehAdministradorMissaoPorComplexo() && adminAnnotation == null && 
					gabineteAnnotation==null && aprovadorAnnotation==null &&
					agenteAnnotation==null && adminGabineteAnnotation==null &&
					admFrotaAnnotation==null && admMissaoAnnotation==null) {
				try {
					throw new Exception(Messages.get("admMissaoComplexoAnnotation.exception"));
				} catch (Exception e) {
					tratarExcecoes(e);	
				}

			}

			if (! ehAdministradorMissaoPorComplexo()) {
				admMissaoComplexoAnnotation = null;
			}
		}
	}

	/**
	 *  Funciona junto a tag MotivoLog nos formul�rios Abastecimentos/listar e
	 *  ControlesGabinete/listar, metodo de exclus�o.
	 *  Necess�rio incluir uma tag <form> com id="formulario".
	 *  Incluir tamb�m a tag #{motivoLog /} antes de </form>
	 */
	@Before(priority=100)
	static void motivoLog() throws Exception {
		LogMotivo motivoLogAnnotation = getActionAnnotation(LogMotivo.class);

		if (motivoLogAnnotation != null) {
			String motivoLog = params.get("motivoLog");
			if(motivoLog == null) {
				throw new Exception(Messages.get("motivoLogAnnotation.exception"));
			}
			renderArgs.put("motivoLog", motivoLog);
		}
	}

	public static Boolean ehAdministrador() {
		return (Boolean) renderArgs.get("exibirMenuAdministrar");
	} 

	public static Boolean ehAdministradorFrota() {
		return (Boolean) renderArgs.get("exibirMenuAdministrarFrota");
	} 

	public static Boolean ehAdministradorMissao() {
		return (Boolean) renderArgs.get("exibirMenuAdministrarMissao");
	} 	
	
	public static Boolean ehAdministradorMissaoPorComplexo() {
		return (Boolean) renderArgs.get("exibirMenuAdministrarMissaoComplexo");
	} 

	public static Boolean ehAprovador() {
		return (Boolean) renderArgs.get("exibirMenuAprovador");
	}

	public static Boolean ehGabinete() {
		return (Boolean) renderArgs.get("exibirMenuGabinete");
	}

	public static Boolean ehAdminGabinete() {
		return (Boolean) renderArgs.get("exibirMenuAdminGabinete");
	}

	public static Boolean ehAgente() {
		return (Boolean) renderArgs.get("exibirMenuAgente");
	}

	public static String getMotivoLog() {
		return (String) renderArgs.get("motivoLog");
	}
	
	public static CpComplexo getComplexoAdministrado() {
		return (CpComplexo) renderArgs.get("cpComplexoAdministrador");
	}
}