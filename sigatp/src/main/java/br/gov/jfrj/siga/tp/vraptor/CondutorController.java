package br.gov.jfrj.siga.tp.vraptor;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class CondutorController extends TpController {
	
	public CondutorController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), localization, validator, so, em);
	}

	private static Logger logger = Logger.getLogger(CondutorController.class);
	
	@Path("/app/condutor/listar")
	public void lista() {
		result.include("condutores", getCondutores());
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@Path("/app/condutor/editar/{id}")
	public void edita(Long id) {
		try {
			Condutor condutor = Condutor.AR.findById(id);

			if (condutor.getDpPessoa() != null) {
				condutor.setDpPessoa(recuperaPessoa(condutor.getDpPessoa()));
			} else {
				condutor.setDpPessoa(new DpPessoa());
			}

			result.include("condutor", condutor);
			result.include("categoriaCNH", condutor.getCategoriaCNH().getDescricao());
			result.include("listCategorias", CategoriaCNH.values());
			result.include("imgArquivo", new ByteArrayInputStream(condutor.getConteudoimagemblob()));
			
			MenuMontador.instance(result).recuperarMenuCondutores(id, ItemMenu.DADOSCADASTRAIS);

		} catch (Exception e) {
			logger.error(e, e.getCause());
		}
	}
	

	@Path("/app/condutor/excluir")
	public void exclui(Long id) {
		System.out.println();
	}

	@Path("/app/condutor/incluir")
	public void inclui() {
		System.out.println();
	}

	private DpPessoa recuperaPessoa(DpPessoa dpPessoa) throws Exception {
		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null", 
				dpPessoa.getIdInicial()).first();
	}

	private List<Condutor> getCondutores() {
		try {
			List<Condutor> condutores = Condutor.listarTodos(getTitular().getOrgaoUsuario());
			return condutores;

		} catch (Exception ignore) {
			return null;
		}
	}
	
//	private void getImagem(Long id) {
//	if (id != null) {
//		// Pesquisar Imagem por id
//		// Imagem arq = Imagem.newInstance(file);
//		// renderBinary(new ByteArrayInputStream(arq.blob),
//		// arq.nomeArquivo);
//		Condutor condutor = Condutor.AR.findById(id);
//		renderBinary(new ByteArrayInputStream(condutor.conteudoimagemblob),	condutor.conteudoimagemblob.length);
//	}
//}S

	

	
	
//
//	public void salvar() {
//		
//	}
//
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//
//	private static final String ACTION_EDITAR = "@editar";
//	private static final String ACTION_INCLUIR = "@incluir";
//
//	public static void listar() throws Exception {
//		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
//		render(condutores);
//	}
//
//	public static void listarComMensagem(String mensagem) throws Exception {
//		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
//		Validation.addError("condutor", mensagem);
//		renderTemplate("@listar", condutores);
//	}
//
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
//	public static void excluir(Long id) throws Exception {
//		EntityTransaction tx = Condutor.em().getTransaction();
//		Condutor condutor = Condutor.findById(id);
//
//		if (!tx.isActive()) {
//			tx.begin();
//		}
//
//		try {
//			condutor.delete();
//			tx.commit();
//			listar();
//
//		} catch (PersistenceException ex) {
//			tx.rollback();
//			if (ex.getCause().getCause().getMessage().contains("restrição de integridade")) {
//				listarComMensagem("condutor.excluir.validation");
//			} else {
//				listarComMensagem(ex.getMessage());
//			}
//		} catch (Exception ex) {
//			tx.rollback();
//			listarComMensagem(ex.getMessage());
//		}
//
//		listar();
//	}
//
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
//	public static void editar(Long id) throws Exception {
//		Condutor condutor = Condutor.findById(id);
//
//		if (condutor.dpPessoa != null) {
//			condutor.dpPessoa = recuperaPessoa(condutor.dpPessoa);
//		} else {
//			condutor.dpPessoa = new DpPessoa();
//		}
//		MenuMontador.instance().RecuperarMenuCondutores(id, ItemMenu.DADOSCADASTRAIS);
//		render(condutor);
//	}
//
//	private static DpPessoa recuperaPessoa(DpPessoa dpPessoa) throws Exception {
//		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null",dpPessoa.getIdInicial()).first();
//	}
//
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
//	public static void incluir() {
//		Condutor condutor = new Condutor();
//		MenuMontador.instance().RecuperarMenuCondutores(new Long(0),ItemMenu.DADOSCADASTRAIS);
//		render(condutor);
//	}
//
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
//	public static void salvar(Condutor condutor) throws Exception {
//		validation.valid(condutor);
//
//		if (condutor.arquivo != null) {
//			if (!Imagem.tamanhoImagemAceito(condutor.arquivo.blob.length)) {
//				Validation.addError("imagem", "condutor.tamanhoImagemAceito.validation");
//			}
//
//			if (!condutor.arquivo.mime.startsWith("image")) { // && !condutor.arquivo.nomeArquivo.contains("pdf")) {
//				Validation.addError("imagem", "condutores.arquivoImagem.validation", condutor.arquivo.mime);
//			}
//			condutor.conteudoimagemblob = condutor.arquivo.blob;
//		} else {
//			if (condutor.situacaoImagem.equals("semimagem")) {
//				condutor.conteudoimagemblob = null;
//			}
//		}
//		
//		if (condutor.dpPessoa == null) {
//			Validation.addError("dpPessoa", "condutor.dppessoa.validation");
//		}
//
//		condutor.cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();
//
//		if (Validation.hasErrors()) {
//			if (condutor.dpPessoa != null) {
//				condutor.dpPessoa = recuperaPessoa(condutor.dpPessoa);
//			} else {
//				condutor.dpPessoa = new DpPessoa();
//			}
//			renderTemplate((condutor.id == 0 ? CondutorController.ACTION_INCLUIR : CondutorController.ACTION_EDITAR), condutor);
//		}
//
//		condutor.save();
//		listar();
//	}
//
//	public static void exibirDadosDpPessoa(DpPessoa pessoa) throws Exception {
//		render(recuperaPessoa(pessoa));
//	}
//
//	public static void getImagem(Long id) {
//		if (id != null) {
//			// Pesquisar Imagem por id
//			// Imagem arq = Imagem.newInstance(file);
//			// renderBinary(new ByteArrayInputStream(arq.blob),
//			// arq.nomeArquivo);
//			Condutor condutor = Condutor.findById(id);
//			renderBinary(new ByteArrayInputStream(condutor.conteudoimagemblob),	condutor.conteudoimagemblob.length);
//		}
//	}
//
//	public static void exibirImgArquivo(Long id) {
//		Condutor condutor = Condutor.findById(id);
//		renderText(condutor.conteudoimagemblob != null ? true : false);
//	}
//
//	public static void exibirImagem(Long id) {
//		/*
//		 * if (file != null) { Imagem arq = Imagem.newInstance(file);
//		 * renderTemplate("@exibirImagem", arq.blob); }
//		 */
//		Condutor condutor = Condutor.findById(id);
//		renderTemplate("@exibirImagem", condutor);
//	}
}