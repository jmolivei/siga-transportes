package controllers;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Imagem;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class Condutores extends Controller {
	private static final String ACTION_EDITAR = "@editar";
	private static final String ACTION_INCLUIR = "@incluir";

	public static void listar() throws Exception {
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		render(condutores);
	}

	public static void listarComMensagem(String mensagem) throws Exception {
		List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
		Validation.addError("condutor", mensagem);
		renderTemplate("@listar", condutores);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
		EntityTransaction tx = Condutor.AR.em().getTransaction();
		Condutor condutor = Condutor.AR.findById(id);

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
		Condutor condutor = Condutor.AR.findById(id);

		if (condutor.getDpPessoa() != null) {
			condutor.setDpPessoa(recuperaPessoa(condutor.getDpPessoa()));
		} else {
			condutor.setDpPessoa(new DpPessoa());
		}
		MenuMontador.instance().recuperarMenuCondutores(id, ItemMenu.DADOSCADASTRAIS);
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
		MenuMontador.instance().recuperarMenuCondutores(new Long(0),ItemMenu.DADOSCADASTRAIS);
		render(condutor);
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(Condutor condutor) throws Exception {
		validation.valid(condutor);

		if (condutor.getArquivo() != null) {
			if (!Imagem.tamanhoImagemAceito(condutor.getArquivo().blob.length)) {
				Validation.addError("imagem", "condutor.tamanhoImagemAceito.validation");
			}

			if (!condutor.getArquivo().mime.startsWith("image")) { // && !condutor.arquivo.nomeArquivo.contains("pdf")) {
				Validation.addError("imagem", "condutores.arquivoImagem.validation", condutor.getArquivo().mime);
			}
			condutor.setConteudoimagemblob(condutor.getArquivo().blob);
		} else {
			if (condutor.getSituacaoImagem().equals("semimagem")) {
				condutor.setConteudoimagemblob(null);
			}
		}
		
		if (condutor.getDpPessoa() == null) {
			Validation.addError("dpPessoa", "condutor.dppessoa.validation");
		}

		condutor.setCpOrgaoUsuario(AutorizacaoGIAntigo.titular().getOrgaoUsuario());

		if (Validation.hasErrors()) {
			if (condutor.getDpPessoa() != null) {
				condutor.setDpPessoa(recuperaPessoa(condutor.getDpPessoa()));
			} else {
				condutor.setDpPessoa(new DpPessoa());
			}
			renderTemplate((condutor.getId() == 0 ? Condutores.ACTION_INCLUIR : Condutores.ACTION_EDITAR), condutor);
		}

		condutor.save();
		listar();
	}

	public static void exibirDadosDpPessoa(DpPessoa pessoa) throws Exception {
		render(recuperaPessoa(pessoa));
	}

	public static void getImagem(Long id) throws Exception {
		if (id != null) {
			// Pesquisar Imagem por id
			// Imagem arq = Imagem.newInstance(file);
			// renderBinary(new ByteArrayInputStream(arq.blob),
			// arq.nomeArquivo);
			Condutor condutor = Condutor.AR.findById(id);
			renderBinary(new ByteArrayInputStream(condutor.getConteudoimagemblob()), condutor.getConteudoimagemblob().length);
		}
	}

	public static void exibirImgArquivo(Long id) throws Exception {
		Condutor condutor = Condutor.AR.findById(id);
		renderText(condutor.getConteudoimagemblob() != null ? true : false);
	}

	public static void exibirImagem(Long id) throws Exception {
		/*s
		 * if (file != null) { Imagem arq = Imagem.newInstance(file);
		 * renderTemplate("@exibirImagem", arq.blob); }
		 */
		Condutor condutor = Condutor.AR.findById(id);
		renderTemplate("@exibirImagem", condutor);
	}
}