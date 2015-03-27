package controllers;

import java.util.List;

import br.gov.jfrj.siga.tp.model.AutoDeInfracao;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.TipoDeNotificacao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.MenuMontador;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;

@With(AutorizacaoGI.class)
public class AutosDeInfracao extends Controller {

	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.INFRACOES);
		render(autosDeInfracao, veiculo);
	}
	
	public static void listarPorCondutor(Long idCondutor) {
		Condutor condutor = Condutor.findById(idCondutor);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorCondutor(condutor);
		MenuMontador.instance().RecuperarMenuCondutores(idCondutor, ItemMenu.INFRACOES);
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
    	List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	AutoDeInfracao autoDeInfracao = new AutoDeInfracao();
    	TipoDeNotificacao tipoNotificacao = TipoDeNotificacao.valueOf(notificacao);
		render(autoDeInfracao, veiculos, condutores, tipoNotificacao);   
    }

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
 		AutoDeInfracao autoDeInfracao = AutoDeInfracao.findById(id);
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
        	List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
        	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
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
	public static void excluir(Long id) {
	   AutoDeInfracao autoDeInfracao = AutoDeInfracao.findById(id);
	   autoDeInfracao.delete();
	   listar();
    }
}