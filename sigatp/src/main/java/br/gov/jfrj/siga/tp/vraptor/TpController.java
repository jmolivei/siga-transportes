package br.gov.jfrj.siga.tp.vraptor;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.jboss.security.SecurityContextAssociation;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.acesso.UsuarioAutenticado;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.SigaHTTP;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.Usuario;
import br.gov.jfrj.siga.model.dao.HibernateUtil;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class TpController extends SigaController {

	public TpController(HttpServletRequest request, Result result, SigaObjects so, EntityManager em) {
		super(request, result, CpDao.getInstance(), so, em);
	}
	
	public void assertAcesso(String pathServico) throws AplicacaoException {
		so.assertAcesso("TP:Modulo de Transportes;" + pathServico);
	}

	protected static void prepararSessao() throws Exception {
		//TODO  Heidi verificar se está correto
		Session sessao = (Session)ContextoPersistencia.em();
		CpDao.freeInstance();
		HibernateUtil.setSessao(sessao);
		CpDao.getInstance(sessao);
		Cp.getInstance().getConf().limparCacheSeNecessario();
	}
	
	protected void obterCabecalhoEUsuario(String backgroundColor) throws Exception {
		SigaHTTP http = new SigaHTTP();
		
		try {
			//TODO  Heidi verificar necessidade
//			request = (request == null) ? Request.current() : request;
//			
//			getRequest().get
//			// Obter cabecalho e rodape do Siga
//			HashMap<String, String> atributos = new HashMap<String, String>();
//			for (Http.Header h : request.headers.values())
//	 			if (!h.name.equals("content-type"))
//					atributos.put(h.name, h.value());
			//params = (params == null) ? Params.current() : params;
			
			String popup = param("popup");
			if (popup == null || (!popup.equals("true") && !popup.equals("false")))
				popup = "false";

			String url = getBaseSiga() + "/pagina_vazia.action?popup=" + popup;
			String paginaVazia = http.get(url, null, getRequest().getSession().getId());
			if (!paginaVazia.contains("/sigaidp")){
				String[] pageText = paginaVazia.split("<!-- insert body -->");
				String[] cabecalho = pageText[0].split("<!-- insert menu -->");

				if (backgroundColor != null)
					cabecalho[0] = cabecalho[0].replace("<html>","<html style=\"background-color: " + backgroundColor	+ " !important;\">");

				String[] cabecalho_pre = cabecalho[0].split("</head>");

				String cabecalhoPreHead = cabecalho_pre[0];
				String cabecalhoPreMenu = "</head>" + cabecalho_pre[1];
				String cabecalhoPos = cabecalho.length > 1 ? cabecalho[1] : null;
				String rodape = pageText[1];

				if (cabecalhoPos == null) {
					cabecalhoPos = cabecalhoPreMenu;
					cabecalhoPreMenu = null; 
				}

				result.include("_cabecalho_pre_head", cabecalhoPreHead);
				result.include("_cabecalho_pre_menu", cabecalhoPreMenu);
				result.include("_cabecalho_pos", cabecalhoPos);
				result.include("_rodape", rodape);
			}
	
//			// TODO  Heidi verificar necessidade
//			if (play.Play.mode.isDev()) {
//				// Obter usuário logado
//				Logger.info("Play executando em modo DEV ..");
//				url = getBaseSiga().replace(":null", "");
//				url = url + "/usuario_autenticado.action?popup=" + popup + atributos;
//				String paginaAutenticada = SigaApplication.getUrl(url, atributos,null,null);
//				String[] IDs = paginaAutenticada.split(";");
//
//				DpPessoa dpPessoa = JPA.em().find(DpPessoa.class, Long.parseLong(IDs[0]));
//				result.include("cadastrante",dpPessoa);
//
//				if (IDs[1] != null && !IDs[1].equals("")) {
//					DpLotacao dpLotacao = JPA.em().find(DpLotacao.class, Long.parseLong(IDs[1]));
//					result.include("lotaCadastrante",dpLotacao);
//				}
//
//				if (IDs[2] != null && !IDs[2].equals("")) {
//					DpPessoa dpPessoaTitular = JPA.em().find(DpPessoa.class, Long.parseLong(IDs[2]));
//					result.include("titular",dpPessoaTitular);
//				}
//
//
//				if (IDs[3] != null && !IDs[3].equals("")) {
//					DpLotacao dpLotacaoTitular = JPA.em().find(DpLotacao.class, Long.parseLong(IDs[3]));
//					result.include("lotaTitular", dpLotacaoTitular);
//				}
//
//				if (IDs[4] != null && !IDs[4].equals("")) {
//					CpIdentidade identidadeCadastrante = JPA.em().find(CpIdentidade.class, Long.parseLong(IDs[4]));
//					result.include("identidadeCadastrante", identidadeCadastrante);
//				}
//
////				renderArgs.put("currentTimeMillis", new Date().getTime());
//				result.include("currentTimeMillis", new Date().getTime());
//			} else {
				
			//	Logger.info("Play executando em modo PROD ..");	
			
			// Obter usuario logado
			String user = SecurityContextAssociation.getPrincipal().getName();
			Usuario usuario = new Usuario();
			CpDao dao = CpDao.getInstance();
			CpIdentidade id = dao.consultaIdentidadeCadastrante(user, true);
			UsuarioAutenticado.carregarUsuario(id, usuario);
			
			result.include("cadastrante", usuario.getCadastrante());
			result.include("lotaCadastrante", usuario.getLotaTitular());
			result.include("titular", usuario.getTitular());
			result.include("lotaTitular", usuario.getLotaTitular());
			result.include("identidadeCadastrante", usuario.getIdentidadeCadastrante());

			result.include("currentTimeMillis", new Date().getTime());

		} catch (ArrayIndexOutOfBoundsException aioob) {
			// Edson: Quando as informações não puderam ser obtidas do Siga,
			// manda para a página de login. Se não for esse o erro, joga
			// exceção pra cima.
			// TODO  Heidi Mudar!
//			redirect("/siga/redirect.action?uri=" + getRequest().getRequestURI());
		}
	}
	
	String getBaseSiga() {
		return "http://" + getRequest().getServerName() + ":" + getRequest().getServerPort() +"/siga";
	}

}