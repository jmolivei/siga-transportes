package controllers;

import java.io.File;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import play.data.Upload;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.model.Imagem;

@With(AutorizacaoGIAntigo.class)
public class Imagens extends Controller {
	
	@RoleAdmin
	@RoleAdminFrota
	@RoleAdminMissao
	public static void salvar(@Valid Imagem imagem) throws Exception {
    	validation.valid(imagem);
    	
    	@SuppressWarnings({ "unchecked", "static-access" })
		List<Upload> uploads  = (List<Upload>) request.current().args.get("__UPLOADS");
    	
    	if (!uploads.isEmpty()) {
    		Upload uploadFile = uploads.get(0);
        	File arquivo = new File(uploadFile.getFileName());
        	
        	if (imagem != null) {
        		if (uploadFile.asBytes().length > 5120) { 
        			Validation.addError("imagem", "imagens.uploadFile.validation");
        		}
        		
        		if (!new MimetypesFileTypeMap().getContentType(arquivo).startsWith("image")) {
        			Validation.addError("imagem", "imagens.mimetypesFileTypeMap.validation", new MimetypesFileTypeMap().getContentType(arquivo));
        		}
        		
        		imagem.blob =  uploadFile.asBytes(); 
        		imagem.nomeArquivo = uploadFile.getFieldName();
        	}
        }
    	else {
    		imagem.blob = null;
    	}
		
		//imagem.save();
    }

	public static void exibirImagem(Long id) {
		if (id != 0) {
			//Imagem imagem = Imagem.findById(id);
			//renderTemplate("@exibirImagem", imagem);
		}
	}

/*	@AutorizacaoGI.RoleAdmin
    public static void excluir(Long id) throws Exception  { 
        EntityTransaction tx = Imagem.em().getTransaction();  
		Imagem imagem = Imagem.findById(id);
		
		if (! tx.isActive()) {
			tx.begin();
		}

		try {
		    imagem.delete();    
			tx.commit();
			
		} catch(PersistenceException ex) {
			tx.rollback();
			if (ex.getCause().getCause().getMessage().contains("restrição de integridade")) {
				//listarComMensagem("Condutor j&aacute; possui afastamentos, plant&otilde;es, miss&otilde;es ou outros dados cadastrados.");
			}
			else {
				//listarComMensagem(ex.getMessage());
			}
		}
		catch(Exception ex) {
			tx.rollback();
			//listarComMensagem(ex.getMessage());
		}

		//listar();
	}
*/
}