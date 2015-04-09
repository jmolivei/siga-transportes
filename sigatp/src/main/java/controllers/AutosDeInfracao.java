package controllers;

import java.util.List;

import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.model.AutoDeInfracao;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.TipoDeNotificacao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;

@With(AutorizacaoGIAntigo.class)
public class AutosDeInfracao extends Controller {

	public static void listarPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorVeiculo(veiculo);
		MenuMontador.instance().recuperarMenuVeiculos(idVeiculo, ItemMenu.INFRACOES);
		render(autosDeInfracao, veiculo);
	}
	
	public static void listarPorCondutor(Long idCondutor) throws Exception {
		Condutor condutor = Condutor.AR.findById(idCondutor);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorCondutor(condutor);
		MenuMontador.instance().recuperarMenuCondutores(idCondutor, ItemMenu.INFRACOES);
		render(autosDeInfracao, condutor);
	}
	
	public static void listar() {
  		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.listarOrdenado();
  		render(autosDeInfracao);
    }
		
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir(String notificacao) throws Exception {
    	List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
    	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
    	AutoDeInfracao autoDeInfracao = new AutoDeInfracao();
    	TipoDeNotificacao tipoNotificacao = TipoDeNotificacao.valueOf(notificacao);
		render(autoDeInfracao, veiculos, condutores, tipoNotificacao);   
    }

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
    	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
 		AutoDeInfracao autoDeInfracao = AutoDeInfracao.AR.findById(id);
 		TipoDeNotificacao tipoNotificacao = autoDeInfracao.codigoDaAutuacao != null ? TipoDeNotificacao.AUTUACAO : TipoDeNotificacao.PENALIDADE;
		render(autoDeInfracao, veiculos, condutores, tipoNotificacao);		
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(@Valid AutoDeInfracao autoDeInfracao) throws Exception{
 		TipoDeNotificacao tipoNotificacao = autoDeInfracao.codigoDaAutuacao != null ? TipoDeNotificacao.AUTUACAO : TipoDeNotificacao.PENALIDADE;

        if (autoDeInfracao.dataDePagamento != null && autoDeInfracao.dataPosteriorDataCorrente(autoDeInfracao.dataDePagamento)) {
			Validation.addError("dataPagamento", "autosDeInfracao.dataDePagamento.validation");
        }
        
		if(Validation.hasErrors()){
        	List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
        	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGIAntigo.titular().getOrgaoUsuario());
        	String template = autoDeInfracao.id > 0 ? "AutosDeInfracao/editar.html" : "AutosDeInfracao/incluir.html";
            renderTemplate(template, autoDeInfracao, veiculos, condutores, tipoNotificacao);
        }
        else{
        	autoDeInfracao.save();
        	listar();
        }
    }              

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) throws Exception {
	   AutoDeInfracao autoDeInfracao = AutoDeInfracao.AR.findById(id);
	   autoDeInfracao.delete();
	   listar();
    }
}