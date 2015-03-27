package controllers;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.CpRepository;
import br.gov.jfrj.siga.tp.model.Imagem;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;

@With(AutorizacaoGI.class)
public class Condutores extends Controller {
	private static final String ACTION_EDITAR = "@editar";
	private static final String ACTION_INCLUIR = "@incluir";

	public static void listar() throws Exception {
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		render(condutores);
	}

	public static void listarComMensagem(String mensagem) throws Exception {
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
		Validation.addError("condutor", mensagem);
		renderTemplate("@listar", condutores);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
		EntityTransaction tx = Condutor.em().getTransaction();
		Condutor condutor = Condutor.findById(id);

		if (!tx.isActive()) {
			tx.begin();
		}

		try {
			condutor.delete();
			tx.commit();
			listar();

		} catch (PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("restrição de integridade")) {
				listarComMensagem("condutor.excluir.validation");
			} else {
				listarComMensagem(ex.getMessage());
			}
		} catch (Exception ex) {
			tx.rollback();
			listarComMensagem(ex.getMessage());
		}

		listar();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) throws Exception {
		Condutor condutor = Condutor.findById(id);

		if (condutor.dpPessoa != null) {
			condutor.dpPessoa = recuperaPessoa(condutor.dpPessoa);
		} else {
			condutor.dpPessoa = new DpPessoa();
		}
		MenuMontador.instance().RecuperarMenuCondutores(id, ItemMenu.DADOSCADASTRAIS);
		render(condutor);
	}

	private static DpPessoa recuperaPessoa(DpPessoa dpPessoa) throws Exception {
		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null",dpPessoa.getIdInicial()).first();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir() {
		Condutor condutor = new Condutor();
		MenuMontador.instance().RecuperarMenuCondutores(new Long(0),ItemMenu.DADOSCADASTRAIS);
		render(condutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(Condutor condutor) throws Exception {
		validation.valid(condutor);

		if (condutor.arquivo != null) {
			if (!Imagem.tamanhoImagemAceito(condutor.arquivo.blob.length)) {
				Validation.addError("imagem", "condutor.tamanhoImagemAceito.validation");
			}

			if (!condutor.arquivo.mime.startsWith("image")) { // && !condutor.arquivo.nomeArquivo.contains("pdf")) {
				Validation.addError("imagem", "condutores.arquivoImagem.validation", condutor.arquivo.mime);
			}
			condutor.conteudoimagemblob = condutor.arquivo.blob;
		} else {
			if (condutor.situacaoImagem.equals("semimagem")) {
				condutor.conteudoimagemblob = null;
			}
		}
		
		if (condutor.dpPessoa == null) {
			Validation.addError("dpPessoa", "condutor.dppessoa.validation");
		}

		condutor.cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();

		if (Validation.hasErrors()) {
			if (condutor.dpPessoa != null) {
				condutor.dpPessoa = recuperaPessoa(condutor.dpPessoa);
			} else {
				condutor.dpPessoa = new DpPessoa();
			}
			renderTemplate((condutor.id == 0 ? Condutores.ACTION_INCLUIR : Condutores.ACTION_EDITAR), condutor);
		}

		condutor.save();
		listar();
	}

	public static void exibirDadosDpPessoa(DpPessoa pessoa) throws Exception {
		render(recuperaPessoa(pessoa));
	}

	public static void getImagem(Long id) {
		if (id != null) {
			// Pesquisar Imagem por id
			// Imagem arq = Imagem.newInstance(file);
			// renderBinary(new ByteArrayInputStream(arq.blob),
			// arq.nomeArquivo);
			Condutor condutor = Condutor.findById(id);
			renderBinary(new ByteArrayInputStream(condutor.conteudoimagemblob),	condutor.conteudoimagemblob.length);
		}
	}

	public static void exibirImgArquivo(Long id) {
		Condutor condutor = Condutor.findById(id);
		renderText(condutor.conteudoimagemblob != null ? true : false);
	}

	public static void exibirImagem(Long id) {
		/*
		 * if (file != null) { Imagem arq = Imagem.newInstance(file);
		 * renderTemplate("@exibirImagem", arq.blob); }
		 */
		Condutor condutor = Condutor.findById(id);
		renderTemplate("@exibirImagem", condutor);
	}
}