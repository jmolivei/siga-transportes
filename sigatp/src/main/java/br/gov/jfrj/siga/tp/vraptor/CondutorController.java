package br.gov.jfrj.siga.tp.vraptor;

import java.io.ByteArrayInputStream;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Imagem;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/condutor")
public class CondutorController extends TpController {
	
	public CondutorController(HttpServletRequest request, Result result, CpDao dao, Localization localization, Validator validator, SigaObjects so, EntityManager em) throws Exception {
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	private static Logger logger = Logger.getLogger(CondutorController.class);
	
	@Path("/listar")
	public void listar() throws Exception {
		result.include("condutores", getCondutores());
	}

//	@Path("/app/condutor/listarComMensagem")
//	public void listarComMensagem(String mensagem) throws Exception {
//		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
//		Validation.addError("condutor", mensagem);
//		renderTemplate("@listar", condutores);
//	}
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/editar/{id}")
	public void editar(Long id) throws Exception {
		Condutor condutor;
		if(id > 0)  {
			condutor = Condutor.AR.findById(id);
			if (condutor.getDpPessoa() != null) 
				condutor.setDpPessoa(recuperaPessoa(condutor.getDpPessoa()));
			else 
				condutor.setDpPessoa(new DpPessoa());
			
			result.include("categoriaCNH", condutor.getCategoriaCNH().getDescricao());
			
			if(condutor.getConteudoimagemblob() != null)
				result.include("imgArquivo", new ByteArrayInputStream(condutor.getConteudoimagemblob()));
			
		} else 
			condutor = new Condutor();

		MenuMontador.instance(result).recuperarMenuCondutores(id, ItemMenu.DADOSCADASTRAIS);
		result.include("listCategorias", CategoriaCNH.values());
		result.include("condutor", condutor);
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/salvar")
	public void salvar(@Valid Condutor condutor) throws Exception {
		condutor.setDpPessoa(DpPessoa.AR.findById(condutor.getDpPessoa().getId()));
		if (condutor.getArquivo() != null) {
			error(!Imagem.tamanhoImagemAceito(condutor.getArquivo().blob.length), "imagem", "condutor.tamanhoImagemAceito.validation");
			// && !condutor.arquivo.nomeArquivo.contains("pdf")) {
			error(!condutor.getArquivo().mime.startsWith("image"), "imagem", "condutores.arquivoImagem.validation");
			condutor.setConteudoimagemblob(condutor.getArquivo().blob);
		} else {
			//TODO  HD verificar!
//			if (condutor.getSituacaoImagem().equals("semimagem")) 
//				condutor.setConteudoimagemblob(null);
		}
		
		error(condutor.getDpPessoa() == null, "dpPessoa", "condutor.dppessoa.validation");
		
		condutor.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());

		if (validator.hasErrors()) {
			if (condutor.getDpPessoa() != null) 
				condutor.setDpPessoa(recuperaPessoa(condutor.getDpPessoa()));
			else 
				condutor.setDpPessoa(new DpPessoa());
			
			result.include("condutor", condutor);
			if(condutor.getId() > 0) 
				result.forwardTo(this).editar(condutor.getId());
			else
				result.forwardTo(this).incluir();
		}

		condutor.save();
		result.forwardTo(this).listar();
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/excluir/{id}")
	public void excluir(Long id) throws Exception {
		EntityTransaction tx = Condutor.AR.em().getTransaction();
		Condutor condutor = Condutor.AR.findById(id);

		if (!tx.isActive()) 
			tx.begin();

		try {
			condutor.delete();
			tx.commit();
			result.forwardTo(this).listar();
		} catch (PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("restrição de integridade")) {
				//TODO  HD mensagem!
//				listarComMensagem("condutor.excluir.validation");
//				result.forwardTo(this).listar();
				validator.add(new I18nMessage("condutor", "condutor.excluir.validation"));
			} else {
				//TODO  HD mensagem!
//				listarComMensagem(ex.getMessage());
				validator.add(new ValidationMessage(ex.getMessage(), "condutor"));
			}
			validator.onErrorUse(Results.logic()).forwardTo(CondutorController.class).listar();
		} catch (Exception ex) {
			tx.rollback();
			//TODO  HD mensagem!
//			listarComMensagem(ex.getMessage());
			validator.add(new ValidationMessage(ex.getMessage(), "condutor"));
			validator.onErrorUse(Results.logic()).forwardTo(CondutorController.class).listar();
		}
		
		result.forwardTo(this).listar();
	}

//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/incluir")
	public void incluir() throws Exception {
		result.forwardTo(this).editar(0L);
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
	
	//TODO  HD public page?
	public void getImagem(Long id) throws Exception {
		if (id != null) {
			// Pesquisar Imagem por id
			// Imagem arq = Imagem.newInstance(file);
			// renderBinary(new ByteArrayInputStream(arq.blob),
			// arq.nomeArquivo);
			Condutor condutor = Condutor.AR.findById(id);
			result.include("conteudoImagemBlob" ,new ByteArrayInputStream(condutor.getConteudoimagemblob()));
			result.include("conteudoImagemBlobTamanho" , condutor.getConteudoimagemblob().length);
		}
	}
	
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/exibirDadosDpPessoa/{idPessoa}")
	public void exibirDadosDpPessoa(Long idPessoa) throws Exception {
		DpPessoa pessoa = DpPessoa.AR.findById(idPessoa);
		result.include("pessoa", pessoa);
	}
	

}